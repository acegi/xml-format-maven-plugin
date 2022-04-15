/*-
 * #%L
 * XML Format Maven Plugin
 * %%
 * Copyright (C) 2011 - 2022 Acegi Technology Pty Limited
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

import static au.com.acegi.xmlformat.IOUtil.hash;
import static au.com.acegi.xmlformat.TestUtil.getResource;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

/**
 * Tests {@link IOUtil}.
 */
public final class IOTest {

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

  private void testHash(final String resource, final long expected) throws
      IOException {
    try (InputStream in = getResource(resource)) {
      final long hash = hash(in);
      assertThat(hash, is(expected));
    }
  }

}
