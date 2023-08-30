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
package team.unnamed.creative.central.event.pack;

import team.unnamed.creative.central.event.Event;
import team.unnamed.creative.central.pack.ResourcePackStatus;

import static java.util.Objects.requireNonNull;

/**
 * An event called when a resource-pack status is received from
 * a player. This is normally after the player fails, succeeds
 * or rejects the resource-pack when they are asked to download it.
 *
 * @since 1.0.0
 */
public final class ResourcePackStatusEvent implements Event {

    private final Object player;
    private final ResourcePackStatus status;

    /**
     * Instantiate a new {@link ResourcePackStatusEvent}
     *
     * @param player The player
     * @param status The resource-pack status
     * @since 1.0.0
     */
    public ResourcePackStatusEvent(Object player, ResourcePackStatus status) {
        this.player = requireNonNull(player, "player");
        this.status = requireNonNull(status, "status");
    }

    /**
     * Returns the player who send the status.
     * The type depends on the server platform.
     *
     * @return The player object.
     * @since 1.0.0
     */
    public Object player() {
        return player;
    }

    /**
     * Returns the received resource-pack status,
     *
     * @return The resource-pack status
     * @since 1.0.0
     * @see ResourcePackStatus
     */
    public ResourcePackStatus status() {
        return status;
    }

    @Override
    public String toString() {
        return "ResourcePackStatusEvent{" +
                "player=" + player +
                ", status=" + status +
                '}';
    }

}
