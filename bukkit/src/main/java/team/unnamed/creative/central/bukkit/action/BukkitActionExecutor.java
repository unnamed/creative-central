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
package team.unnamed.creative.central.bukkit.action;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.central.common.action.Action;
import team.unnamed.creative.central.common.action.ActionExecutor;
import team.unnamed.creative.central.common.action.KickAction;
import team.unnamed.creative.central.common.action.MessageAction;
import team.unnamed.creative.central.common.action.TitleAction;

public final class BukkitActionExecutor implements ActionExecutor<Player> {
    private static final ActionExecutor<Player> INSTANCE = new BukkitActionExecutor();

    private static final boolean ARE_TITLE_TIMES_AVAILABLE;

    static {
        boolean areTitleTimesAvailable = false;
        try {
            Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            areTitleTimesAvailable = true;
        } catch (final NoSuchMethodException ignored) {
        }
        ARE_TITLE_TIMES_AVAILABLE = areTitleTimesAvailable;
    }

    private BukkitActionExecutor() {
    }

    @Override
    @SuppressWarnings({"deprecation", "UsagesOfObsoleteApi"}) // Spigot!
    public void execute(Action action, Player player) {
        if (action instanceof MessageAction) {
            player.sendMessage(toLegacy(((MessageAction) action).message()));
        } else if (action instanceof TitleAction) {
            final Title title = ((TitleAction) action).title();
            final Title.Times times = title.times();
            if (times == null || !ARE_TITLE_TIMES_AVAILABLE) {
                player.sendTitle(
                        toLegacy(title.title()),
                        toLegacy(title.subtitle())
                );
            } else {
                player.sendTitle(
                        toLegacy(title.title()),
                        toLegacy(title.subtitle()),
                        (int) times.fadeIn().getSeconds() * 20,
                        (int) times.stay().getSeconds() * 20,
                        (int) times.fadeOut().getSeconds() * 20
                );
            }
        } else if (action instanceof KickAction kickAction) {
            player.kickPlayer(toLegacy(kickAction.reason()));
        } else {
            throw new IllegalArgumentException("Unknown action type: '" + action + "'");
        }
    }

    private static @NotNull String toLegacy(final @NotNull Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static ActionExecutor<Player> bukkit() {
        return INSTANCE;
    }
}
