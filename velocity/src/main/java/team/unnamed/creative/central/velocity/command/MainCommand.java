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
package team.unnamed.creative.central.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import team.unnamed.creative.central.velocity.CreativeCentralPlugin;
import team.unnamed.creative.central.common.util.Components;
import team.unnamed.creative.central.request.ResourcePackRequest;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainCommand implements SimpleCommand {

    private final CreativeCentralPlugin central;
    private final Map<String, Component> messageCache = new ConcurrentHashMap<>();

    public MainCommand(CreativeCentralPlugin central) {
        this.central = central;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        String subcommand = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);

        switch (subcommand) {
            case "reload" -> {
                if (!sender.hasPermission("creative-central.command.reload")) {
                    send(sender, "command.no-permission.reload");
                    return;
                }

                // more than one argument, we are being strict, show usage
                if (args.length != 1) {
                    send(sender, "command.usage.reload");
                    return;
                }

                central.reloadConfig();
                messageCache.clear();
                central.generate().thenAccept(resourcePack ->
                    send(sender, "command.feedback.reload"));
            }

            case "apply" -> {
                if (sender.getPermissionValue("creative-central.command.apply") == Tristate.FALSE) {
                    send(sender, "command.no-permission.apply.self");
                    return;
                }

                Collection<? extends Player> targets;

                if (sender.hasPermission("creative-central.command.apply.others")) {
                    if (args.length != 1 && args.length != 2) {
                        send(sender, "command.usage.apply.others");
                        return;
                    }

                    if (args.length == 2 && !args[1].equals("@p")) {
                        // other argument
                        if (args[1].equals("@a")) {
                            targets = central.getProxy().getAllPlayers();
                            if (targets.isEmpty()) {
                                send(sender, "command.apply.no-players");
                                return;
                            }
                        } else {
                            Player player = central.getProxy().getPlayer(args[1]).orElse(null);
                            if (player == null) {
                                send(sender, "command.apply.player-not-found");
                                return;
                            }

                            targets = Collections.singleton(player);
                        }
                    } else {
                        if (!(sender instanceof Player player)) {
                            send(sender, "command.apply.no-player");
                            return;
                        }

                        targets = Collections.singleton(player);
                    }
                } else {
                    // does not have permission to send to others
                    if (args.length == 2) {
                        send(sender, "command.no-permission.apply.others");
                        return;
                    } else if (args.length != 1) {
                        send(sender, "command.usage.apply.self");
                        return;
                    }

                    if (!(sender instanceof Player player)) {
                        send(sender, "command.apply.no-player");
                        return;
                    }

                    // apply to self
                    targets = Collections.singleton(player);
                }

                ResourcePackRequest request = central.serveOptions().request();
                if (request == null) {
                    send(sender, "command.apply.no-resource-pack");
                    return;
                }

                // send
                for (Player target : targets) {
                    central.requestSender().send(target, request);
                }
                send(sender, "command.feedback.apply");
            }

            case "help", "?" -> {
                if (sender.getPermissionValue("creative-central.command.help") == Tristate.FALSE) {
                    send(sender, "command.no-permission.help");
                    return;
                }

                send(sender, "command.help");
            }

            // covers 'help', '?' and any other unknown subcommand
            default ->
                send(sender, "command.usage.unknown");
        }
    }


    private void send(CommandSource sender, String messageKey) {
        Component message = messageCache.computeIfAbsent(messageKey, k ->
                Components.deserialize(central.rawConfig().node((Object[]) k.split("\\.")).getString(k)));
        sender.sendMessage(message);
    }

}
