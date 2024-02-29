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
package team.unnamed.creative.central.velocity.request;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import team.unnamed.creative.central.request.ResourcePackRequest;
import team.unnamed.creative.central.request.ResourcePackRequestSender;

import java.util.function.BiConsumer;

public final class VelocityResourcePackRequestSender implements ResourcePackRequestSender {

    private final BiConsumer<Player, ResourcePackRequest> delegate;

    private VelocityResourcePackRequestSender(BiConsumer<Player, ResourcePackRequest> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void send(Object playerObject, ResourcePackRequest request) {
        if (!(playerObject instanceof Player)) {
            throw new IllegalArgumentException("Provided 'player' is not an actual Velocity Player: " + playerObject);
        }

        delegate.accept((Player) playerObject, request);
    }

    public static ResourcePackRequestSender velocity(ProxyServer proxy) {

        return new VelocityResourcePackRequestSender((player, request) -> {
            // convert to hash byte array
            String hs = request.hash();
            int len = hs.length();
            byte[] hash = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                hash[i / 2] = (byte) ((Character.digit(hs.charAt(i), 16) << 4)
                        + Character.digit(hs.charAt(i + 1), 16));
            }

            player.sendResourcePackOffer(proxy.createResourcePackBuilder(request.uri().toString())
                    .setHash(hash)
                    .setShouldForce(request.required())
                    .setPrompt(request.prompt())
                    .build()
            );
        });

    }

}
