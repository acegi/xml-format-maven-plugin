/*-
 * #%L
 * XML Format Maven Plugin
 * %%
 * Copyright (C) 2011 - 2023 Acegi Technology Pty Limited
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;

/**
 * Utility methods required by the test package.
 */
final class TestUtil {

  private TestUtil() {
  }

  static String fileToString(final File in) {
    try (InputStream is = Files.newInputStream(in.toPath())) {
      return streamToString(is);
    } catch (final IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  static InputStream getResource(final String resource) {
    final InputStream in = FormatUtilTest.class.getResourceAsStream(resource);
    assertThat(resource + " not found", in, not(nullValue()));
    return in;
  }

  static String streamToString(final InputStream in) {
    final StringWriter sw = new StringWriter();
    final InputStreamReader reader = new InputStreamReader(in, UTF_8);
    final char[] buffer = new char[4_096];
    int n;
    try {
      while (-1 != (n = reader.read(buffer))) {
        sw.write(buffer, 0, n);
      }
    } catch (final IOException ex) {
      throw new IllegalStateException(ex);
    }
    return sw.toString();
  }

  static void stringToFile(final String msg, final File out) {
    try (OutputStream fos = Files.newOutputStream(out.toPath());
        Writer writer = new OutputStreamWriter(fos, UTF_8)) {
      writer.append(msg);
    } catch (final IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

}
