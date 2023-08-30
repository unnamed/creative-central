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
package team.unnamed.creative.central;

import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.central.event.EventBus;
import team.unnamed.creative.central.event.pack.ResourcePackGenerateEvent;
import team.unnamed.creative.central.request.ResourcePackRequestSender;
import team.unnamed.creative.central.server.CentralResourcePackServer;
import team.unnamed.creative.central.server.ServeOptions;

import java.util.concurrent.CompletableFuture;

/**
 * Main interface for the creative-central system
 *
 * @since 1.0.0
 */
public interface CreativeCentral {

    /**
     * Generates or re-generates the server's resource-pack.
     *
     * <p>This will fire the {@link ResourcePackGenerateEvent}
     * event, making all the registered plugins add their
     * resources into the resource-pack</p>
     *
     * <p>After the resource-pack is completed, it is built
     * to a {@link BuiltResourcePack} and exported.</p>
     *
     * <p>If it was exported to somewhere we know the location
     * (URL and Hash), the resource-pack is applied to all the
     * online players</p>
     *
     * @return A completable future of the generated resource-pack
     * @since 1.0.0
     */
    CompletableFuture<ResourcePack> generate();

    /**
     * Gets the {@link EventBus} instance used for everything
     * within the creative-central system
     *
     * @return The held event bus instance
     * @since 1.0.0
     */
    EventBus eventBus();

    CentralResourcePackServer server();

    ServeOptions serveOptions();

    ResourcePackRequestSender requestSender();

}
