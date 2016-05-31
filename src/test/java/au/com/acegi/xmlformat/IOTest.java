package au.com.acegi.xmlformat;

import static au.com.acegi.xmlformat.IOUtil.hash;
import static au.com.acegi.xmlformat.TestUtil.getResource;
import java.io.IOException;
import java.io.InputStream;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class IOTest {

  @Test
  public void hash1() throws IOException {
    testHash("/test1-in.xml", 1_864_626_297L);
  }

  @Test
  public void hash2() throws IOException {
    testHash("/test2-in.xml", 1_687_393_391L);
  }

  @Test
  public void hashInvalid() throws IOException {
    testHash("/invalid.xml", 2_678_376_893L);
  }

  private void testHash(String resource, long expected) throws IOException {
    InputStream in = getResource(resource);
    long hash = hash(in);
    assertThat(hash, is(expected));
  }

}
