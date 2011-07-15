package org.technicalsoftwareconfigurationmanagement.maven.plugin;

import java.io.InputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter; // can be removed??
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.codehaus.plexus.util.DirectoryScanner;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * A little while ago our project's XML files were out of control and I didn't
 * want to have to open each one in our favorite IDE and use that tool to format
 * them because it was just too time consuming. I began searching for a tool like
 * Jalopy that would format the xml but couldn't find one. Noting that the code
 * is rather easy for just standard XML formatting.  After searching
 * the Internet for 30 minutes with no luck, we decided to write our own tool.
 *
 * The XML formatter has been designed to ingest any xml files to include
 * the project pom.xml files and format them... This is very useful for
 * parent poms to run recursively.
 *
 * By default the XML formatter uses the includes all xml files and an empty
 * exclude pattern.
 *
 * Developer's Note: At the moment this code is setup to only work with
 * Java 1.6 or newer because of the use of transformations (JAXP which
 * was included in Java in version 1.6 not 1.5).
 *
 * @goal xmlFormatter
 **/
public class XmlFormatter extends AbstractMojo {

	/**
	 * This method is called automatically, it sends each file that it
	 * finds to the formatter.  The file will be written to its original
	 * location, and will contain either spaces or tabs depending on
	 * what indentMode is set to.
	 **/
    public void execute() throws MojoExecutionException {

        if ((baseDirectory != null) 
	    && (getLog().isInfoEnabled())) {
            getLog().info("[xml formatter] Base Directory:" + baseDirectory);
        }

        if (includes != null) {
            String[] filesToFormat = getIncludedFiles(baseDirectory, includes, excludes);
	    
		    if (getLog().isInfoEnabled()) {
			getLog().info("[xml formatter] Format " 
				      + filesToFormat.length 
				      + " source files in " 
			    	  + baseDirectory);
		    }

            for (String include : filesToFormat) {
                format(new File(baseDirectory + File.separator + include));
            }
        }
    }

	/**
	 * A constant used to set the indent mode to use tabs.
	 **/
	private static final String TABS_MODE = "tabs";

	/**
	 * A constant used to set the indent mode to use spaces.
	 **/
	private static final String SPACES_MODE = "spaces";

	/**
	 * The type of character to use when indenting the elements.
	 * Defaults to SPACES_MODE.
	 **/
	private String indentMode = SPACES_MODE;

    /**
     * Base directory of the project
     * @parameter expression="${basedir}"
     **/
    private File baseDirectory;

    /**
     * A set of file patterns to include in the formatting with
     * each file pattern being relative to the base directory.
     * @parameter alias="includes"
     **/
    private String[] includes = {"**/*.xml"};

    /**
     * A set of file patterns to exclude in the formatting
     * @parameter alias="excludes"
     **/
    private String[] excludes = {"**/target/**"};

    /**
     * By default we have setup the exclude list to remove the target 
     * folders. Setting any value including an empty array will
     * overide this functionality.
     *
     * @param excludes - String array of patterns or file names to exclude
     *        from formatting.
     **/
    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    /**
     * By default all XML files ending with .xml are included for formatting.
     *
     * @param includes - Default "**\/*.xml". Assigning a new value overrides
     *        the default settings.
     **/
    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

	/**
	 * Valid values for this are 'tabs' and 'spaces', which correspond to
	 * the static variables TABS_MODE and SPACES_MODE respectively.
	 *
	 * @param indentMode - Should be set to 'tabs' or 'spaces' all other values
	 * 		  will be ignored and will instead default to 'spaces'.
	 **/
	public void setIndentMode(String indentMode) {
		this.indentMode = indentMode;
	}

    /**
     * Return a string array of files to format.
     *
     * @param directory - Base directory from which we start scanning for files.
     *        note that this must be the root directory of the project in order
     *        to obtain the pom.xml as part of the xml files. This is one other
     *        differentiator when we were looking for tools, anything we found
     *        remotely like this did not start at the root directory.
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

		if (getLog().isInfoEnabled()) {
		    getLog().info("Files:");
	    	for (String file : filesToFormat) {
			getLog().info("file<" + file 
				       + "> is scheduled for formatting");
	    	}
		}

        return filesToFormat;
    } 


    /**
     * Format the provided file, writing it back to the input location.
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
				    getLog().error("File<" + formatFile + "> could not be opened, skipping");
				    return;
				}

                xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);

		    } catch(Throwable t) {
                throw new RuntimeException("Failed to parse..." + t.getMessage(), t);
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
				    getLog().error("Could not find remove-whitespace.xsl");
				    return;
				}

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer(new StreamSource(stylesheet));
				fos = new FileOutputStream(formatFile);
				StreamResult streamResult = new StreamResult(fos);
				DOMSource domSource = new DOMSource(xml);
				transformer.transform(domSource, streamResult);

            } catch(Throwable t) {
                throw new RuntimeException("Failed to parse..." + t.getMessage(), t);
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
			if (indentMode.equals(TABS_MODE)) {
				indentFile(formatFile);	
			}
        } else {
            getLog().info("File was not valid:" + formatFile + " skipping");
        }
    }

	/**
	 * Indent the file using tabs, writing it back to its original location.  This method
	 * is only called if indentMode is set to TABS_MODE.
	 * @param file
	 * 			The file to be indented with tabs.
	 **/
	public void indentFile(File file) {

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
			throw new RuntimeException("Failed to read file..." + t.getMessage(), t);
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

				}
			}
		}
	}
}
