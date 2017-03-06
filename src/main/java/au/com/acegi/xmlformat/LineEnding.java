/*-
 * #%L
 * XML Format Maven Plugin
 * %%
 * Copyright (C) 2011 - 2017 Acegi Technology Pty Limited
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

/**
 * Valid line endings for use by {@link XmlFormatPlugin#lineEnding}.
 */
public enum LineEnding {

  /**
   * Use the system default line ending.
   */
  SYSTEM(),
  /**
   * Use the newline character. Typical on Unix and Unix-like systems.
   */
  LF("\n"),
  /**
   * Use the carriage return and new line characters. Typical on Windows.
   */
  CRLF("\r\n"),
  /**
   * Use the carriage return character.
   */
  CR("\r");

  private final String chars;

  LineEnding() {
    this.chars = System.lineSeparator();
  }

  LineEnding(final String value) {
    this.chars = value;
  }

  public String getChars() {
    return this.chars;
  }

}
