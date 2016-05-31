package au.com.acegi.xmlformat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
final class IOUtil {

  /**
   * Returns a CRC32 of the provided input stream.
   *
   * @param in to CRC32
   * @return the CRC32 value
   * @throws IOException if unable to read the input stream
   */
  static long hash(final InputStream in) throws IOException {
    Checksum cksum = new CRC32();
    CheckedInputStream is = new CheckedInputStream(in, cksum);
    byte[] buff = new byte[4_096];
    while (is.read(buff) >= 0) {
    }
    return is.getChecksum().getValue();
  }

  /**
   * Returns a CRC32 of the given file.
   *
   * @param file to CRC32
   * @return the CRC32 value
   * @throws IOException if unable to read the file
   */
  static long hash(final File file) throws IOException {
    try (FileInputStream fis = new FileInputStream(file);) {
      return hash(fis);
    }
  }
}
