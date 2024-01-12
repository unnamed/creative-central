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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.central.common.util.HttpUtil;
import team.unnamed.creative.central.export.ResourcePackExporter;
import team.unnamed.creative.central.export.ResourcePackLocation;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter;
import team.unnamed.creative.serialize.minecraft.fs.FileTreeWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipOutputStream;

import static java.util.Objects.requireNonNull;

public final class PolymathExporter implements ResourcePackExporter {
    private static final JsonParser JSON_PARSER = new JsonParser();

    // base polymath url, e.g. https://atlas.oraxen.com
    // never ends with '/', always starts with 'http://' or 'https://'
    private final String baseUrl;
    private final String secret;

    public PolymathExporter(final @NotNull String baseUrl, final @NotNull String secret) {
        this.baseUrl = reformatUrl(baseUrl);
        this.secret = requireNonNull(secret, "secret");
    }

    private String reformatUrl(@NotNull String baseUrl) {
        requireNonNull(baseUrl, "baseUrl");
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        if (!baseUrl.startsWith("https://") && !baseUrl.startsWith("http://")) {
            baseUrl = "https://" + baseUrl;
        }
        return baseUrl;
    }

    @Override
    public @NotNull ResourcePackLocation export(final ResourcePack pack) throws IOException {
        final URL url = new URL(baseUrl + "/upload");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        final String multipartBoundary = HttpUtil.generateBoundary();

        connection.setRequestProperty("User-Agent", "creative-central");
        connection.setRequestProperty("Charset", "utf-8");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + multipartBoundary);

        try (final OutputStream output = connection.getOutputStream()) {
            // write "id" field
            output.write((
                    "--" + multipartBoundary + HttpUtil.LINE_FEED
                    + "Content-Disposition: form-data; name=\"id\"" + HttpUtil.LINE_FEED + HttpUtil.LINE_FEED
                    + secret + HttpUtil.LINE_FEED
            ).getBytes(StandardCharsets.UTF_8));

            // write "pack" field
            output.write((
                    "--" + multipartBoundary + HttpUtil.LINE_FEED
                            + "Content-Disposition: form-data; name=\"pack\"; filename=\"pack.zip\"" + HttpUtil.LINE_FEED
                            + "Content-Type: application/zip"+ HttpUtil.LINE_FEED + HttpUtil.LINE_FEED
            ).getBytes(StandardCharsets.UTF_8));

            final FileTreeWriter treeOutput = FileTreeWriter.zip(new ZipOutputStream(output));
            try {
                MinecraftResourcePackWriter.minecraft().write(treeOutput, pack);
            } finally {
                treeOutput.finish();
            }
            // end
            output.write((HttpUtil.LINE_FEED + "--" + multipartBoundary + "--" + HttpUtil.LINE_FEED).getBytes(StandardCharsets.UTF_8));
        }

        final String response = Writable.inputStream(connection::getInputStream).toUTF8String();
        final JsonObject responseObject = JSON_PARSER.parse(response).getAsJsonObject();

        if (responseObject.has("url") && responseObject.has("sha1")) {
            // success
            final String packUrl = responseObject.get("url").getAsString();
            final String sha1 = responseObject.get("sha1").getAsString();
            try {
                return ResourcePackLocation.of(new URI(packUrl), sha1);
            } catch (final URISyntaxException e) {
                throw new IllegalStateException("Polymath server returned an invalid URI: " + packUrl, e);
            }
        }

        if (responseObject.has("error")) {
            // error
            throw new IOException("Polymath server returned an error: " + responseObject.get("error").getAsString());
        }

        throw new IOException("Unknown error given: " + responseObject);
    }
}
