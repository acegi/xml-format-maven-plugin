package org.technicalsoftwareconfigurationmanagement.maven.plugin;

import java.io.InputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.Document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.codehaus.plexus.util.DirectoryScanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * The XML Formatter is a plugin that is designed to be run
 * from the parent POM of a project, so that all XML files within the
 * project can be formatting using one formatting option (either spaces
 * or tabs).  This is due to the fact that when a big project is being
 * worked on by many different people, with each person using their own 
 * preferred formatting style, the files become hard to read.
 * 
 *
 * <p>The plugin contains two arrays in which you can specify which
 * files to include/exclude from the formatting. <strong> By default all XML
 * files are included, except those in the target folder.</strong>
 *
 * <p>To use this plugin, type <strong>one</strong> of the following at the command line:
 * <UL>
 *   <LI>mvn org.technicalsoftwareconfigurationmanagement.maven-plugin:tscm-maven-plugin:2.1-SNAPSHOT:xmlFormatter
 *   <LI>mvn org.technicalsoftwareconfigurationmanagement.maven-plugin:tscm-maven-plugin:xmlFormatter
 *   <LI>mvn tscm:xmlFormatter
 * </UL>
 *
 * <p>To format the files using tabs instead of spaces, add this onto the end of one of the above commands.
 * <UL>
 *   <LI>-DxmlFormatter.useTabs="true"
 *
 * <p>Developer's Note:  At the moment the code is setup to only work with
 * Java 1.6 or newer because of the use of transformations (JAXP which
 * was included in Java in version 1.6).
 *
 * @goal xmlFormatter
 **/
public class XmlFormatter extends AbstractMojo {

    /**
     * Since parent pom execution causes multiple executions we 
     * created this static map that contains the fully qualified file
     * names for all of the files we process and we check it before
     * processing a file and skip if we have already processed the file.
     * note that the execution method is called numerous times within
     * the same JVM (hence the static reference works but a local
     * reference might not work).
     **/
    private static Set<String> processedFileNames = new HashSet<String>();

    /**
     * Called automatically by each module in the project, including the parent
     * module.  All files will formatted with either <i>spaces</i> or <i>tabs</i>,
     * and will be written back to it's original location.
     *
     * @throws MojoExecutionException
     **/
    public void execute() throws MojoExecutionException {

        if ((baseDirectory != null) 
	    && (getLog().isDebugEnabled())) {
            getLog().debug("[xml formatter] Base Directory:" + baseDirectory);
        }

        if (includes != null) {
            String[] filesToFormat = getIncludedFiles(baseDirectory, includes, excludes);
	    
            if (getLog().isDebugEnabled()) {
                getLog().debug("[xml formatter] Format " 
			       + filesToFormat.length 
			       + " source files in " 
			       + baseDirectory);
            }

            for (String include : filesToFormat) {
		try {

		    if (!processedFileNames.contains(baseDirectory + File.separator + include)) {
			processedFileNames.add(baseDirectory + File.separator + include);
			format(new File(baseDirectory + File.separator + include));
		    }
		} catch(RuntimeException re) {
		    getLog().error("File <" + baseDirectory + File.separator + include + "> failed to parse, skipping and moving on to the next file", re);
		}
            }
        }
    }

    /**
     * A flag used to tell the program to format with either <i>spaces</i>
     * or <i>tabs</i>.  By default, the formatter uses spaces.
     *
     * <UL>
     *   <LI><tt>true</tt> - tabs</LI>
     *   <LI><tt>false</tt> - spaces</LI>
     * <UL>
     *
     * <p>In configure this parameter to use tabs, use the following
     * at the command line:
     *     -DxmlFormatter.useTabs="true"
     *
     * @parameter expression="${xmlFormatter.useTabs}"
     *            default-value="false"
     **/
    private boolean useTabs;

    /**
     * The base directory of the project.
     * @parameter expression="${basedir}"
     **/
    private File baseDirectory;

    /**
     * A set of file patterns that dictates which files should be
     * included in the formatting with each file pattern being relative 
     * to the base directory.  <i>By default all xml files are included.</i>
     * This parameter is most easily configured in the parent pom file.
     * @parameter alias="includes"
     **/
    private String[] includes = {"**/*.xml"};

    /**
     * A set of file patterns that allow you to exclude certain
     * files/folders from the formatting.  <i>By default the target folder
     * is excluded from the formatting.</i>  This parameter is most easily
     * configured in the parent pom file.
     * @parameter alias="excludes"
     **/
    private String[] excludes = {"**/target/**"};

