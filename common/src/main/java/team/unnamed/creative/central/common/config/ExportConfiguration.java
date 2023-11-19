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

public class ExportConfiguration {

    private String type = "mcpacks";
    private LocalHostExportConfiguration localHost = new LocalHostExportConfiguration();

    public String type() {
        return type;
    }

    public void type(String type) {
        this.type = type;
    }

    public LocalHostExportConfiguration localHost() {
        return localHost;
    }

    public static class LocalHostExportConfiguration {

        private boolean enabled = false;
        private String publicAddress = "";
        private String address = "";
        private int port = 7270;

        public boolean enabled() {
            return enabled;
        }

        public void enabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String publicAddress() {
            return publicAddress;
        }

        public void publicAddress(String publicAddress) {
            this.publicAddress = publicAddress;
        }

        public String address() {
            return address;
        }

        public void address(String address) {
            this.address = address;
        }

        public int port() {
            return port;
        }

        public void port(int port) {
            this.port = port;
        }

    }

}