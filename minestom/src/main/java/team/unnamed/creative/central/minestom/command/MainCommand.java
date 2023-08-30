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
package team.unnamed.creative.central.minestom.command;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.permission.Permission;
import net.minestom.server.utils.entity.EntityFinder;
import team.unnamed.creative.central.CreativeCentral;
import team.unnamed.creative.central.common.util.Permissions;
import team.unnamed.creative.central.request.ResourcePackRequest;

import java.util.List;

public class MainCommand extends Command {

    private static final Permission HELP_COMMAND_PERMISSION = new Permission(Permissions.HELP_COMMAND);
    private static final Permission RELOAD_COMMAND_PERMISSION = new Permission(Permissions.RELOAD_COMMAND);
    private static final Permission APPLY_COMMAND_PERMISSION = new Permission(Permissions.APPLY_COMMAND);
    private static final Permission APPLY_OTHERS_COMMAND_PERMISSION = new Permission(Permissions.APPLY_OTHERS_COMMAND);

    public MainCommand(CreativeCentral central) {
        super("central");

        // "reload"
        addConditionalSyntax(
                (sender, commandString) -> {
                    if (sender.hasPermission(RELOAD_COMMAND_PERMISSION)) {
                        return true;
                    } else if (commandString != null) {
                        send(sender, "command.no-permission.reload");
                    }
                    return false;
                },
                (sender, context) -> {
                    //central.reloadConfig();
                    //messageCache.clear();
                    central.generate().thenAccept(resourcePack -> send(sender, "command.feedback.reload"));
                },
                ArgumentType.Literal("reload")
        );

        // apply self
        addConditionalSyntax(
                (sender, commandString) -> {
                    //if (sender instanceof Player && sender.hasPermission(APPLY_COMMAND_PERMISSION)) {
                    //    return true;
                    //} else if (commandString != null) {
                    //    send(sender, "command.no-permission.apply.self");
                    //}
                    return false;
                },
                (sender, context) -> {
                    // Player player = (Player) sender;
                    ResourcePackRequest request = central.serveOptions().request();
                    if (request == null) {
                       // send(sender, "command.apply.no-resource-pack");
                    }

                    // send
                    // central.requestSender().send(player, request);
                    // send(sender, "command.feedback.apply");
                },
                ArgumentType.Literal("apply")
        );

        // apply others
        var targetArgument = ArgumentType.Entity("target")
                .singleEntity(false)
                .onlyPlayers(true)
                .setDefaultValue(new EntityFinder()
                        .setTargetSelector(EntityFinder.TargetSelector.SELF));
        addConditionalSyntax(
                (sender, commandArg) -> {
                    if (sender.hasPermission(APPLY_OTHERS_COMMAND_PERMISSION)) {
                        return true;
                    } else if (commandArg != null) {
                        send(sender, "command.no-permission.apply.others");
                    }
                    return false;
                },
                (sender, context) -> {
                    EntityFinder entityFinder = context.get(targetArgument);
                    List<Entity> entities = entityFinder.find(sender);

                    if (entities.isEmpty()) {
                        send(sender, "command.apply.no-players");
                        return;
                    }

                    for (Entity entity : entities) {
                        if (entity instanceof Player player) {
                            // central.requestSender().send(player, request);
                        }
                    }
                    send(sender, "command.feedback.apply");
                },
                ArgumentType.Literal("apply"),
                targetArgument
        );

        // "help", "?"
        CommandCondition condition = (sender, commandString) -> {
            if (sender.hasPermission(HELP_COMMAND_PERMISSION)) {
                return true;
            } else if (commandString != null) {
                send(sender, "command.no-permission.help");
            }
            return false;
        };
        CommandExecutor helpExecutor = (sender, context) -> send(sender, "command.help");
        addConditionalSyntax(condition, helpExecutor, ArgumentType.Literal("help"));
        addConditionalSyntax(condition, helpExecutor, ArgumentType.Literal("?"));

        // any others
        setDefaultExecutor((sender, context) ->
                send(sender, "command.usage.unknown"));
    }

    private void send(CommandSender player, String key) {

    }

}
