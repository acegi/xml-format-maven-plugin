package au.com.acegi.xmlformat;

import static au.com.acegi.xmlformat.FormatUtil.formatInPlace;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import static org.dom4j.io.OutputFormat.createPrettyPrint;

/**
 * Finds the XML files in a project and automatically reformats them.
 */
@Mojo(name = "xml-format")
public final class XmlFormatPlugin extends AbstractMojo {

  /**
   * Quote character to use when writing attributes.
   */
  @Parameter(property = "attributeQuoteChar", defaultValue = "\"")
  @SuppressWarnings("FieldMayBeFinal")
  private char attributeQuoteChar = '"';

  /**
   * The encoding format
   */
  @Parameter(property = "encoding", defaultValue = "UTF-8")
  @SuppressWarnings("FieldMayBeFinal")
  private String encoding = "UTF-8";

  /**
   * Whether or not to expand empty elements to &lt;tagName&gt;&lt;/tagName&gt;.
   */
  @Parameter(property = "expandEmptyElements", defaultValue = "false")
  private boolean expandEmptyElements;

  /**
   * Indicates the number of spaces to apply when indenting.
   */
  @Parameter(property = "indentSize", defaultValue = "2")
  private int indentSize;

  /**
   * New line separator
   */
  @Parameter(property = "lineSeparator", defaultValue = "\n")
  @SuppressWarnings("FieldMayBeFinal")
  private String lineSeparator = "\n";

  /**
   * Whether or not to print new line after the XML declaration.
   */
  @Parameter(property = "newLineAfterDeclaration", defaultValue = "false")
  private boolean newLineAfterDeclaration;

  /**
   * Controls when to output a line.separator every so many tags in case of no
   * lines and total text trimming.
   */
  @Parameter(property = "newLineAfterNTags", defaultValue = "0")
  private int newLineAfterNTags;

  /**
   * The default new line flag, set to do new lines only as in original document
   */
  @Parameter(property = "newlines", defaultValue = "true")
  private boolean newlines;

  /**
   * Whether or not to output the encoding in the XML declaration.
   */
  @Parameter(property = "omitEncoding", defaultValue = "false")
  private boolean omitEncoding;

  /**
   * Pad string-element boundaries with whitespace.
   */
  @Parameter(property = "padText", defaultValue = "false")
  private boolean padText;

  /**
   * Whether or not to suppress the XML declaration.
   */
  @Parameter(property = "suppressDeclaration", defaultValue = "false")
  private boolean suppressDeclaration;

  /**
   * Should we preserve whitespace or not in text nodes.
   */
  @Parameter(property = "trimText", defaultValue = "true")
  private boolean trimText;
  /**
   * Whether or not to use XHTML standard.
   */
  @Parameter(property = "xhtml", defaultValue = "false")
  private boolean xhtml;
  /**
   * The base directory of the project.
   */
  @Parameter(defaultValue = ".", readonly = true, required = true, property
             = "project.basedir")
  File baseDirectory;
  /**
   * A set of file patterns that allow you to exclude certain files/folders from
   * the formatting. In addition to these exclusions, the project build
   * directory (typically <code>target</code>) is always excluded.
   */
  @Parameter(property = "excludes", defaultValue = "")
  String[] excludes;
  /**
   * A set of file patterns that dictate which files should be included in the
   * formatting with each file pattern being relative to the base directory.
   */
  @Parameter(property = "includes", defaultValue = "**/*.xml")
  String[] includes;
  /**
   * The project target directory. This is always excluded from formatting.
   */
  @Parameter(defaultValue = "${project.build.directory}", readonly = true,
             required = true)
  File targetDirectory;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    assert baseDirectory != null;
    assert targetDirectory != null;
    assert includes != null && includes.length > 0;
    assert excludes != null;

    final OutputFormat fmt = buildFormatter();

    boolean success = true;
    for (final String inputName : find()) {
      final File input = new File(baseDirectory, inputName);
      final boolean changed;
      try {
        changed = formatInPlace(input, fmt);
      } catch (DocumentException | IOException ex) {
        success = false;
        getLog().error("[xml-format] Error for " + input, ex);
        continue;
      }
      if (!getLog().isDebugEnabled()) {
        continue;
      }
      final String msg = changed ? "Formatted" : "Unchanged";
      getLog().debug("[xml-format] " + msg + ": " + input);
    }

    if (!success) {
      throw new MojoFailureException("[xml-format] Failed)");
    }
  }

  private OutputFormat buildFormatter() {
    final OutputFormat fmt = createPrettyPrint();
    fmt.setAttributeQuoteCharacter(attributeQuoteChar);
    fmt.setEncoding(encoding);
    fmt.setExpandEmptyElements(expandEmptyElements);
    fmt.setIndentSize(indentSize);
    fmt.setLineSeparator(lineSeparator);
    fmt.setNewLineAfterDeclaration(newLineAfterDeclaration);
    fmt.setNewLineAfterNTags(newLineAfterNTags);
    fmt.setNewlines(newlines);
    fmt.setOmitEncoding(omitEncoding);
    fmt.setPadText(padText);
    fmt.setSuppressDeclaration(suppressDeclaration);
    fmt.setTrimText(trimText);
    fmt.setXHTML(xhtml);
    return fmt;
  }

  private String[] find() {
    DirectoryScanner dirScanner = new DirectoryScanner();
    dirScanner.setBasedir(baseDirectory);
    dirScanner.setIncludes(includes);

    final List<String> exclude = new ArrayList<>(asList(excludes));
    if (baseDirectory.equals(targetDirectory.getParentFile())) {
      exclude.add(targetDirectory.getName() + "/**");
    }
    final String[] excluded = new String[exclude.size()];
    dirScanner.setExcludes(exclude.toArray(excluded));

    dirScanner.scan();
    return dirScanner.getIncludedFiles();
  }
}
