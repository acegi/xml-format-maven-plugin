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

import static au.com.acegi.xmlformat.IOUtil.hash;
import static au.com.acegi.xmlformat.TestUtil.getResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link IOUtil}.
 */
public class IOTest {

    @Test
    void hash1() throws IOException {
        testHash("/test1-in.xml", 459_402_491L);
    }

    @Test
    void hash2() throws IOException {
        testHash("/test2-in.xml", 1_687_393_391L);
    }

    @Test
    void hashInvalid() throws IOException {
        testHash("/invalid.xml", 2_274_913_643L);
    }

    private void testHash(final String resource, final long expected) throws IOException {
        try (InputStream in = getResource(resource)) {
            final long hash = hash(in);
            assertThat(hash, is(expected));
        }
    }

}
