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
package team.unnamed.creative.central.velocity.external;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.central.velocity.CreativeCentralPlugin;

/**
 * Interface representing an external (like from another plugin)
 * resource pack provider.
 */
public interface ExternalResourcePackProvider {
    /**
     * Returns the name of the plugin that provides the resource pack.
     *
     * @return The name of the plugin that provides the resource pack.
     */
    @NotNull String pluginName();

    /**
     * Should we wait for the providers to make an initial resource-pack
     * zip/export/build before incorporating them to our own resource-pack?
     *
     * <p>When it's set to true, the first resource-pack generation will
     * wait until the resource-pack is created for this plugin.</p>
     *
     * <p>When it's set to false, the first resource-pack generation will
     * not wait, but will only check if it can load the current resource-pack</p>
     *
     * @return Whether we should wait for the providers to make an initial
     * resource-pack zip/export/build before incorporating them to our own
     * resource-pack.
     */
    default boolean awaitOnStart() {
        return false;
    }

    /**
     * Listens for changes/rebuilds in the resource pack provided
     * by this plugin.
     *
     * <p>It MUST be ensured that, when executing the given {@code changeListener},
     * the changes will be applied and calls to {@link #load()} from {@code changeListener}
     * MUST return the updated resource-pack.</p>
     *
     * @param plugin The plugin that will be used to register the listener.
     * @param changeListener The listener that will be called when the
     *                       resource pack changes.
     */
    default void listenForChanges(final @NotNull CreativeCentralPlugin plugin, final @NotNull Runnable changeListener) {
    }

    /**
     * Loads the resource-pack. Null if not found.
     *
     * @return The resource-pack. Null if not found.
     */
    @Nullable ResourcePack load();
}
