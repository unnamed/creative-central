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
import com.velocitypowered.api.event.player.PlayerResourcePackStatusEvent;
import com.velocitypowered.api.proxy.Player;
import team.unnamed.creative.central.CreativeCentral;
import team.unnamed.creative.central.event.pack.ResourcePackStatusEvent;
import team.unnamed.creative.central.pack.ResourcePackStatus;

public class ResourcePackStatusListener {

    private final CreativeCentral central;

    public ResourcePackStatusListener(CreativeCentral central) {
        this.central = central;
    }

    @Subscribe
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        // convert bukkit status to creative status
        ResourcePackStatus status = switch (event.getStatus()) {
            case ACCEPTED -> ResourcePackStatus.ACCEPTED;
            case DECLINED -> ResourcePackStatus.DECLINED;
            case FAILED_DOWNLOAD -> ResourcePackStatus.FAILED;
            case SUCCESSFUL -> ResourcePackStatus.LOADED;
            case DOWNLOADED -> ResourcePackStatus.DOWNLOADED;
            case INVALID_URL -> ResourcePackStatus.INVALID_URL;
            case FAILED_RELOAD -> ResourcePackStatus.FAILED_RELOAD;
            case DISCARDED -> ResourcePackStatus.DISCARDED;
        };

        central.eventBus().call(ResourcePackStatusEvent.class, new ResourcePackStatusEvent(player, status));
    }

}
