package com.cjburkey.radgame.texture;

import com.cjburkey.radgame.RadGame;
import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.game.GameManager;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import org.lwjgl.system.MemoryStack;

import static org.joml.Math.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageResize.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by CJ Burkey on 2019/03/05
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Texture implements Closeable {

    private static final Int2IntOpenHashMap currentTextures = new Int2IntOpenHashMap();

    static {
        currentTextures.defaultReturnValue(-1);
    }

    final int bindLocation;
    private final int texture;

    final int width;
    final int height;

    private int minFilter = GL_NEAREST_MIPMAP_NEAREST;
    private int magFilter = GL_NEAREST;
    private int textureWrapS = GL_REPEAT;
    private int textureWrapT = GL_REPEAT;

    Texture(final int bindLocation,
            final int texture,
            final int width,
            final int height) {
        this.bindLocation = bindLocation;
        this.texture = texture;
        this.width = width;
        this.height = height;

        GameManager.EVENT_BUS.addListener(RadGame.EventCleanup.class, ignored -> this.close());
    }

    public void bind() {
        if (!isBound()) {
            glBindTexture(bindLocation, texture);
            currentTextures.put(bindLocation, texture);

            glPixelStorei(GL_PACK_ALIGNMENT, 1);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexParameteri(bindLocation, GL_TEXTURE_MIN_FILTER, minFilter);
            glTexParameteri(bindLocation, GL_TEXTURE_MAG_FILTER, magFilter);
            glTexParameteri(bindLocation, GL_TEXTURE_WRAP_S, textureWrapS);
            glTexParameteri(bindLocation, GL_TEXTURE_WRAP_T, textureWrapT);
        }
    }

    @Override
    public void close() {
        if (isBound()) {
            currentTextures.remove(bindLocation);
            glBindTexture(bindLocation, 0);
        }
        glDeleteTextures(texture);
    }

    private boolean isBound() {
        return (currentTextures.get(bindLocation) == texture);
    }

    private void genMipmaps() {
        bind();
        glGenerateMipmap(bindLocation);
    }

    public Texture setMinFilter(int minFilter) {
        this.minFilter = minFilter;
        return this;
    }

    public Texture setMagFilter(int magFilter) {
        this.magFilter = magFilter;
        return this;
    }

    public Texture setWrapS(int textureWrapS) {
        this.textureWrapS = textureWrapS;
        return this;
    }

    public Texture setWrapT(int textureWrapT) {
        this.textureWrapT = textureWrapT;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Texture texture1 = (Texture) o;
        return bindLocation == texture1.bindLocation &&
                texture == texture1.texture;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bindLocation, texture);
    }

    // DOES NOT FREE input IMAGE BUFFER! IT MUST BE DONE MANUALLY
    public void subTexture(final ByteBuffer input,
                           final int mipmap,
                           final int x,
                           final int y,
                           final int w,
                           final int h) {
        var image = input;
        final var newW = (w >> mipmap);
        final var newH = (h >> mipmap);
        if (mipmap > 0) image = resizeRaw(image, w, h, newW, newH, 4);
        final var newX = (x >> mipmap);
        final var newY = (y >> mipmap);

        bind();
        glTexSubImage2D(bindLocation, mipmap, newX, newY, newW, newH, GL_RGBA, GL_UNSIGNED_BYTE, image);

        if (image != input) memFree(image);
    }

    public static ByteBuffer readStreamToRaw(final InputStream stream,
                                             final IntBuffer width,
                                             final IntBuffer height,
                                             final IntBuffer channels,
                                             final int desiredChannels) throws IOException {
        final var rawFileBytes = stream.readAllBytes();
        final var rawFileBuffer = memAlloc(rawFileBytes.length).put(rawFileBytes).flip();
        try {
            ByteBuffer output = stbi_load_from_memory(rawFileBuffer, width, height, channels, desiredChannels);
            if (output == null) throw new IOException("Failed to read image from memory: " + getStbiError());
            return output;
        } finally {
            memFree(rawFileBuffer);
        }
    }

    // DOES NOT FREE input IMAGE BUFFER! IT MUST BE DONE MANUALLY
    public static ByteBuffer resizeRaw(
            final ByteBuffer input,
            final int inputWidth,
            final int inputHeight,
            final int outputWidth,
            final int outputHeight,
            final int channels) {
        final var output = memAlloc(outputWidth * outputHeight * 4);
        if (!stbir_resize_uint8(input,
                inputWidth,
                inputHeight,
                0,
                output,
                outputWidth,
                outputHeight,
                0,
                channels)) {
            throw new IllegalStateException("Failed to resize image: " + getStbiError());
        }
        return output;
    }

    // DOES NOT FREE rawImage INPUT BUFFER! IT MUST BE DONE MANUALLY
    public static Texture readRawTexture(final int bindLocation,
                                         final int width,
                                         final int height,
                                         final int imageType,
                                         final ByteBuffer rawImage,
                                         final boolean mipmap) {
        var texture = new Texture(bindLocation, glGenTextures(), width, height);
        texture.bind();
        glTexImage2D(bindLocation, 0, imageType, width, height, 0, imageType, GL_UNSIGNED_BYTE, rawImage);
        if (mipmap) texture.genMipmaps();
        return texture;
    }

    public static Texture readStream(final int bindLocation,
                                     final InputStream stream,
                                     final boolean mipmap) throws IOException {
        ByteBuffer rawImgBuffer = null;
        try (MemoryStack stack = stackPush()) {
            final var width = stack.mallocInt(1);
            final var height = stack.mallocInt(1);
            rawImgBuffer = readStreamToRaw(stream, width, height, stack.mallocInt(1), 4);
            if (rawImgBuffer == null) {
                throw new IOException(Objects.requireNonNull(stbi_failure_reason()).trim());
            }

            return readRawTexture(bindLocation,
                    width.get(0),
                    height.get(0),
                    GL_RGBA,
                    rawImgBuffer,
                    mipmap);
        } finally {
            if (rawImgBuffer != null) stbi_image_free(rawImgBuffer);
        }
    }

    public static Texture readStream(final int bindLocation,
                                     final InputStream stream) throws IOException {
        return readStream(bindLocation, stream, true);
    }

    public static Texture readStream(final InputStream stream) throws IOException {
        return readStream(GL_TEXTURE_2D, stream);
    }

    public static Texture read(final ResourceLocation location) throws IOException {
        return readStream(location.getStream());
    }

    static int getMipmapCount(final int textureSize) {
        return 1 + (int) floor(Math.log10(textureSize) / Math.log10(2.0d));
    }

    private static String getStbiError() {
        final var str = stbi_failure_reason();
        if (str == null) return "";
        return str.trim();
    }

}
