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
package team.unnamed.creative.central.bukkit.request;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import team.unnamed.creative.central.request.ResourcePackRequest;
import team.unnamed.creative.central.request.ResourcePackRequestSender;

import java.util.function.BiConsumer;

public final class BukkitResourcePackRequestSender implements ResourcePackRequestSender {

    private final BiConsumer<Player, ResourcePackRequest> delegate;

    private BukkitResourcePackRequestSender(BiConsumer<Player, ResourcePackRequest> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void send(Object playerObject, ResourcePackRequest request) {
        if (!(playerObject instanceof Player)) {
            throw new IllegalArgumentException("Provided 'player' is not an actual Bukkit Player: " + playerObject);
        }

        delegate.accept((Player) playerObject, request);
    }

    @SuppressWarnings("deprecation")
    public static ResourcePackRequestSender bukkit() {

        if (isSetResourcePackOverrideAvailable(String.class, String.class, boolean.class, Component.class)) {
            // Method that accepts: URL, Hash, Required and Prompt options,
            // available since Paper 1.17 (We are currently on 1.20)
            return new BukkitResourcePackRequestSender((player, request) -> player.setResourcePack(
                    request.uri().toString(),
                    request.hash(),
                    request.required(),
                    request.prompt()
            ));
        }

        if (isSetResourcePackOverrideAvailable(String.class, String.class)) {
            // Method that accepts URL and Hash options
            // available since Paper 1.12 and probably before too
            return new BukkitResourcePackRequestSender((player, request) -> player.setResourcePack(
                    request.uri().toString(),
                    request.hash()
            ));
        }

        if (isSetResourcePackOverrideAvailable(String.class, byte[].class)) {
            // Stupid method that accepts URL and Hash options,
            // but only accepts hash in a byte array. It's stupid
            // because it will internally convert the byte array to
            // a string again, to send it using the resource pack
            // packet
            return new BukkitResourcePackRequestSender((player, request) -> {
                // convert to hash byte aray
                String hs = request.hash();
                int len = hs.length();
                byte[] hash = new byte[len / 2];
                for (int i = 0; i < len; i += 2) {
                    hash[i / 2] = (byte) ((Character.digit(hs.charAt(i), 16) << 4)
                            + Character.digit(hs.charAt(i + 1), 16));
                }

                // now call the stupid method
                player.setResourcePack(request.uri().toString(), hash);
            });
        }

        if (isSetResourcePackOverrideAvailable(String.class)) {
            // Method that accepts URL option only
            // This is ancient! Available in Spigot 1.8.8
            return new BukkitResourcePackRequestSender((player, request) -> player.setResourcePack(
                    request.uri().toString()
            ));
        }

        throw new IllegalStateException("No supported setResourcePack method found on Player!");
    }

    private static boolean isSetResourcePackOverrideAvailable(Class<?>... argumentTypes) {
        try {
            Player.class.getDeclaredMethod("setResourcePack", argumentTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

}
