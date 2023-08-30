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
package team.unnamed.creative.central.pack;

import team.unnamed.creative.central.request.ResourcePackRequest;

/**
 * The status of a resource-pack in a Minecraft client.
 * Status are responses to {@link ResourcePackRequest}s
 * that were been sent to players.
 *
 * @since 1.0.0
 */
public enum ResourcePackStatus {

    /**
     * Means that the player just accepted the server's
     * resource-pack.
     *
     * <p>Sent when the player clicks the 'Accept' button
     * when they are asked to use the server's resource-pack</p>
     *
     * <p>Also sent when the player already accepted the
     * server's resource-pack and just joined the server.
     * (they are not asked twice)</p>
     *
     * @since 1.0.0
     */
    ACCEPTED,

    /**
     * Means that the player just finished loading the
     * server's resource-pack.
     *
     * <p>Commonly sent a little bit after the
     * {@link ResourcePackStatus#ACCEPTED} status is sent</p>
     *
     * @since 1.0.0
     */
    LOADED,

    /**
     * Means that the player declined the server's
     * resource-pack.
     *
     * <p>Similar to {@link ResourcePackStatus#ACCEPTED},
     * this status is sent when the player clicks the
     * 'Reject' button when they are asked to use the
     * server's resource-pack. Or when they have already
     * rejected the resource-pack and just joined the
     * server.</p>
     *
     * @since 1.0.0
     */
    DECLINED,

    /**
     * Means that the player failed to download the
     * server's resource-pack.
     *
     * <p>This could mean multiple things like, an
     * invalid URL was sent, or an invalid resource-pack
     * was sent.</p>
     *
     * @since 1.0.0
     */
    FAILED

}
