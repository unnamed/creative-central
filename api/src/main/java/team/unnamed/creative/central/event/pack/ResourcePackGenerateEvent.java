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
package team.unnamed.creative.central.event.pack;

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.central.event.Event;

import static java.util.Objects.requireNonNull;

/**
 * An event called when we want plugins/extensions to register
 * their own resources in our unified resource pack (for the
 * entire server)
 *
 * @since 1.0.0
 */
public final class ResourcePackGenerateEvent implements Event {
    private static final int ANY_FORMAT = -1;

    private final ResourcePack resourcePack;
    private final int format;

    /**
     * Instantiate a new {@link ResourcePackGenerateEvent}.
     *
     * @param format The expected format that the resource pack
     *               should be generated in, or {@code -1}
     *               if any format is accepted
     * @param resourcePack The resource-pack being generated
     * @since 1.0.0
     */
    public ResourcePackGenerateEvent(final int format, final @NotNull ResourcePack resourcePack) {
        this.format = format;
        this.resourcePack = requireNonNull(resourcePack, "resourcePack");
    }

    /**
     * Instantiate a new {@link ResourcePackGenerateEvent}.
     *
     * @param resourcePack The resource-pack being generated
     * @since 1.0.0
     */
    public ResourcePackGenerateEvent(final @NotNull ResourcePack resourcePack) {
        this(ANY_FORMAT, resourcePack);
    }

    /**
     * Returns the mutable resource pack instance
     * being generated and that can be edited when
     * listening to this event
     *
     * @return The resource pack
     * @since 1.0.0
     */
    public @NotNull ResourcePack resourcePack() {
        return resourcePack;
    }

    /**
     * Returns the expected format that the resource pack
     * should be generated in, or {@link #ANY_FORMAT} if
     * any format is accepted (or format has not been
     * specified)
     *
     * @return The expected format
     * @since 1.0.0
     */
    public int format() {
        return format;
    }

    /**
     * Returns whether the resource pack should be
     * generated in any format.
     *
     * @return Whether the resource pack should be
     * generated in any format
     * @since 1.0.0
     */
    public boolean isAnyFormat() {
        return format == ANY_FORMAT;
    }

    /**
     * Returns whether the resource pack should be
     * generated in the specified format.
     *
     * @param format The format to check
     * @return Whether the resource pack should be
     * generated in the specified format
     */
    public boolean isFormat(final int format) {
        return this.format == format || isAnyFormat();
    }
}
