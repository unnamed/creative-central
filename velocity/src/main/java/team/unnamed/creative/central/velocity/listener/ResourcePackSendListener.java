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
package team.unnamed.creative.central.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.util.Ticks;
import team.unnamed.creative.central.request.ResourcePackRequest;
import team.unnamed.creative.central.server.ServeOptions;
import team.unnamed.creative.central.velocity.CreativeCentralPlugin;

public class ResourcePackSendListener {

    private final CreativeCentralPlugin plugin;

    public ResourcePackSendListener(CreativeCentralPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Subscribe
    public void onJoin(ServerPostConnectEvent event) {
        ServeOptions options = plugin.serveOptions();
        if (!options.serve()) {
            // we don't send the resource pack on join
            return;
        }
        if (event.getPreviousServer() != null) {
            // we don't send the resource pack again
            return;
        }

        Player player = event.getPlayer();
        ResourcePackRequest request = options.request();
        if (request == null) {
            // todo: should we kick the player?
            return;
        }

        int delay = options.delay();
        if (delay <= 0) {
            // send the resource pack request immediately
            plugin.requestSender().send(player, request);
        } else {
            // delay the resource pack request
            plugin.getProxy().getScheduler()
                    .buildTask(plugin, () -> plugin.requestSender().send(player, request))
                    .delay(Ticks.duration(delay * 20L))
                    .schedule();
        }
    }

}
