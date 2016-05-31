package au.com.acegi.xmlformat;

import static au.com.acegi.xmlformat.IOUtil.hash;
import java.io.File;
import static java.io.File.createTempFile;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.nio.file.Files.copy;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
final class FormatUtil {

  private static final String TMP_FILE_PREFIX = FormatUtil.class.getSimpleName();

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
  static boolean formatInPlace(final File file, final OutputFormat fmt)
      throws DocumentException, IOException {
    assert file != null;
    assert fmt != null;
    assert file.exists();
    assert file.isFile();

    final File tmpFile = createTempFile(TMP_FILE_PREFIX, ".xml");
    tmpFile.deleteOnExit();

    try (final FileInputStream inputFis = new FileInputStream(file);
         final FileOutputStream tmpOut = new FileOutputStream(tmpFile);) {
      format(inputFis, tmpOut, fmt);
    }

    if (hash(file) == hash(tmpFile)) {
      return false;
    }

    final Path source = tmpFile.toPath();
    final Path target = file.toPath();
    copy(source, target, REPLACE_EXISTING);
    return true;
  }
}
