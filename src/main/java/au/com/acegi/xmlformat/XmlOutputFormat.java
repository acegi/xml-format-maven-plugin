/*
 * XML Format Maven Plugin (https://github.com/acegi/xml-format-maven-plugin)
 *
 * Copyright 2011-2025 Acegi Technology Pty Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.com.acegi.xmlformat;

import org.dom4j.io.OutputFormat;

/**
 * Extended DOM4J configuration.
 * <ul>
 * <li>Defaults to pretty print.
 * <li>Adds an option to keep blank lines.
 * </ul>
 */
public class XmlOutputFormat extends OutputFormat {

    private boolean keepBlankLines;

    /**
     * Instantiates a new xml output format.
     */
    public XmlOutputFormat() {
        // same as pretty print
        setIndentSize(2);
        setNewlines(true);
        setTrimText(true);
        setPadText(true);
    }

    /**
     * When set to true, preserves at most one blank line between tags, if it was alredy present in the input file.
     * Defaults to <code>false</code>.
     *
     * @return Whether blank lines are preserved, or not.
     */
    public boolean isKeepBlankLines() {
        return keepBlankLines;
    }

    /**
     * When set to true, preserves at most one blank line between tags, if it was alredy present in the input file.
     *
     * @param keepBlankLines
     *            true to preserve at most one blank line, false to remove all blank lines.
     */
    public void setKeepBlankLines(final boolean keepBlankLines) {
        this.keepBlankLines = keepBlankLines;
    }
}
