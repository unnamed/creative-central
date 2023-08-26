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
package team.unnamed.creative.central.minestom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerResourcePackStatusEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extensions.Extension;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.central.CreativeCentral;
import team.unnamed.creative.central.CreativeCentralProvider;
import team.unnamed.creative.central.common.config.Configuration;
import team.unnamed.creative.central.common.config.YamlConfigurationLoader;
import team.unnamed.creative.central.common.event.EventBusImpl;
import team.unnamed.creative.central.common.event.EventExceptionHandler;
import team.unnamed.creative.central.common.server.CommonResourcePackServer;
import team.unnamed.creative.central.common.util.Monitor;
import team.unnamed.creative.central.event.EventBus;
import team.unnamed.creative.central.event.pack.ResourcePackStatusEvent;
import team.unnamed.creative.central.minestom.command.MainCommand;
import team.unnamed.creative.central.minestom.listener.CreativeResourcePackStatusListener;
import team.unnamed.creative.central.minestom.listener.ResourcePackSendListener;
import team.unnamed.creative.central.minestom.listener.ResourcePackStatusListener;
import team.unnamed.creative.central.minestom.request.MinestomResourcePackRequestSender;
import team.unnamed.creative.central.request.ResourcePackRequestSender;
import team.unnamed.creative.central.server.CentralResourcePackServer;
import team.unnamed.creative.central.server.ServeOptions;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class CreativeCentralExtension extends Extension implements CreativeCentral {

    private ServeOptions serveOptions;
    private EventBus eventBus;
    private ResourcePackRequestSender requestSender;
    private CentralResourcePackServer resourcePackServer;

    @Override
    public void initialize() {
        Configuration config = YamlConfigurationLoader.load(getResource("config.yml"));
        Monitor<Configuration> configMonitor = Monitor.monitor(config);

        this.serveOptions = new ServeOptions();
        this.eventBus = new EventBusImpl<>(
                Extension.class,
                EventExceptionHandler.logging((message, exception) -> getLogger().warn(message, exception))
        );
        this.requestSender = MinestomResourcePackRequestSender.minestom();
        this.resourcePackServer = new CommonResourcePackServer();

        // load serve/send options
        serveOptions.serve(true);
        serveOptions.delay(config.send().delay());

        // register event listeners
        var sendListener = new ResourcePackSendListener(this);
        var statusListener = new ResourcePackStatusListener(this);
        getEventNode().addListener(PlayerSpawnEvent.class, sendListener::onJoin);
        getEventNode().addListener(PlayerResourcePackStatusEvent.class, statusListener::onResourcePackStatus);

        // register our command
        MinecraftServer.getCommandManager().register(new MainCommand());

        // load actions
        eventBus.listen(this, ResourcePackStatusEvent.class, new CreativeResourcePackStatusListener(configMonitor));

        registerService();
    }

    @Override
    public void terminate() {
        eventBus = null;
        requestSender = null;
        serveOptions = null;

        if (resourcePackServer != null) {
            try {
                resourcePackServer.close();
            } catch (IOException e) {
                getLogger().warn("Failed to close resource pack server", e);
            }
            resourcePackServer = null;
        }

        unregisterService();
    }

    @Override
    public CompletableFuture<ResourcePack> generate() {
        return null;
    }

    private void registerService() {
        CreativeCentralProvider.set(this);
    }

    private void unregisterService() {
        CreativeCentralProvider.unset();
    }

    @Override
    public CentralResourcePackServer server() {
        return resourcePackServer;
    }

    @Override
    public ServeOptions serveOptions() {
        return serveOptions;
    }

    @Override
    public ResourcePackRequestSender requestSender() {
        return requestSender;
    }

    @Override
    public EventBus eventBus() {
        return eventBus;
    }

}
