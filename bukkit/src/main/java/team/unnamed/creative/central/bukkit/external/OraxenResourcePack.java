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
package team.unnamed.creative.central.bukkit.external;

import io.th0rgal.oraxen.api.events.OraxenPackGeneratedEvent;
import io.th0rgal.oraxen.utils.VirtualFile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackReader;
import team.unnamed.creative.serialize.minecraft.fs.FileTreeReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static java.util.Objects.requireNonNull;

public final class OraxenResourcePack implements Listener {
    @EventHandler
    public void onGenerated(final @NotNull OraxenPackGeneratedEvent event) {
        final var output = event.getOutput();
        final var resourcePack = MinecraftResourcePackReader.minecraft().read(new VirtualFilesTreeReader(output.iterator()));

        // todo: merge resource pack
    }

    private static class VirtualFilesTreeReader implements FileTreeReader {
        private final Iterator<VirtualFile> files;
        private VirtualFile current;

        VirtualFilesTreeReader(final @NotNull Iterator<VirtualFile> files) {
            this.files = requireNonNull(files, "files");
        }

        @Override
        public boolean hasNext() {
            return files.hasNext();
        }

        @Override
        public String next() {
            current = files.next();
            return current.getPath();
        }

        @Override
        public @NotNull InputStream stream() {
            if (current == null) {
                throw new IllegalStateException("No current file");
            }

            final byte[] bytes;

            // copy the current input stream
            try (final var inputStream = current.getInputStream()) {
                bytes = Writable.copyInputStream(inputStream).toByteArray();
            } catch (final IOException e) {
                throw new IllegalStateException("Failed to read (copy) Oraxen resource pack", e);
            }

            // use different input streams, so we do not cause issues :)
            current.setInputStream(new ByteArrayInputStream(bytes));
            return new ByteArrayInputStream(bytes);
        }

        @Override
        public void close() {
        }
    }
}
