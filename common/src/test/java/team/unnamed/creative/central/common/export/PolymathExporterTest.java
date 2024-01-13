/*
 * This file is part of creative-central, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.creative.central.common.export;

import org.junit.jupiter.api.Test;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.central.common.util.Streams;
import team.unnamed.creative.central.export.ResourcePackLocation;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.DigestOutputStream;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PolymathExporterTest {
    // @Test // manually activate it
    void test() throws Exception {
        final ResourcePack resourcePack = ResourcePack.resourcePack();
        resourcePack.packMeta(8, "Test resource-pack!");

        // upload the resource-pack to MCPacks
        ResourcePackLocation location = new PolymathExporter("atlas.oraxen.com", "oraxen")
                .export(resourcePack);

        assertNotNull(location, "Location should not be null");
        assertEquals("7c708abe63955fefc2ff1fca614688874b9bd3f0", location.hash());

        // download the resource-pack from MCPacks
        URI uri = location.uri();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        DigestOutputStream digestOutputStream = new DigestOutputStream(bos, digest);
        try (InputStream input = uri.toURL().openStream()) {
            Streams.pipe(input, digestOutputStream);
        }
        // byte[] bytes = bos.toByteArray();
        byte[] hash = digest.digest();
        int len = hash.length;
        StringBuilder hashBuilder = new StringBuilder(len * 2);
        for (byte b : hash) {
            int part1 = (b >> 4) & 0xF;
            int part2 = b & 0xF;
            hashBuilder
                    .append(Character.forDigit(part1, 16))
                    .append(Character.forDigit(part2, 16));
        }
        String hashString = hashBuilder.toString();

        // check
        assertEquals("7c708abe63955fefc2ff1fca614688874b9bd3f0", hashString, "Downloaded hash should be the same as uploaded hash! ");
    }
}
