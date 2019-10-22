/*-
 * #%L
 * XML Format Maven Plugin
 * %%
 * Copyright (C) 2011 - 2019 Acegi Technology Pty Limited
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

import static au.com.acegi.xmlformat.FormatUtil.format;
import static au.com.acegi.xmlformat.FormatUtil.formatInPlace;
import static au.com.acegi.xmlformat.TestUtil.getResource;
import static au.com.acegi.xmlformat.TestUtil.streamToString;
import static au.com.acegi.xmlformat.TestUtil.stringToFile;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import static org.dom4j.io.OutputFormat.createPrettyPrint;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests {@link FormatUtil}.
 */
public final class FormatUtilTest {

  private static final String FORMATTED_XML = "<xml><hello/></xml>";
  private static final String UNFORMATTED_XML = "<xml>   <hello/> </xml>";

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  @Test
  public void formattedWillNotChange() throws DocumentException, IOException {
    inPlaceChange(FORMATTED_XML, false);
  }

  @Test
  public void test1() throws DocumentException, IOException {
    final OutputFormat fmt = createPrettyPrint();
    fmt.setIndentSize(4);
    fmt.setNewLineAfterDeclaration(false);
    fmt.setPadText(false);
    testInOut(1, fmt);
  }

  @Test
  public void test2() throws DocumentException, IOException {
    final OutputFormat fmt = createPrettyPrint();
    fmt.setIndentSize(2);
    fmt.setNewLineAfterDeclaration(false);
    fmt.setPadText(false);
    testInOut(2, fmt);
  }

  @Test
  public void test3() throws DocumentException, IOException {
    final OutputFormat fmt = createPrettyPrint();
    fmt.setIndentSize(2);
    fmt.setNewLineAfterDeclaration(false);
    fmt.setPadText(false);
    testInOut(3, fmt);
  }

  @Test(expected = DocumentException.class)
  public void testInvalid() throws DocumentException, IOException {
    final InputStream in = getResource("/invalid.xml");
    try {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      format(in, out, createPrettyPrint());
    } finally {
      in.close();
    }
  }

  @Test
  public void unformattedWillChange() throws DocumentException, IOException {
    inPlaceChange(UNFORMATTED_XML, true);
  }

  private void inPlaceChange(final String txt, final boolean shouldChange)
      throws DocumentException, IOException {
    final File file = tmp.newFile();
    stringToFile(txt, file);

    final OutputFormat fmt = createPrettyPrint();
    fmt.setSuppressDeclaration(true);
    fmt.setIndent("");
    fmt.setNewlines(false);

    final boolean written = formatInPlace(file, fmt);
    assertThat(written, is(shouldChange));
  }

  private void testInOut(final int id, final OutputFormat fmt) throws
      DocumentException, IOException {
    final InputStream in = getResource("/test" + id + "-in.xml");
    try {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      format(in, out, fmt);

      final String received = new String(out.toByteArray(), UTF_8);
      final String expected = streamToString(
          getResource("/test" + id + "-out.xml"));
      assertThat(received, is(expected));
    } finally {
      in.close();
    }
  }

}
