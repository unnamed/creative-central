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
package team.unnamed.creative.central.bukkit.util;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public final class PluginResources {

    private PluginResources() {
    }

    public static @Nullable InputStream get(Plugin plugin, String path) {
        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) {
            // file doesn't exist in the plugin's data folder,
            // try to get it from the jar and copy it to the
            // data folder
            InputStream stream = plugin.getResource(path);
            if (stream == null) {
                // file doesn't exist in the jar either
                return null;
            }
            plugin.saveResource(path, false);
            return stream;
        }

        // file exists in the plugin's data folder,
        // read the resource from there
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            // should never happen
            throw new IllegalStateException("File doesn't exist", e);
        }
    }

}