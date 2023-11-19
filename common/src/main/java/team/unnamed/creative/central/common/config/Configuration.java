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
package team.unnamed.creative.central.common.config;

import team.unnamed.creative.central.common.action.Action;
import team.unnamed.creative.central.pack.ResourcePackStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {

    // Settings for the resource pack sending. In this section, we specify
    // some options for the resource pack request and some extra options on
    // how and when to send the resource pack to the player
    private final SendConfiguration send = new SendConfiguration();

    // specifies the actions to perform when the player
    // accepts, declines, loads or fails to download the resource pack
    private final Map<ResourcePackStatus, List<Action>> feedback = new HashMap<>();

    // settings for Resource Pack exporting, in this section we specify
    // how to export the generated resource pack
    private final ExportConfiguration export = new ExportConfiguration();

    private final Map<String, String> messages = new HashMap<>();

    // specifies what "What is my ip" services we can use in order to obtain
    // the server's IP address (which is sent to the client to download the
    // resource pack)
    private final List<String> whatIsMyIpServices = new ArrayList<>();

    public SendConfiguration send() {
        return send;
    }

    public Map<ResourcePackStatus, List<Action>> feedback() {
        return feedback;
    }

    public ExportConfiguration export() {
        return export;
    }

    public Map<String, String> messages() {
        return messages;
    }

    public List<String> whatIsMyIpServices() {
        return whatIsMyIpServices;
    }

}