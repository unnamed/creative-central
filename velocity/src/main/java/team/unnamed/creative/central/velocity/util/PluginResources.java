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
package team.unnamed.creative.central.velocity.util;

import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.central.velocity.CreativeCentralPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PluginResources {

    private PluginResources() {
    }

    public static @Nullable InputStream get(CreativeCentralPlugin plugin, String path) {
        Path dataFolder = plugin.getDataFolder();
        Path file = dataFolder.resolve(path);
        try {
            if (!Files.exists(file)) {
                if (!Files.isDirectory(dataFolder)) {
                    Files.createDirectory(dataFolder);
                }
                // file doesn't exist in the plugin's data folder,
                // try to get it from the jar and copy it to the
                // data folder
                InputStream stream = plugin.getClass().getResourceAsStream("/" + path);
                if (stream == null) {  // file doesn't exist in the jar either
                    if (path.equals("config.yml")) {
                        throw new IllegalStateException("config.yml not present in JAR");
                    }
                    return null;
                }
                Files.copy(stream, file);
            }

        // file exists in the plugin's data folder,
        // read the resource from there
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load or create resource:", e);
        }
    }

}