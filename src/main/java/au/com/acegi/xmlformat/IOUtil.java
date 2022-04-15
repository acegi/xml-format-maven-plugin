/*-
 * #%L
 * XML Format Maven Plugin
 * %%
 * Copyright (C) 2011 - 2022 Acegi Technology Pty Limited
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

/**
 * Utility methods for dealing with I/O resources.
 */
final class IOUtil {

  private IOUtil() {
  }

  /**
   * Returns a CRC32 of the provided input stream.
   *
   * @param in to CRC32
   * @return the CRC32 value
   * @throws IOException if unable to read the input stream
   */
  @SuppressWarnings("PMD.EmptyWhileStmt")
  static long hash(final InputStream in) throws IOException {
    final Checksum cksum = new CRC32();
    final CheckedInputStream is = new CheckedInputStream(in, cksum);
    final byte[] buff = new byte[4_096];
    while (is.read(buff) >= 0) {
      // CheckInputStream will update its internal checksum
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
    try (InputStream fis = Files.newInputStream(file.toPath())) {
      return hash(fis);
    }
  }

}
