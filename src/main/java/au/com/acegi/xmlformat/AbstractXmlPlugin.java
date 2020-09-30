/*-
 * #%L
 * XML Format Maven Plugin
 * %%
 * Copyright (C) 2011 - 2020 Acegi Technology Pty Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package au.com.acegi.xmlformat;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;
import static org.dom4j.io.OutputFormat.createPrettyPrint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;

/**
 * Common infrastructure for the various plugin goals.
 */
@SuppressWarnings("DesignForExtension")
public abstract class AbstractXmlPlugin extends AbstractMojo {

  /**
   * Quote character to use when writing attributes.
   */
  @Parameter(property = "attributeQuoteChar", defaultValue = "\"")
  @SuppressWarnings("PMD.ImmutableField")
  private char attributeQuoteChar = '"';

  /**
   * The base directory of the project.
   */
  @Parameter(defaultValue = ".", readonly = true, required = true, property
             = "project.basedir")
  private File baseDirectory;

  /**
   * The encoding format.
   */
  @Parameter(property = "encoding", defaultValue = "UTF-8")
  @SuppressWarnings("PMD.ImmutableField")
  private String encoding = "UTF-8";
  /**
   * A set of file patterns that allow you to exclude certain files/folders from
   * the formatting. In addition to these exclusions, the project build
   * directory (typically <code>target</code>) is always excluded if skipTargetFolder
   * is true.
   */
  @Parameter(property = "excludes")
  private String[] excludes;

  /**
   * Whether or not to expand empty elements to &lt;tagName&gt;&lt;/tagName&gt;.
   */
  @Parameter(property = "expandEmptyElements", defaultValue = "false")
  private boolean expandEmptyElements;
  /**
   * A set of file patterns that dictate which files should be included in the
   * formatting with each file pattern being relative to the base directory.
   */
  @Parameter(property = "includes")
  private String[] includes;

  /**
   * Indicates the number of spaces to apply when indenting. 
   */
  @Parameter(property = "indentSize", defaultValue = "2")
  private int indentSize;

  /**
   * Use tabs instead of spaces for indents.
   * If set to <code>true</code>, <code>indentSize</code> will be ignored.
   */
  @Parameter(property = "tabIndent", defaultValue = "false")
  private boolean tabIndent;

  /**
   * Sets the line-ending of files after formatting. Valid values are:
   * <ul>
   * <li><b>"SYSTEM"</b> - Use line endings of current system</li>
   * <li><b>"LF"</b> - Use Unix and Mac style line endings</li>
   * <li><b>"CRLF"</b> - Use DOS and Windows style line endings</li>
   * <li><b>"CR"</b> - Use early Mac style line endings</li>
   * </ul>
   *
   * <p>
   * This property is only used if {@link #lineSeparator} has its default
   * value. Do not set any value for {@link #lineSeparator}.
   */
  @Parameter(property = "lineEnding", defaultValue = "LF")
  @SuppressWarnings("PMD.ImmutableField")
  private LineEnding lineEnding = LineEnding.LF;
  /**
   * New line separator.
   *
   * <p>
   * @deprecated Please do not set this value; use {@link #lineEnding} instead
   */
  @Parameter(property = "lineSeparator", defaultValue = "\n")
  @SuppressWarnings("PMD.ImmutableField")
  @Deprecated
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
   * The default new line flag, set to do new lines only as in original
   * document.
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
   * Skip XML formatting.
   */
  @Parameter(property = "xml-format.skip", defaultValue = "false")
  private boolean skip;

  /**
   * In addition to the exclusions, the project build
   * directory (typically <code>target</code>) is always excluded if true.
   */
  @Parameter(property = "skipTargetFolder", defaultValue = "true")
  private boolean skipTargetFolder = true;

  /**
   * Whether or not to suppress the XML declaration.
   */
  @Parameter(property = "suppressDeclaration", defaultValue = "false")
  private boolean suppressDeclaration;

  /**
   * The project target directory. This is always excluded from formatting.
   */
  @Parameter(defaultValue = "${project.build.directory}", readonly = true,
             required = true)
  private File targetDirectory;

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

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    assert baseDirectory != null;
    assert targetDirectory != null;

    if (skip) {
      getLog().info("[xml-format] Skipped");
      return;
    }

    initializeIncludes();
    initializeExcludes();

    final OutputFormat fmt = buildFormatter();

    boolean success = true;
    boolean neededFormatting = false;
    for (final String inputName : find()) {
      final File input = new File(baseDirectory, inputName);
      try {
        neededFormatting |= processFile(input, fmt);
      } catch (final DocumentException | IOException ex) {
        success = false;
        getLog().error("[xml-format] Error for " + input, ex);
      }
    }

    if (!success) {
      throw new MojoFailureException("[xml-format] Failed)");
    }
    afterAllProcessed(neededFormatting);
  }

  /**
   * Processes a single file found in the project.
   *
   * @param input the file to process
   * @param fmt   the formatting options
   * @return true if the file required changes to match the formatting style
   * @throws DocumentException if input XML could not be parsed
   * @throws IOException       if output XML stream could not be written
   */
  protected abstract boolean processFile(File input, OutputFormat fmt)
      throws DocumentException, IOException;

  /**
   * Invoked after all files in the project have been processed.
   *
   * @param neededFormatting whether any processed file required changes to match the
   *                         formatting style
   * @throws MojoExecutionException if the build must be failed
   */
  protected abstract void afterAllProcessed(boolean neededFormatting)
      throws MojoExecutionException;

  void setBaseDirectory(final File baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  void setExcludes(final String... excludes) {
    this.excludes = excludes == null ? null : copyOf(excludes, excludes.length);
  }

  void setIncludes(final String... includes) {
    this.includes = includes == null ? null : copyOf(includes, includes.length);
  }

  void setSkip(final boolean skip) {
    this.skip = skip;
  }

  void setSkipTargetFolder(final boolean skipTargetFolder) {
    this.skipTargetFolder = skipTargetFolder;
  }

  void setTargetDirectory(final File targetDirectory) {
    this.targetDirectory = targetDirectory;
  }

  private OutputFormat buildFormatter() {
    final OutputFormat fmt = createPrettyPrint();
    fmt.setAttributeQuoteCharacter(attributeQuoteChar);
    fmt.setEncoding(encoding);
    fmt.setExpandEmptyElements(expandEmptyElements);
    if (tabIndent) {
      fmt.setIndent("\t");
    } else {
      fmt.setIndentSize(indentSize);
    }
    fmt.setLineSeparator(determineLineSeparator());
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

  private String determineLineSeparator() {
    return "\n".equals(lineSeparator) ? lineEnding.getChars() : lineSeparator;
  }

  private String[] find() {
    final DirectoryScanner dirScanner = new DirectoryScanner();
    dirScanner.setBasedir(baseDirectory);
    dirScanner.setIncludes(includes);

    final List<String> exclude = new ArrayList<>(asList(excludes));
    if (skipTargetFolder
        && baseDirectory.equals(targetDirectory.getParentFile())) {
      exclude.add(targetDirectory.getName() + "/**");
    }
    final String[] excluded = new String[exclude.size()];
    dirScanner.setExcludes(exclude.toArray(excluded));

    dirScanner.scan();
    return dirScanner.getIncludedFiles();
  }

  private void initializeExcludes() {
    if (excludes == null || excludes.length == 0) {
      excludes = new String[0];
    }
  }

  private void initializeIncludes() {
    if (includes == null || includes.length == 0) {
      includes = new String[]{"**/*.xml"};
    }
  }
}
