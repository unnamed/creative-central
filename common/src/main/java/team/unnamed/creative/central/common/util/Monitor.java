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
package team.unnamed.creative.central.common.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

public final class Monitor<T> {

    private T value;
    private final Set<BiConsumer<T, T>> listeners = Collections.synchronizedSet(new HashSet<>());

    private Monitor(T initialValue) {
        this.value = requireNonNull(initialValue, "initialValue");
    }

    public synchronized T get() {
        return value;
    }

    public synchronized void set(T value) {
        T oldValue = this.value;
        this.value = value;
        listeners.forEach(listener -> listener.accept(oldValue, value));
    }

    public synchronized void onChange(BiConsumer<T, T> listener) {
        requireNonNull(listener, "listener");
        listeners.add(listener);
    }

    public static <T> Monitor<T> monitor(T initialValue) {
        return new Monitor<>(initialValue);
    }

}