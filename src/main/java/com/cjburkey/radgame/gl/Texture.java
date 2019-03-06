package com.cjburkey.radgame.gl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by CJ Burkey on 2019/03/05
 */
@SuppressWarnings("WeakerAccess")
public class Texture implements AutoCloseable {

    private static HashMap<Integer, Integer> currentTextures = new HashMap<>();

    private final int bindLocation;
    private final int texture;
    private boolean hasMipmaps;
    public final int width;
    public final int height;
    public final int channels;
    private final int minFilter;
    private final int magFilter;

    private Texture(final int bindLocation,
                    final int texture,
                    final int width,
                    final int height,
                    final int channels,
                    final int minFilter,
                    final int magFilter) {
        this.bindLocation = bindLocation;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.minFilter = minFilter;
        this.magFilter = magFilter;
    }

    public void bind() {
        if (!isBound()) {
            glBindTexture(bindLocation, texture);
            glTexParameteri(bindLocation, GL_TEXTURE_MIN_FILTER, minFilter);
            glTexParameteri(bindLocation, GL_TEXTURE_MAG_FILTER, magFilter);
            currentTextures.put(bindLocation, texture);
        }
    }

    public void close() {
        if (isBound()) {
            currentTextures.remove(bindLocation);
            glBindTexture(bindLocation, 0);
        }
        glDeleteTextures(texture);
    }

    private boolean isBound() {
        return currentTextures.getOrDefault(bindLocation, -1).equals(texture);
    }

    private void genMipmaps() {
        if (!hasMipmaps) {
            bind();
            glGenerateMipmap(bindLocation);
            hasMipmaps = true;
        }
    }

    // DOES NOT FREE rawImage INPUT BUFFER!
    public static Texture readRawTexture(final int bindLocation,
                                         final int width,
                                         final int height,
                                         final int channels,
                                         final int imageType,
                                         final ByteBuffer rawImage,
                                         final int minFilter,
                                         final int magFilter,
                                         final boolean mipmap) {
        var texture = new Texture(bindLocation, glGenTextures(), width, height, channels, minFilter, magFilter);
        texture.bind();
        glTexImage2D(bindLocation, 0, imageType, width, height, 0, imageType, GL_UNSIGNED_BYTE, rawImage);
        if (mipmap) texture.genMipmaps();
        return texture;
    }

    public static Texture readStream(final int bindLocation,
                                     final InputStream stream,
                                     final int minFilter,
                                     final int magFilter,
                                     final boolean mipmap) throws IOException {
        final var rawFileBytes = stream.readAllBytes();
        final var rawFileBuffer = memAlloc(rawFileBytes.length).put(rawFileBytes).flip();
        ByteBuffer rawImgBuffer = null;
        try (MemoryStack stack = stackPush()) {
            final var width = stack.mallocInt(1);
            final var height = stack.mallocInt(1);
            final var channels = stack.mallocInt(1);
            rawImgBuffer = stbi_load_from_memory(rawFileBuffer, width, height, channels, 0);
            memFree(rawFileBuffer);
            if (rawImgBuffer == null) {
                throw new IOException(stbi_failure_reason());
            }
            var imageType = 0;

            final var channelCount = channels.get(0);
            if (channelCount == 1) imageType = GL_RED;
            else if (channelCount == 2) imageType = GL_RG;
            else if (channelCount == 3) imageType = GL_RGB;
            else if (channelCount == 4) imageType = GL_RGBA;
            else throw new IllegalStateException("Invalid image channel count: " + channelCount);

            return readRawTexture(bindLocation,
                    width.get(0),
                    height.get(0),
                    channelCount,
                    imageType,
                    rawImgBuffer,
                    minFilter,
                    magFilter,
                    mipmap);
        } finally {
            if (rawImgBuffer != null) stbi_image_free(rawImgBuffer);
        }
    }

    public static Texture readStream(final int bindLocation,
                                     final InputStream stream,
                                     final int minFilter,
                                     final int magFilter) throws IOException {
        return readStream(bindLocation, stream, minFilter, magFilter, true);
    }

    public static Texture readStream(final InputStream stream,
                                     final int minFilter,
                                     final int magFilter) throws IOException {
        return readStream(GL_TEXTURE_2D, stream, minFilter, magFilter);
    }

    public static Texture readStream(final InputStream stream) throws IOException {
        return readStream(stream, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST);
    }

}