    /**
     * By default we have setup the exclude list to remove the target 
     * folders. Setting any value including an empty array will
     * overide this functionality.  This parameter can be configured in the
     * POM file using the 'excludes' alias in the configuration option. Note
     * that all files are relative to the parent POM.
     *
     * @param excludes - String array of patterns or filenames to exclude
     *        from formatting.
     **/
    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    /**
     * By default all XML files ending with .xml are included for formatting.
     * This parameter can be configured in the POM file using the 'includes' 
     * alias in the configuration option.  Note that all files are
     * relative to the parent POM.
     *
     * @param includes - Default "**\/*.xml". Assigning a new value overrides
     *        the default settings.
     **/
    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    /**
     * Scans the given directory for files to format, and returns them in an
     * array.  The files are only added to the array if they match a pattern
     * in the <tt>includes</tt> array, and <strong>do not</strong> match any
     * pattern in the <tt>excludes</tt> array.
     *
     * @param directory - Base directory from which we start scanning for files.
     *        Note that this must be the root directory of the project in order
     *        to obtain the pom.xml as part of the XML files. This is one other
     *        differentiator when we were looking for tools, anything we found
     *        remotely like this did not start at the root directory.
     * @param includes - A string array containing patterns that are used to
     *                   search for files that should be formatted.
     * @param excludes - A string array containing patterns that are used to
     *                   filter out files so that they are <strong>not</strong> 
     *                   formatted.
     * @return - A string array containing all the files that should be 
     *            formatted.
     **/
    public String[] getIncludedFiles(File directory, 
				     String[] includes, 
				     String[] excludes) {

        DirectoryScanner dirScanner = new DirectoryScanner();
        dirScanner.setBasedir(directory);
        dirScanner.setIncludes(includes);
        dirScanner.setExcludes(excludes);
        dirScanner.scan();

        String[] filesToFormat = dirScanner.getIncludedFiles();      

        if (getLog().isDebugEnabled()) {
			
	    if (useTabs) {
		getLog().debug("[xml formatter] Formatting with tabs...");
	    } else {
		getLog().debug("[xml formatter] Formatting with spaces...");
	    }

            getLog().debug("[xml formatter] Files:");
            for (String file : filesToFormat) {
                getLog().debug("[xml formatter] file<" + file 
			       + "> is scheduled for formatting");
            }
        }

        return filesToFormat;
    } 


    /**
     * Formats the provided file, writing it back to it's original location.
     * @param file - File to be formatted. The output file is the same as
     *        the input file. Please be sure that you have your files in
     *        a revision control system (and saved before running this plugin).
     **/
    public void format(File formatFile) {

        if (formatFile.exists() && formatFile.isFile()) {

            InputStream inputStream = null;
            Document xml = null;

            try {
                inputStream = new FileInputStream(formatFile);

                if (inputStream == null) {
                    getLog().error("[xml formatter] File<" + formatFile + "> could not be opened, skipping");
                    return;
                }

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                xml = documentBuilder.parse(inputStream);

		getLog().info("Successfully formatted file: " + formatFile);

            } catch(Throwable t) {
                throw new RuntimeException("[xml formatter] Failed to parse..." + t.getMessage(), t);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch(Throwable tr) {
                        // intentially exception hiding for failures on close....
                    }
                }
            }

            FileOutputStream fos = null;
            String formattedXml = null;
            InputStream stylesheet = null;
            try {

                // Read the stylesheet from the classpath
                stylesheet = new XmlFormatter().getClass().getClassLoader()
                    .getResourceAsStream("remove-whitespace.xsl");

                if (stylesheet == null) {
                    getLog().error("[xml formatter] Could not find remove-whitespace.xsl");
                    return;
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer(new StreamSource(stylesheet));
                fos = new FileOutputStream(formatFile);
                StreamResult streamResult = new StreamResult(fos);
                DOMSource domSource = new DOMSource(xml);
                transformer.transform(domSource, streamResult);

            } catch(Throwable t) {
                throw new RuntimeException("[xml formatter] Failed to parse..." + t.getMessage(), t);
            } finally {
                if (stylesheet != null) {
                    try {
                        stylesheet.close();
                    } catch(Throwable tr) {
                        // intentially exception hiding for failures on close....
                    }
                }

                if (fos != null) {
                    try {
                        fos.close();
                    } catch(Throwable t) {
                        // intentially exception hiding for failures on close....
                    }
                }
            }

            // Now that we know that the indent is set to four spaces, we can either
            // keep it like that or change them to tabs depending on which 'mode' we
            // are in.
            
            if (useTabs) {
                indentFile(formatFile);	
            }
        } else {
            getLog().debug("[xml formatter] File was not valid:" + formatFile + " skipping");
        }
    }

    /**
     * Indents the file using tabs, writing it back to its original location.  This method
     * is only called if useTabs is set to true.
     * @param file
     * 			The file to be indented using tabs.
     **/
    private void indentFile(File file) {

        List<String> temp = new ArrayList<String>();  // a temporary list to hold the lines
        BufferedReader reader = null;
        BufferedWriter writer = null;

        // Read the file, and replace the four spaces with tabs.
        try {
            reader= new BufferedReader(new FileReader(file));
            String line = null;

            while ((line = reader.readLine()) != null) {
                temp.add(line.replaceAll("[\\s]{4}", "\t"));
            }

            writer = new BufferedWriter(new FileWriter(file));

            for (String ln : temp) {
                writer.write(ln);
                writer.newLine();
            }
        } catch (Throwable t) {
            throw new RuntimeException("[xml formatter] Failed to read file..." + t.getMessage(), t);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Throwable t) {
                    // Intentionally catching exception...
                }
            }

            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (Throwable t) {
                    // Intentionally catching exception...
                }
            }
        }
    }
}
