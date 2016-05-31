package au.com.acegi.xmlformat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

final class TestUtil {

  static String fileToString(File in) throws IOException {
    try (InputStream is = new FileInputStream(in);) {
      return streamToString(is);
    }
  }

  static InputStream getResource(String resource) {
    InputStream in = FormatUtilTest.class.getResourceAsStream(resource);
    assertThat(resource + " not found", in, not(nullValue()));
    return in;
  }

  static String streamToString(InputStream in) throws IOException {
    StringWriter sw = new StringWriter();
    InputStreamReader reader = new InputStreamReader(in);
    char[] buffer = new char[4_096];
    int n = 0;
    while (-1 != (n = reader.read(buffer))) {
      sw.write(buffer, 0, n);
    }
    return sw.toString();
  }

  static void stringToFile(String msg, File out) throws IOException {
    try (FileWriter writer = new FileWriter(out)) {
      writer.append(msg);
    }
  }

  private TestUtil() {
  }

}
