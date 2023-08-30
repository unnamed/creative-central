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
package team.unnamed.creative.central.event;

import java.util.function.Consumer;

/**
 * An event bus. Main interface for an event/listener
 * or pub/sub local system.
 *
 * @since 1.0.0
 */
public interface EventBus {

    /**
     * Registers a new {@link EventListener} for the given
     * {@code eventType}, from the given {@code plugin}
     *
     * @param plugin The registerer plugin
     * @param eventType The subscribed event type
     * @param listener The actual event listener
     * @param <E> The event type
     * @since 1.0.0
     */
    <E extends Event> void listen(Object plugin, Class<E> eventType, EventListener<E> listener);

    /**
     * Registers a new {@link EventListener} using the given
     * {@code listener} and {@code priority}, from the given
     * {@code plugin} and for the given event type
     *
     * @param plugin The registerer plugin
     * @param eventType The subscribed event type
     * @param priority The event listener priority
     * @param listener The actual event listener
     * @param <E> The event type
     * @since 1.0.0
     */
    default <E extends Event> void listen(Object plugin, Class<E> eventType, EventListener.Priority priority, Consumer<E> listener) {
        listen(plugin, eventType, new EventListener<E>() {

            @Override
            public void on(E event) {
                listener.accept(event);
            }

            @Override
            public Priority priority() {
                return priority;
            }

            @Override
            public String toString() {
                return "EventListener<" + eventType + "> { " +
                        "priority = '" + priority + "', " +
                        "listener = '" + listener + "' " +
                        "}";
            }

        });
    }

    /**
     * Calls the given {@code event}. This will execute
     * all the listener subscribed to the provided event
     * type.
     *
     * @param eventType The event type
     * @param event The event data
     * @param <E> The event type
     * @since 1.0.0
     */
    <E extends Event> void call(Class<E> eventType, E event);

}
