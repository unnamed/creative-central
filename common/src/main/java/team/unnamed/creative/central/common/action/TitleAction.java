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

import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import team.unnamed.creative.central.common.util.Components;

import java.util.Map;

public class TitleAction implements Action {

    public static final String IDENTIFIER = "title";

    private final Title title;

    public TitleAction(Title title) {
        this.title = title;
    }

    public Title title() {
        return title;
    }

    public static Action deserialize(Object src) {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) src;

        return new TitleAction(Title.title(
                Components.deserialize(data.getOrDefault("title", "").toString()),
                Components.deserialize(data.getOrDefault("subtitle", "").toString()),
                Title.Times.times(
                        Ticks.duration(Integer.parseInt(data.getOrDefault("fade-in", 10).toString())),
                        Ticks.duration(Integer.parseInt(data.getOrDefault("stay", 70).toString())),
                        Ticks.duration(Integer.parseInt(data.getOrDefault("fade-out", 20).toString()))
                )
        ));
    }

}