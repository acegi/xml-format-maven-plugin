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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Node;
import org.dom4j.io.XMLWriter;

/**
 * Subclass of {@link XMLWriter} that preserves blank lines in outupt (at most
 * one of them, among two subsequent tags).
 */
class BlankLinesWriter extends XMLWriter {

  BlankLinesWriter(final OutputStream out, final XmlOutputFormat fmt)
      throws UnsupportedEncodingException {
    super(out, fmt);
  }

  @Override
  protected void writeString(final String text) throws IOException {
    if (text == null || text.length() == 0) {
      return;
    }

    String input = text;
    if (isEscapeText()) {
      input = escapeElementEntities(text);
    }

    if (getOutputFormat().isTrimText()) {
      boolean first = true;
      final StringTokenizer tokenizer = new StringTokenizer(input, " \t\r\f");

      final NewLinesHandler newLinesHandler = new NewLinesHandler();
      while (tokenizer.hasMoreTokens()) {
        final String token = tokenizer.nextToken();

        if (newLinesHandler.processToken(token)) {
          // Only if more tokens exist, continue
          if (tokenizer.hasMoreTokens()) {
              continue;
          }
        }

        if (first) {
          first = false;
          if (lastOutputNodeType == Node.TEXT_NODE) {
            writer.write(" ");
          }
        } else {
          writer.write(" ");
        }

        writer.write(token);
        lastOutputNodeType = Node.TEXT_NODE;
      }
      newLinesHandler.finished();
    } else {
      lastOutputNodeType = Node.TEXT_NODE;
      writer.write(input);
    }
  }

  private class NewLinesHandler {
    private int newLinesCount;

    /**
     * Processes the token, counting the newlines and producing at most one in
     * output.
     *
     * @param token The token to be written
     * @return True if the token needs to be skipped (it's a newline or a set of
     *         newlines)
     * @throws IOException If an I/O error occurs.
     */
    private boolean processToken(final String token) throws IOException {
      final int tokenNewLines = StringUtils.countMatches(token, '\n');
      if (tokenNewLines > 0) {
        newLinesCount += tokenNewLines;
        return true;
      }
      if (newLinesCount > 1) {
        writer.write("\n");
        newLinesCount = 0;
      }
      return false;
    }

    /**
     * Marks the end of token streams, allows to emit a last newlines if the last
     * tokens were all newlines.
     *
     * @throws IOException If an I/O error occurs.
     */
    private void finished() throws IOException {
      if (newLinesCount > 1) {
        writer.write("\n");
      }
    }
  }
}
