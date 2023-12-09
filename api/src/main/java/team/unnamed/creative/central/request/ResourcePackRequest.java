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
package team.unnamed.creative.central.request;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Represents a resource-pack download request, sent to the
 * Minecraft players by the server
 *
 * @since 1.0.0
 */
public final class ResourcePackRequest {
    private final UUID uuid;
    private final URI uri;
    private final String hash;
    private final boolean required;
    private final @Nullable Component prompt;

    private ResourcePackRequest(
            final @NotNull UUID uuid,
            final @NotNull URI uri,
            final @NotNull String hash,
            final boolean required,
            final @Nullable Component prompt
    ) {
        this.uuid = requireNonNull(uuid, "uuid");
        this.uri = requireNonNull(uri, "uri");
        this.hash = requireNonNull(hash, "hash");
        this.required = required;
        this.prompt = prompt;
    }

    /**
     * Returns this resource-pack UUID.
     *
     * @return The resource-pack UUID
     * @since 1.0.0
     * @sinceMinecraft 1.20.3
     */
    public @NotNull UUID uuid() {
        return uuid;
    }

    /**
     * Returns the URL for the resource-pack being
     * sent
     *
     * @return The resource-pack URL
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.0.0")
    public String url() {
        return uri.toString();
    }

    public @NotNull URI uri() {
        return uri;
    }

    /**
     * Returns the SHA-1 hash of the resource-pack,
     * may be empty
     *
     * @return The SHA-1 hash of the resource-pack
     */
    public String hash() {
        return hash;
    }

    /**
     * Returns true if the use of the resource-pack
     * is required on the server, false otherwise
     *
     * @return If the resource-pack is required
     */
    public boolean required() {
        return required;
    }

    /**
     * Returns the prompt message, shown to the end-user
     * in the confirmation screen when they are asked to
     * download the resource-pack
     *
     * @return The prompt message
     */
    public @Nullable Component prompt() {
        return prompt;
    }

    /**
     * Creates a {@link ResourcePackRequest} with the given properties.
     *
     * @param uuid The UUID of the resource-pack
     * @param uri The URI of the resource-pack
     * @param hash The SHA-1 hash of the resource-pack
     * @param required If the resource-pack is required
     * @param prompt The prompt message
     * @return The created {@link ResourcePackRequest}
     */
    public static @NotNull ResourcePackRequest of(final @NotNull UUID uuid, final @NotNull URI uri, final @NotNull String hash, final boolean required, final @Nullable Component prompt) {
        return new ResourcePackRequest(uuid, uri, hash, required, prompt);
    }

    /**
     * Creates a {@link ResourcePackRequest} with the given properties,
     * using the {@link URI} as the {@link UUID} for the resource-pack.
     *
     * @param uri The URI of the resource-pack
     * @param hash The SHA-1 hash of the resource-pack
     * @param required If the resource-pack is required
     * @param prompt The prompt message
     * @return The created {@link ResourcePackRequest}
     * @deprecated Use {@link #of(UUID, URI, String, boolean, Component)} instead
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.0.0")
    public static @NotNull ResourcePackRequest of(final @NotNull URI uri, final @NotNull String hash, final boolean required, final @Nullable Component prompt) {
        return new ResourcePackRequest(UUID.nameUUIDFromBytes(uri.toString().getBytes(StandardCharsets.UTF_8)), uri, hash, required, prompt);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.0.0")
    public static ResourcePackRequest of(
            String url,
            String hash,
            boolean required,
            @Nullable Component prompt
    ) {
        return of(URI.create(url), hash, required, prompt);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.0.0")
    public static ResourcePackRequest of(String url, String hash, boolean required) {
        return of(url, hash, required, null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private URI uri;
        private String hash;
        private boolean required;
        private @Nullable Component prompt;

        private Builder() {
        }

        /**
         * Sets the UUID of the resource-pack.
         *
         * @param uuid The UUID of the resource-pack
         * @return This builder
         * @since 1.0.0
         * @sinceMinecraft 1.20.3
         */
        @Contract("_ -> this")
        public @NotNull Builder uuid(final @NotNull UUID uuid) {
            this.uuid = requireNonNull(uuid, "uuid");
            return this;
        }

        @Deprecated
        @ApiStatus.ScheduledForRemoval(inVersion = "2.0.0")
        public Builder url(String url) {
            this.uri = URI.create(url);
            return this;
        }

        public @NotNull Builder uri(final @NotNull URI uri) {
            this.uri = requireNonNull(uri, "uri");
            return this;
        }

        public Builder hash(String hash) {
            this.hash = hash;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder prompt(@Nullable Component prompt) {
            this.prompt = prompt;
            return this;
        }

        public ResourcePackRequest build() {
            if (uuid == null) {
                requireNonNull(uri, "uri");
                // backwards compatibility
                uuid = UUID.nameUUIDFromBytes(uri.toString().getBytes(StandardCharsets.UTF_8));
            }
            return new ResourcePackRequest(uuid, uri, hash, required, prompt);
        }


    }

}
