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
package team.unnamed.creative.central.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.central.CreativeCentral;
import team.unnamed.creative.central.CreativeCentralProvider;
import team.unnamed.creative.central.common.config.Configuration;
import team.unnamed.creative.central.common.config.ExportConfiguration;
import team.unnamed.creative.central.common.config.YamlConfigurationLoader;
import team.unnamed.creative.central.common.event.EventBusImpl;
import team.unnamed.creative.central.common.event.EventExceptionHandler;
import team.unnamed.creative.central.common.export.ResourcePackExporterFactory;
import team.unnamed.creative.central.common.server.CommonResourcePackServer;
import team.unnamed.creative.central.common.util.Components;
import team.unnamed.creative.central.common.util.LocalAddressProvider;
import team.unnamed.creative.central.common.util.Monitor;
import team.unnamed.creative.central.common.util.Streams;
import team.unnamed.creative.central.event.EventBus;
import team.unnamed.creative.central.event.pack.ResourcePackGenerateEvent;
import team.unnamed.creative.central.event.pack.ResourcePackStatusEvent;
import team.unnamed.creative.central.export.ResourcePackExporter;
import team.unnamed.creative.central.export.ResourcePackLocation;
import team.unnamed.creative.central.request.ResourcePackRequest;
import team.unnamed.creative.central.request.ResourcePackRequestSender;
import team.unnamed.creative.central.server.CentralResourcePackServer;
import team.unnamed.creative.central.server.ServeOptions;
import team.unnamed.creative.central.velocity.command.MainCommand;
import team.unnamed.creative.central.velocity.external.ExternalResourcePackProvider;
import team.unnamed.creative.central.velocity.external.ExternalResourcePackProviders;
import team.unnamed.creative.central.velocity.listener.CreativeResourcePackStatusListener;
import team.unnamed.creative.central.velocity.listener.ResourcePackSendListener;
import team.unnamed.creative.central.velocity.listener.ResourcePackStatusListener;
import team.unnamed.creative.central.velocity.request.VelocityResourcePackRequestSender;
import team.unnamed.creative.central.velocity.util.PluginResources;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.resources.MergeStrategy;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.LogManager;

@Plugin(
        id = "creative-central", version = "1.3.0-dev", authors = {"Unnamed Team", "itsTyrion"},
        description = "Creative central, the resource-pack unifier",
        url = "https://unnamed.team/docs/creative/central/creative-central"
)
public final class CreativeCentralPlugin implements CreativeCentral {

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataFolder;
    private final PluginDescription description;
    private final Metrics.Factory metricsFactory;
    private CommentedConfigurationNode rawConfig;

