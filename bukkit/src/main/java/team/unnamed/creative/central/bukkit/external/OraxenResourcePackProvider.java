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

import io.th0rgal.oraxen.api.OraxenPack;
import io.th0rgal.oraxen.api.events.OraxenPackGeneratedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackReader;

import static java.util.Objects.requireNonNull;

public final class OraxenResourcePackProvider implements ExternalResourcePackProvider {
    @Override
    public @NotNull String pluginName() {
        return "Oraxen";
    }

    @Override
    public boolean awaitOnStart() {
        // Oraxen generates its resource-pack on startup
        return true;
    }

    @Override
    public void listenForChanges(final @NotNull Plugin plugin, final @NotNull Runnable changeListener) {
        requireNonNull(plugin, "plugin");
        requireNonNull(changeListener, "changeListener");
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onGenerated(final @NotNull OraxenPackGeneratedEvent event) {
                // todo: change when Oraxen adds an event when they finished writing the pack
                // at the moment, we have to wait 1 second to ensure that the pack is written
                // (although it's not guaranteed)
                plugin.getServer().getScheduler().runTaskLater(plugin, changeListener, 20L);
            }
        }, plugin);
    }

    @Override
    public @Nullable ResourcePack load() {
        final var oraxenPack = OraxenPack.getPack();
        if (!oraxenPack.exists()) {
            return null;
        }
        return MinecraftResourcePackReader.minecraft().readFromZipFile(oraxenPack);
    }
}
