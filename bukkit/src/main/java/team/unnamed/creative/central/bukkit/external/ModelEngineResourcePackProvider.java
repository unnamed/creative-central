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

import com.ticxo.modelengine.api.events.ModelRegistrationEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackReader;

import java.io.File;
import java.lang.reflect.Method;

import static java.util.Objects.requireNonNull;

public final class ModelEngineResourcePackProvider implements ExternalResourcePackProvider {
    @Override
    public @NotNull String pluginName() {
        return "ModelEngine";
    }

    @Override
    public boolean awaitOnStart() {
        // ModelEngine generates its resource-pack on startup
        return true;
    }

    @Override
    public void listenForChanges(final @NotNull Plugin plugin, final @NotNull Runnable changeListener) {
        requireNonNull(plugin, "plugin");
        requireNonNull(changeListener, "changeListener");

        // We do this to support both ModelEngine3 and ModelEngine4 :(
        final Method getPhaseMethod;
        try {
            getPhaseMethod = ModelRegistrationEvent.class.getDeclaredMethod("getPhase");
            getPhaseMethod.setAccessible(true);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException("Failed to get ModelRegistrationEvent#getPhase method", e);
        }

        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onModelRegistration(final @NotNull ModelRegistrationEvent event) {
                final Enum<?> phase;
                try {
                    phase = (Enum<?>) getPhaseMethod.invoke(event);
                } catch (final ReflectiveOperationException e) {
                    throw new IllegalStateException("Failed to get phase from ModelRegistrationEvent", e);
                }

                final var phaseName = phase.name();

                // It's FINISHED for ME4, and FINAL for ME3
                if (phaseName.equals("FINISHED") || phaseName.equals("FINAL")) {
                    changeListener.run();
                }
            }
        }, plugin);
    }

    @Override
    public @Nullable ResourcePack load() {
        final var modelEngine = Bukkit.getPluginManager().getPlugin(pluginName());
        if (modelEngine == null) {
            // Should never happen
            return null;
        }

        final var resourcePackZipFile = new File(modelEngine.getDataFolder(), "resource pack.zip");
        if (!resourcePackZipFile.exists()) {
            return null;
        }
        return MinecraftResourcePackReader.minecraft().readFromZipFile(resourcePackZipFile);
    }
}
