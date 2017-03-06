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

public enum LineEnding {

  SYSTEM_DEFAULT(),
  LF("\n"),
  CRLF("\r\n"),
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