    @Inject
    public CreativeCentralPlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataFolder,
                                 PluginDescription description, Metrics.Factory metricsFactory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataFolder = dataFolder;
        this.description = description;
        this.metricsFactory = metricsFactory;
    }

    private ServeOptions serveOptions;
    private EventBus eventBus;
    private ResourcePackRequestSender requestSender;
    private CentralResourcePackServer resourcePackServer;

    private Monitor<Configuration> configurationMonitor;


    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        Configuration config = YamlConfigurationLoader.load(PluginResources.get(this, "config.yml"));
        this.configurationMonitor = Monitor.monitor(config);
        reloadConfig();

        metricsFactory.make(this, 20718); // metrics (bstats.org)

        serveOptions = new ServeOptions();
        eventBus = new EventBusImpl<>(Object.class, EventExceptionHandler.logging(logger::warn));
        requestSender = VelocityResourcePackRequestSender.velocity(proxy);
        resourcePackServer = new CommonResourcePackServer();

        // load serve/send options
        serveOptions.serve(true);
        serveOptions.delay(config.send().delay());

        // register event listeners
        listen(
                new ResourcePackStatusListener(this),
                new ResourcePackSendListener(this)
        );

        // register our command
        proxy.getCommandManager().register("vcentral", new MainCommand(this));

        // load actions
        eventBus.listen(this, ResourcePackStatusEvent.class, new CreativeResourcePackStatusListener(configurationMonitor));

        // start resource pack server if enabled
        loadResourcePackServer();

        // register service providers
        registerService();

        // generate the resource-pack for the first time
        generateFirstLoad();
    }

    public Monitor<Configuration> config() {
        return configurationMonitor;
    }

    private void registerService() {
        // Services are not YET a feature on Velocity, there's an open maintainer PR, though. recheck occasionally?
//        Bukkit.getServicesManager().register(CreativeCentral.class, this, this, ServicePriority.High);
        CreativeCentralProvider.set(this);
    }

    private void unregisterService() {
        CreativeCentralProvider.unset();
        // Services are not YET a feature on Velocity, there's an open maintainer PR, though.
//        Bukkit.getServicesManager().unregister(CreativeCentral.class, this);
    }

    private void loadResourcePackServer() {
        ExportConfiguration.LocalHostExportConfiguration config = config().get().export().localHost();

        if (!config.enabled()) {
            return;
        }

        getLogger().info("Resource-pack server enabled, starting...");

        String address = config.address();
        String publicAddress = config.publicAddress();
        int port = config.port();

        // if address is empty, automatically detect the server's address
        if (address.trim().isEmpty()) {
            try {
                address = LocalAddressProvider.getLocalAddress(configurationMonitor.get().whatIsMyIpServices());
            } catch (IOException e) {
                getLogger().error("An exception was caught when trying to get the local server address", e);
            }

            if (address == null) {
                getLogger().error("Couldn't get the local server address");
            }
        }

        if (publicAddress == null || publicAddress.trim().isEmpty()) {
            publicAddress = address;
        }

        if (address != null) {
            try {
                resourcePackServer.open(address, publicAddress, port);
                getLogger().info("Successfully started the resource-pack server, listening on port " + port);
            } catch (IOException e) {
                getLogger().error("Failed to open the resource pack server", e);
            }
        }
    }

    private void generateFirstLoad() {
        final var allProviders = ExternalResourcePackProviders.get();
        final var awaitingProviders = new ArrayList<ExternalResourcePackProvider>(allProviders.length);

        for (final var provider : allProviders) {
            if (proxy.getPluginManager().getPlugin(provider.pluginName()).isEmpty()) {
                continue;
            }

            getLogger().info("Found " + provider.pluginName() + ", registering as an external resource pack provider...");

            final AtomicBoolean generateCalledByChangeOnThisProvider = new AtomicBoolean(false);

            eventBus.listen(this, ResourcePackGenerateEvent.class, event -> {
                final var externalResourcePack = provider.load();
                if (externalResourcePack != null) {
                    getLogger().info("Merging resource pack from external provider: " + provider.pluginName());
                    event.resourcePack().merge(externalResourcePack, MergeStrategy.mergeAndKeepFirstOnError());
                } else {
                    getLogger().warn("Couldn't load resource pack from external provider: " + provider.pluginName() + ": Not found");
                }
            });

            if (provider.awaitOnStart()) {
                awaitingProviders.add(provider);
            }
        }

        if (awaitingProviders.isEmpty()) {
            // do not wait for anything
            proxy.getScheduler().buildTask(this, () -> generate().whenComplete((pack, throwable) -> {
                if (throwable != null) {
                    getLogger().error("Error while generating resource pack", throwable);
                }
            })).delay(Duration.ofMillis(50)).schedule();
        } else {
            getLogger().info("Waiting on " + awaitingProviders.size() + " external resource-pack providers to finish generating...");
            final var awaitingResourcePackCountDown = new AtomicInteger(awaitingProviders.size());
            for (final var provider : awaitingProviders) {
                provider.listenForChanges(this, () -> {
                    synchronized (awaitingResourcePackCountDown) {
                        final boolean generatePack;

                        if (awaitingResourcePackCountDown.get() == 0) {
                            // this means that the change on this provider was made
                            // AFTER the first generation
                            generatePack = true;
                        } else {
                            // this means that the change on this provider was made
                            // DURING the first generation
                            getLogger().info(provider.pluginName() + " finished generating its resource-pack...");

                            // will be true if this is the last provider to be loaded
                            generatePack = awaitingResourcePackCountDown.decrementAndGet() == 0;
                        }

                        if (generatePack) {
                            getLogger().info("All external resource-packs finished generating...");
                            proxy.getScheduler().buildTask(this, () -> generate().whenComplete((pack, throwable) -> {
                                if (throwable != null) {
                                    getLogger().error("Error while generating resource pack", throwable);
                                }
                            })).delay(Duration.ofMillis(50)).schedule();
                        }
                    }
                });
            }
        }
    }

    public ResourcePack generateSync() {
        if (eventBus == null) {
            throw new IllegalStateException("Unexpected status, event bus was null when trying to" +
                    " generate the resource pack. Is the server shutting down?");
        }

        Configuration config = configurationMonitor.get();

        Path resourcesFolder = dataFolder.resolve("resources");
        if (!Files.exists(resourcesFolder)) {
            try {
                Files.createDirectories(resourcesFolder);
                // copy pack.mcmeta and pack.png inside resources
                try (InputStream meta = getClass().getResourceAsStream("/resources/pack.mcmeta")) {
                    Streams.pipeToFile(meta, resourcesFolder.resolve("pack.mcmeta"));
                }

                try (InputStream icon = getClass().getResourceAsStream("/resources/pack.png")) {
                    Streams.pipeToFile(icon, resourcesFolder.resolve("pack.png"));
                }
            } catch (IOException e) {
                getLogger().warn("Failed to copy pack.mcmeta and pack.png files" +
                        " inside the resources folder", e);
            }
        }

        ResourcePack resourcePack = Files.exists(resourcesFolder)
                ? MinecraftResourcePackReader.minecraft().readFromDirectory(resourcesFolder.toFile())
                : ResourcePack.resourcePack();

        // process the pack meta
        {
            PackMeta meta = resourcePack.packMeta();
            if (meta == null) {
                getLogger().warn("Couldn't find pack metadata in the generated resource-pack");
            } else {
                resourcePack.packMeta(
                        meta.formats().format(),
                        // reprocess description using MiniMessage
                        Components.deserialize(meta.description())
                );
            }
        }

        eventBus.call(ResourcePackGenerateEvent.class, new ResourcePackGenerateEvent(resourcePack));
        getLogger().info("The resource pack has been generated successfully");

        // export resource-pack
        @Nullable ResourcePackLocation location = null;
        {
            getLogger().info("Exporting resource pack...");
            String exportType = config.export().type();
            var julLogger = LogManager.getLogManager().getLogger(description.getId());
            ResourcePackExporter exporter =
                    ResourcePackExporterFactory.create(exportType, dataFolder.toFile(), resourcePackServer, julLogger);
            try {
                location = exporter.export(resourcePack);
            } catch (IOException e) {
                getLogger().error("Failed to export resource pack", e);
            }
        }

        if (location != null) {
            getLogger().info("Exported resource pack to " + location.uri() + " (" + location.hash() + ")");
            serveOptions.request(ResourcePackRequest.of(
                    location.uri(),
                    location.hash(),
                    config.send().request().required(),
                    Components.deserialize(config.send().request().prompt())
            ));
        } else {
            serveOptions.request(null);
            getLogger().warn("Resource-pack has not been exported to a hosted server, the"
                    + " resource-pack will not be automatically sent to players.");
        }

        if (location != null) {
            // apply the resource-pack to online players
            for (Player player : proxy.getAllPlayers()) {
                requestSender.send(player, serveOptions.request());
            }
        }

        return resourcePack;
    }

    @Override
    public CompletableFuture<ResourcePack> generate() {
        if (eventBus == null) {
            throw new IllegalStateException("Unexpected status, event bus was null when trying to" +
                    " generate the resource pack. Is the server shutting down?");
        }

        return CompletableFuture.supplyAsync(
                this::generateSync,
                task -> proxy.getScheduler().buildTask(this, task).schedule()
        );
    }

    public void reloadConfig() {
        try {
            rawConfig = org.spongepowered.configurate.yaml.YamlConfigurationLoader.builder()
                    .path(dataFolder.resolve("config.yml")).build().load();
        } catch (IOException ex) {
            logger.error("Unable to load config.yml", ex);
        }
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
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

    public ProxyServer getProxy() {
        return proxy;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataFolder() {
        return dataFolder;
    }

    private void listen(Object... listeners) {
        for (Object listener : listeners) {
            proxy.getEventManager().register(this, listener);
        }
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

    public CommentedConfigurationNode rawConfig() {
        return rawConfig;
    }
}