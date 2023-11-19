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
package team.unnamed.creative.central.common.event;

import team.unnamed.creative.central.event.Event;

import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface EventExceptionHandler {

    <E extends Event> void handleException(Class<E> eventType, E event, RegisteredEventListener<E> listener, Exception exception);

    static EventExceptionHandler empty() {
        return new EventExceptionHandler() {
            @Override
            public <E extends Event> void handleException(
                    Class<E> eventType,
                    E event,
                    RegisteredEventListener<E> listener,
                    Exception exception
            ) {
                // no-op
            }
        };
    }

    static EventExceptionHandler logging(Logger logger) {
        return logging((header, exception) -> logger.log(Level.WARNING, header, exception));
    }

    static EventExceptionHandler logging(BiConsumer<String, Exception> logger) {
        return new EventExceptionHandler() {
            @Override
            public <E extends Event> void handleException(
                    Class<E> eventType,
                    E event,
                    RegisteredEventListener<E> listener,
                    Exception exception
            ) {
                Object plugin = listener.plugin();
                logger.accept(
                        "Unhandled exception caught when calling event\n"
                                + "    Event: " + eventType.getName() + "\n"
                                + "    For listener: " + listener.listener() + "\n"
                                + "    Of plugin: " + (plugin != null ? plugin.toString() : "(No plugin specified)"),
                        exception
                );
            }
        };
    }

}