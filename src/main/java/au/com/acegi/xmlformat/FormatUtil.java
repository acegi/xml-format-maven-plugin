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

import static au.com.acegi.xmlformat.IOUtil.hash;
import java.io.File;
import static java.io.File.createTempFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import static java.nio.file.Files.copy;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility methods private to the package.
 */
final class FormatUtil {

  private static final String TMP_FILE_PREFIX = FormatUtil.class.getSimpleName();

  private FormatUtil() {
  }

  /**
   * Ingest an input stream, writing formatted XML to the output stream. The
   * caller is responsible for closing the input and output streams. Any errors
   * in the input stream will cause an exception and the output stream should
   * not be relied upon.
   *
   * @param in  input XML stream
   * @param out output XML stream
   * @param fmt format configuration to apply
   * @throws DocumentException if input XML could not be parsed
   * @throws IOException       if output XML stream could not be written
   */
  static void format(final InputStream in, final OutputStream out,
                     final OutputFormat fmt) throws DocumentException,
                                                    IOException {
    final SAXReader reader = new SAXReader();
    reader.setEntityResolver(new EntityResolver() {
      @Override
      public InputSource resolveEntity(final String publicId,
                                       final String systemId)
          throws SAXException, IOException {
        return new InputSource(new StringReader(""));
      }
    });
    final Document xmlDoc = reader.read(in);

    final XMLWriter xmlWriter = new XMLWriter(out, fmt);
    xmlWriter.write(xmlDoc);
    xmlWriter.flush();
  }

  /**
   * Formats the input file, overwriting the input file with the new content if
   * the formatted content differs.
   *
   * @param file to read and then potentially overwrite
   * @param fmt  format configuration to apply
   * @return true if the file was overwritten
   * @throws DocumentException if input XML could not be parsed
   * @throws IOException       if output XML stream could not be written
   */
  @SuppressWarnings("checkstyle:returncount")
  static boolean formatInPlace(final File file, final OutputFormat fmt)
      throws DocumentException, IOException {
    if (file.length() == 0) {
      return false;
    }

    final File tmpFile = createTempFile(TMP_FILE_PREFIX, ".xml");
    tmpFile.deleteOnExit();

    try (InputStream in = Files.newInputStream(file.toPath());
         OutputStream out = Files.newOutputStream(tmpFile.toPath())) {
      format(in, out, fmt);
    }

    final long hashFile = hash(file);
    final long hashTmp = hash(tmpFile);
    if (hashFile == hashTmp) {
      return false;
    }

    final Path source = tmpFile.toPath();
    final Path target = file.toPath();
    copy(source, target, REPLACE_EXISTING);
    return true;
  }

  /**
   * Only checks if the input file would be modified by the formatter, without
   * overwriting it.
   *
   * @param file to read
   * @param fmt  format configuration to apply
   * @return true if the file would be modified by the formatter
   * @throws DocumentException if input XML could not be parsed
   * @throws IOException       if output XML stream could not be written
   */
  @SuppressWarnings("checkstyle:returncount")
  static boolean needsFormatting(final File file, final OutputFormat fmt)
      throws DocumentException, IOException {
    if (file.length() == 0) {
      return false;
    }

    final File tmpFile = createTempFile(TMP_FILE_PREFIX, ".xml");
    tmpFile.deleteOnExit();

    try (InputStream in = Files.newInputStream(file.toPath());
        OutputStream out = Files.newOutputStream(tmpFile.toPath())) {
      format(in, out, fmt);
    }

    final long hashFile = hash(file);
    final long hashTmp = hash(tmpFile);
    return hashFile != hashTmp;
  }
}
