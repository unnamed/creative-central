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
package team.unnamed.creative.central.common.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActionParser {

    public static List<Action> parse(Object object) {
        List<?> rawActions = (List<?>) object;
        if (rawActions == null) {
            // no actions with this key
            return Collections.emptyList();
        }
        List<Action> actions = new ArrayList<>(rawActions.size());
        for (Object rawAction : rawActions) {
            String actionId;
            Object actionData;

            if (rawAction instanceof Map<?,?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) rawAction;
                if (map.size() != 1) {
                    throw new IllegalArgumentException(
                            "Found an invalid action, invalid format.\n"
                            + "    The format MUST be:\n"
                            + "        declined:\n"
                            + "            - kick: 'Kicked!'\n"
                            + "            - message: 'Kicking...'\n"
                            + "       ^^^^^^^^^^^^^^^^\n"
                            + "    It seems like you forgot the '-' before the action name\n"
                            + "    Check the plugin configuration!\n"
                    );
                }

                actionId = map.keySet().iterator().next();
                actionData = map.get(actionId);
            } else {
                throw new IllegalArgumentException(
                        "Found an invalid action, invalid format.\n"
                        + "    The format MUST be:\n"
                        + "        declined:\n"
                        + "            - kick: 'Message'\n"
                        + "             ^^^^^^^^^^^^^^^^^^\n"
                        +"     It seems like you forgot the action identifier (e.g. 'kick:')\n"
                        +"     Check the plugin configuration!\n"
                );
            }

            Action action;
            switch (actionId.toLowerCase(Locale.ROOT)) {
                case KickAction.IDENTIFIER: {
                    action = KickAction.deserialize(actionData);
                    break;
                }
                case MessageAction.IDENTIFIER: {
                    action = MessageAction.deserialize(actionData);
                    break;
                }
                case TitleAction.IDENTIFIER: {
                    action = TitleAction.deserialize(actionData);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown action identifier: " + actionId);
            }
            actions.add(action);
        }
        return actions;
    }

}