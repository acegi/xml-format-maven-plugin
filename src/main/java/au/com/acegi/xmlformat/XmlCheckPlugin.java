/*-
 * #%L
 * XML Format Maven Plugin
 * %%
 * Copyright (C) 2011 - 2024 Acegi Technology Pty Limited
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

import static au.com.acegi.xmlformat.FormatUtil.needsFormatting;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_SOURCES;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.dom4j.DocumentException;

/**
 * Finds the XML files in a project and only check them: no files are changed,
 * but the build will fail if any file does not follow the formatting
 * conventions.
 */
@Mojo(name = "xml-check", defaultPhase = PROCESS_SOURCES, threadSafe = true)
public final class XmlCheckPlugin extends AbstractXmlPlugin {

  @Override
  protected boolean processFile(final File input, final XmlOutputFormat fmt)
      throws DocumentException, IOException {
    final boolean needsFormatting = needsFormatting(input, fmt);
    final Log log = getLog();
    if (needsFormatting && log.isErrorEnabled()) {
      log.error("[xml-check] Needs formatting:" + input);
    } else if (log.isDebugEnabled()) {
      log.debug("[xml-check] Correctly formatted: " + input);
    }
    return needsFormatting;
  }

  @Override
  protected void afterAllProcessed(final boolean neededFormatting) throws MojoExecutionException {
    if (neededFormatting) {
      throw new MojoExecutionException(
          "[xml-check] At least one XML file needs formatting, see the error logs above)");
    }
  }
}
