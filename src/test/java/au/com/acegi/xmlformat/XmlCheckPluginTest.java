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

import static au.com.acegi.xmlformat.TestUtil.fileToString;
import static au.com.acegi.xmlformat.TestUtil.stringToFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link XmlCheckPlugin}.
 */
public class XmlCheckPluginTest {

    private static final String EMPTY_FILE_NAME = "empty.xml";
    private static final String EMPTY_TXT = "";
    private static final String ERR_FILE_NAME = "error.xml";
    private static final String ERR_TXT = "<xml> <hello> hello not closed! </xml>";
    private static final String NO_CHG_TXT = "<xml> <leave-me-alone/> </xml>";
    private static final String TO_CHG_FILE_NAME = "my.xml";
    private static final String TO_CHG_TXT = "<xml> <hello/> </xml>";

    @TempDir
    private File tmp;

    @SuppressWarnings("PMD.ProperLogger")
    private Log log;

    private File proj;
    private File target;

    @BeforeEach
    void before() throws IOException {
        proj = newFolder(tmp, "junit");
        target = new File(proj, "target");
        assertThat(target.mkdir(), is(true));

        final File toChange = new File(proj, TO_CHG_FILE_NAME);
        stringToFile(TO_CHG_TXT, toChange);

        final File noChange = new File(target, "exclude-me.xml");
        stringToFile(NO_CHG_TXT, noChange);

        final File empty = new File(proj, EMPTY_FILE_NAME);
        stringToFile(EMPTY_TXT, empty);

        final File error = new File(proj, ERR_FILE_NAME);
        stringToFile(ERR_TXT, error);

        assertThat(fileToString(toChange), is(TO_CHG_TXT));
        assertThat(fileToString(noChange), is(NO_CHG_TXT));
        assertThat(fileToString(empty), is(EMPTY_TXT));
        assertThat(fileToString(error), is(ERR_TXT));

        log = mock(Log.class);
    }

    @Test
    @SuppressWarnings("PMD.JUnitUseExpected")
    void pluginReportsError() throws MojoExecutionException {
        final XmlCheckPlugin plugin = new XmlCheckPlugin();
        plugin.setLog(log);
        when(log.isDebugEnabled()).thenReturn(true);
        when(log.isErrorEnabled()).thenReturn(true);

        plugin.setBaseDirectory(proj);
        plugin.setIncludes("**/*.xml");
        plugin.setTargetDirectory(target);

        Assertions.assertThrows(MojoFailureException.class, () -> { 
            plugin.execute();
            fail("Should have raised exception when handling error");
        });

        verify(log, atLeastOnce()).isErrorEnabled();
        verify(log, atLeastOnce()).isDebugEnabled();
        verify(log, atLeastOnce()).debug(anyString());
    }

    @Test
    @SuppressWarnings("PMD.JUnitUseExpected")
    void pluginReportsFormattingNeeded() throws MojoFailureException {
        final XmlCheckPlugin plugin = new XmlCheckPlugin();
        plugin.setLog(log);
        when(log.isDebugEnabled()).thenReturn(true);
        when(log.isErrorEnabled()).thenReturn(true);

        plugin.setBaseDirectory(proj);
        plugin.setExcludes("**/" + ERR_FILE_NAME);
        plugin.setIncludes("**/*.xml");
        plugin.setTargetDirectory(target);

        Assertions.assertThrows(MojoExecutionException.class, () -> { 
            plugin.execute();
            fail("Should have raised exception when encountering non-formatted file");
        });

        verify(log, atLeastOnce()).isErrorEnabled();
        verify(log, atLeastOnce()).isDebugEnabled();
        verify(log, atLeastOnce()).debug(anyString());
    }

    @Test
    void pluginSucceedsWhenAllFormatted() throws MojoExecutionException, MojoFailureException {
        final XmlCheckPlugin plugin = new XmlCheckPlugin();
        plugin.setLog(log);
        when(log.isDebugEnabled()).thenReturn(true);
        when(log.isErrorEnabled()).thenReturn(true);

        plugin.setBaseDirectory(proj);
        plugin.setExcludes("**/" + ERR_FILE_NAME, "**/" + TO_CHG_FILE_NAME);
        plugin.setIncludes("**/*.xml");
        plugin.setTargetDirectory(target);

        plugin.execute();

        verify(log, never()).isErrorEnabled();
        verify(log, atLeastOnce()).isDebugEnabled();
        verify(log, atLeastOnce()).debug(anyString());
    }

    private static File newFolder(final File root, final String... subDirs) throws IOException {
        final String subFolder = String.join("/", subDirs);
        final File result = new File(root, subFolder);
        if (!result.mkdirs()) {
            throw new IOException("Couldn't create folders " + root);
        }
        return result;
    }
}
