package au.com.acegi.xmlformat;

import static au.com.acegi.xmlformat.TestUtil.fileToString;
import static au.com.acegi.xmlformat.TestUtil.stringToFile;
import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;

public class XmlFormatPluginTest {

  private static final String ERR_FILE_NAME = "error.xml";
  private static final String ERR_TXT = "<xml> <hello> hello not closed! </xml>";
  private static final String NO_CHG_TXT = "<xml> <leave-me-alone/> </xml>";
  private static final String TO_CHG_TXT = "<xml> <hello/> </xml>";

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();
  private File error;
  private Log log;
  private File noChange;
  private File proj;
  private File target;
  private File toChange;

  @Before
  public void before() throws IOException {
    proj = tmp.newFolder();
    target = new File(proj, "target");
    target.mkdir();

    toChange = new File(proj, "my.xml");
    stringToFile(TO_CHG_TXT, toChange);

    noChange = new File(target, "exclude-me.xml");
    stringToFile(NO_CHG_TXT, noChange);

    error = new File(proj, ERR_FILE_NAME);
    stringToFile(ERR_TXT, error);

    assertThat(fileToString(toChange), is(TO_CHG_TXT));
    assertThat(fileToString(noChange), is(NO_CHG_TXT));
    assertThat(fileToString(error), is(ERR_TXT));

    log = mock(Log.class);
  }

  @Test
  public void pluginExcludesError() throws IOException, MojoExecutionException,
                                           MojoFailureException {

    final XmlFormatPlugin plugin = new XmlFormatPlugin();
    plugin.setLog(log);
    when(log.isDebugEnabled()).thenReturn(true);
    when(log.isErrorEnabled()).thenReturn(true);

    plugin.baseDirectory = proj;
    plugin.excludes = new String[]{"**/" + ERR_FILE_NAME};
    plugin.includes = new String[]{"**/*.xml"};
    plugin.targetDirectory = target;

    plugin.execute();

    verify(log, never()).isErrorEnabled();
    verify(log, atLeastOnce()).isDebugEnabled();
    verify(log, atLeastOnce()).debug(anyString());

    assertThat(fileToString(toChange), not(TO_CHG_TXT));
    assertThat(fileToString(noChange), is(NO_CHG_TXT));
    assertThat(fileToString(error), is(ERR_TXT));
  }

  @Test
  public void pluginReportsError() throws IOException, MojoExecutionException,
                                          MojoFailureException {

    final XmlFormatPlugin plugin = new XmlFormatPlugin();
    plugin.setLog(log);
    when(log.isDebugEnabled()).thenReturn(false);
    when(log.isErrorEnabled()).thenReturn(true);

    plugin.baseDirectory = proj;
    plugin.excludes = new String[]{""};
    plugin.includes = new String[]{"**/*.xml"};
    plugin.targetDirectory = target;

    try {
      plugin.execute();
      fail("Should have raised exception when handling error");
    } catch (MojoFailureException expected) {
    }

    verify(log, atLeastOnce()).error(anyString(), any(Throwable.class));
    verify(log, atLeastOnce()).isDebugEnabled();
    verify(log, never()).debug(anyString());

    assertThat(fileToString(toChange), not(TO_CHG_TXT));
    assertThat(fileToString(noChange), is(NO_CHG_TXT));
    assertThat(fileToString(error), is(ERR_TXT));
  }
}
