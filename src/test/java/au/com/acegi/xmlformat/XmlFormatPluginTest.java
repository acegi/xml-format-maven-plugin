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

import static au.com.acegi.xmlformat.TestUtil.fileToString;
import static au.com.acegi.xmlformat.TestUtil.stringToFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests {@link XmlFormatPlugin}.
 */
public final class XmlFormatPluginTest {

  private static final String EMPTY_FILE_NAME = "empty.xml";
  private static final String EMPTY_TXT = "";
  private static final String ERR_FILE_NAME = "error.xml";
  private static final String ERR_TXT = "<xml> <hello> hello not closed! </xml>";
  private static final String NO_CHG_TXT = "<xml> <leave-me-alone/> </xml>";
  private static final String TO_CHG_TXT = "<xml> <hello/> </xml>";
  private static final String INCLUDE_ALL_XML = "**/*.xml";

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();
  private File error;
  @SuppressWarnings("PMD.ProperLogger")
  private Log log;
  private File noChange;
  private File proj;
  private File target;
  private File toChange;

  @Before
  public void before() throws IOException {
    proj = tmp.newFolder();
    target = new File(proj, "target");
    assertThat(target.mkdir(), is(true));

    toChange = new File(proj, "my.xml");
    stringToFile(TO_CHG_TXT, toChange);

    noChange = new File(target, "exclude-me.xml");
    stringToFile(NO_CHG_TXT, noChange);

    final File empty = new File(proj, EMPTY_FILE_NAME);
    stringToFile(EMPTY_TXT, empty);

    error = new File(proj, ERR_FILE_NAME);
    stringToFile(ERR_TXT, error);

    assertThat(fileToString(toChange), is(TO_CHG_TXT));
    assertThat(fileToString(noChange), is(NO_CHG_TXT));
    assertThat(fileToString(empty), is(EMPTY_TXT));
    assertThat(fileToString(error), is(ERR_TXT));

    log = mock(Log.class);
  }

  @Test
  public void pluginExcludesError() throws MojoExecutionException, MojoFailureException {
    final XmlFormatPlugin plugin = new XmlFormatPlugin();
    plugin.setLog(log);
    when(log.isDebugEnabled()).thenReturn(true);
    when(log.isErrorEnabled()).thenReturn(true);

    plugin.setBaseDirectory(proj);
    plugin.setExcludes("**/" + ERR_FILE_NAME);
    plugin.setIncludes(INCLUDE_ALL_XML);
    plugin.setTargetDirectory(target);

    plugin.execute();

    verify(log, never()).isErrorEnabled();
    verify(log, atLeastOnce()).isDebugEnabled();
    verify(log, atLeastOnce()).debug(anyString());

    assertThat(fileToString(toChange), not(TO_CHG_TXT));
    assertThat(fileToString(noChange), is(NO_CHG_TXT));
    assertThat(fileToString(error), is(ERR_TXT));
  }

  @Test
  @SuppressWarnings("PMD.JUnitUseExpected")
  public void pluginReportsError() throws MojoExecutionException {
    final XmlFormatPlugin plugin = new XmlFormatPlugin();
    plugin.setLog(log);
    when(log.isDebugEnabled()).thenReturn(false);
    when(log.isErrorEnabled()).thenReturn(true);

    plugin.setBaseDirectory(proj);
    plugin.setExcludes("");
    plugin.setIncludes(INCLUDE_ALL_XML);
    plugin.setTargetDirectory(target);

    try {
      plugin.execute();
      fail("Should have raised exception when handling error");
    } catch (final MojoFailureException ignored) {
    }

    verify(log, atLeastOnce()).error(anyString(), any(Throwable.class));
    verify(log, atLeastOnce()).isDebugEnabled();
    verify(log, never()).debug(anyString());

    assertThat(fileToString(toChange), not(TO_CHG_TXT));
    assertThat(fileToString(noChange), is(NO_CHG_TXT));
    assertThat(fileToString(error), is(ERR_TXT));
  }

  @Test
  @SuppressWarnings("PMD.JUnitUseExpected")
  public void pluginSkipTargetFolder() throws MojoExecutionException, MojoFailureException {
    final XmlFormatPlugin plugin = new XmlFormatPlugin();
    plugin.setLog(log);

    plugin.setSkipTargetFolder(false);
    when(log.isDebugEnabled()).thenReturn(true);
    when(log.isErrorEnabled()).thenReturn(true);

    plugin.setBaseDirectory(proj);
    plugin.setExcludes("**/" + ERR_FILE_NAME);
    plugin.setIncludes(INCLUDE_ALL_XML);
    plugin.setTargetDirectory(target);

    plugin.execute();

    assertThat(fileToString(toChange), not(TO_CHG_TXT));
    assertThat(fileToString(noChange), not(NO_CHG_TXT));
    assertThat(fileToString(error), is(ERR_TXT));
  }

  @Test
  public void pluginSkip() throws MojoExecutionException, MojoFailureException {
    final XmlFormatPlugin plugin = new XmlFormatPlugin();
    plugin.setLog(log);
    plugin.setSkip(true);
    when(log.isDebugEnabled()).thenReturn(true);
    when(log.isInfoEnabled()).thenReturn(true);
    when(log.isErrorEnabled()).thenReturn(true);

    plugin.setBaseDirectory(proj);
    plugin.setExcludes("**/" + ERR_FILE_NAME);
    plugin.setIncludes(INCLUDE_ALL_XML);
    plugin.setTargetDirectory(target);

    plugin.execute();

    verify(log, atLeastOnce()).info("[xml-format] Skipped");

    assertThat(fileToString(toChange), is(TO_CHG_TXT));
  }
}
