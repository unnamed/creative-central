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

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.events.ModelRegistrationEvent;
import com.ticxo.modelengine.api.generator.ModelGenerator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackReader;

import java.io.File;

public final class ModelEngineResourcePack implements Listener {
    @EventHandler
    public void onLoad(final @NotNull ModelRegistrationEvent event) {
        final var phase = event.getPhase();
        if (phase == ModelGenerator.Phase.POST_ZIPPING) {
            // ModelEngine resource pack has been zipped, we can load it
            // into our own resource pack
            final var resourcePackZipFile = new File(ModelEngineAPI.getAPI().getDataFolder(), "resource pack.zip");
            final var resourcePack = MinecraftResourcePackReader.minecraft().readFromZipFile(resourcePackZipFile);

            // todo: merge resource pack
        }
    }
}
