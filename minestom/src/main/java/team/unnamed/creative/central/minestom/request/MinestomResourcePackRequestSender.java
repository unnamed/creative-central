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
package team.unnamed.creative.central.minestom.request;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.resourcepack.ResourcePack;
import team.unnamed.creative.central.request.ResourcePackRequest;
import team.unnamed.creative.central.request.ResourcePackRequestSender;

public final class MinestomResourcePackRequestSender implements ResourcePackRequestSender {

    private static final ResourcePackRequestSender INSTANCE = new MinestomResourcePackRequestSender();

    private MinestomResourcePackRequestSender() {
    }

    @Override
    public void send(Object playerObject, ResourcePackRequest request) {
        if (!(playerObject instanceof Player player)) {
            throw new IllegalArgumentException("Provided 'player' is not an actual Minestom Player: " + playerObject);
        }

        String url = request.url();
        String hash = request.hash();
        Component prompt = request.prompt();

        player.setResourcePack(
                request.required()
                        ? ResourcePack.forced(url, hash, prompt)
                        : ResourcePack.optional(url, hash, prompt)
        );
    }

    public static ResourcePackRequestSender minestom() {
        return INSTANCE;
    }

}
