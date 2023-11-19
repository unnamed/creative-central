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
import org.bukkit.entity.Player;
import team.unnamed.creative.central.common.action.Action;
import team.unnamed.creative.central.common.action.ActionExecutor;
import team.unnamed.creative.central.common.action.AudienceActionExecutor;
import team.unnamed.creative.central.common.action.KickAction;

public final class PaperActionExecutor extends AudienceActionExecutor<Player> {
    private static final ActionExecutor<Player> INSTANCE = new PaperActionExecutor();

    private PaperActionExecutor() {
    }

    @Override
    protected void executeAction(Action action, Player player) {
        if (action instanceof KickAction kickAction) {
            player.kick(kickAction.reason());
        } else {
            throw new IllegalArgumentException("Unknown action type: '" + action + "'");
        }
    }

    public static ActionExecutor<Player> paper() {
        return INSTANCE;
    }

    public static boolean isAvailable() {
        try {
            Player.class.getMethod("sendMessage", Component.class);
            return true;
        } catch (final NoSuchMethodException e) {
            return false;
        }
    }
}
