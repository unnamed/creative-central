/*
 * This file is part of creative, licensed under the MIT license
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
package team.unnamed.creative.central.minestom.listener;

import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.utils.time.TimeUnit;
import team.unnamed.creative.central.CreativeCentral;
import team.unnamed.creative.central.request.ResourcePackRequest;
import team.unnamed.creative.central.server.ServeOptions;

public class ResourcePackSendListener {

    private final CreativeCentral central;

    public ResourcePackSendListener(CreativeCentral central) {
        this.central = central;
    }

    public void onJoin(PlayerSpawnEvent event) {

        if (!event.isFirstSpawn()) {
            return;
        }

        ServeOptions options = central.serveOptions();
        if (!options.serve()) {
            // we don't send the resource pack on join
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
            central.requestSender().send(player, request);
        } else {
            // delay the resource pack request
            player.scheduler().buildTask(() -> central.requestSender().send(player, request))
                    .delay(delay, TimeUnit.SECOND)
                    .schedule();
        }
    }

}
