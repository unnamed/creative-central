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
package team.unnamed.creative.central.common.config;

import org.yaml.snakeyaml.Yaml;
import team.unnamed.creative.central.common.action.ActionParser;
import team.unnamed.creative.central.pack.ResourcePackStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class YamlConfigurationLoader {

    private YamlConfigurationLoader() {
    }

    @SuppressWarnings("unchecked")
    public static Configuration load(InputStream stream) {
        Map<String, ?> data;
        try {
            Yaml yaml = new Yaml();
            data = yaml.load(stream);
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }

        Configuration config = new Configuration();

        // read the 'send' section
        Map<String, Object> send = (Map<String, Object>) data.get("send");
        Map<String, Object> request = (Map<String, Object>) send.get("request");
        config.send().request().required((boolean) request.get("required"));
        config.send().request().prompt((String) request.get("prompt"));
        config.send().delay((int) send.get("delay"));

        // read the feedback section
        Map<String, ?> feedback = data.containsKey("feedback")
                ? (Map<String, ?>) data.get("feedback")
                : Collections.emptyMap();
        for (Map.Entry<String, ?> entry : feedback.entrySet()) {
            String statusKey = entry.getKey();
            ResourcePackStatus status;

            switch (statusKey.toLowerCase(Locale.ROOT)) {
                case "success":
                    status = ResourcePackStatus.LOADED;
                    break;
                case "failed":
                    status = ResourcePackStatus.FAILED;
                    break;
                case "accepted":
                    status = ResourcePackStatus.ACCEPTED;
                    break;
                case "declined":
                    status = ResourcePackStatus.DECLINED;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown status to give feedback: '" + statusKey + "'");
            }

            config.feedback().put(status, ActionParser.parse(entry.getValue()));
        }

        // read the export section
        Map<String, Object> export = (Map<String, Object>) data.get("export");
        config.export().type((String) export.get("type"));
        Map<String, Object> localhost = (Map<String, Object>) export.get("localhost");
        config.export().localHost().enabled((boolean) localhost.get("enabled"));
        config.export().localHost().address((String) localhost.get("address"));
        config.export().localHost().publicUrlFormat((String) localhost.get("public-url-format"));
        config.export().localHost().port((int) localhost.get("port"));

        // read the "command" section (messages)
        Map<String, ?> command = (Map<String, ?>) data.get("command");
        toNodes("", command, config.messages());

        // read the what-is-my-ip-services section
        List<String> whatIsMyIpServices = (List<String>) data.get("--what-is-my-ip-services");
        config.whatIsMyIpServices().addAll(whatIsMyIpServices);

        return config;
    }

    @SuppressWarnings("unchecked")
    private static void toNodes(String parent, Map<String, ?> data, Map<String, String> into) {
        for (Map.Entry<String, ?> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                toNodes(parent + key + '.', (Map<String, ?>) value, into);
            } else {
                if (value instanceof List) {
                    value = String.join("\n", (List<String>) value);
                }
                into.put(parent + key, value.toString());
            }
        }
    }

}