package com.cjburkey.radgame.gl;

import com.cjburkey.radgame.ResourceLocation;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.joml.Rectanglef;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Created by CJ Burkey on 2019/03/09
 */
public class TextureAtlas {

    private final Texture texture;
    private final Object2ObjectOpenHashMap<ResourceLocation, Rectanglef> mapping;
    public final int width;

    // Width = number of subimages
    private TextureAtlas(final Texture texture, final int width, final Object2ObjectOpenHashMap<ResourceLocation, Rectanglef> mapping) {
        if (texture.width != texture.height)
            throw new IllegalArgumentException(String.format("Invalid texture size: %sx%s", texture.width, texture.height));

        this.texture = texture;
        this.mapping = mapping;
        this.width = width;
    }

    public Texture getTexture() {
        return texture;
    }

    public Rectanglef getUv(final ResourceLocation textureName) {
        return mapping.getOrDefault(textureName, null);
    }

    private static int getTextureSize(int count) {
        if (count == 0) return 0;
        if (count == 1) return 1;

        var i = 0;
        while ((i * i) < count) i++;
        return i;
    }

    public static TextureAtlas create(int tileSize, ResourceLocation... textures) {
        if (textures.length == 0) return null;
        final var textureWidth = getTextureSize(textures.length);
        final var mipmapCount = Texture.getMipmapCount(textureWidth * tileSize);
        final var atlasTexture = new Texture(GL_TEXTURE_2D, glGenTextures(), tileSize, tileSize, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST, GL_RGBA);
        final var atlas = new Object2ObjectOpenHashMap<ResourceLocation, Rectanglef>();

        atlasTexture.bind();

        // Create empty texture atlas
        for (var lod = 0; lod < mipmapCount; lod++) {
            final var w = ((textureWidth * tileSize) >> lod);
            glTexImage2D(atlasTexture.bindLocation, lod, GL_RGBA, w, w, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0L);
        }

        var x = 0;
        var y = 0;
        final var uvW = 1.0f / textureWidth;
        for (ResourceLocation resourceLocation : textures) {
            try (MemoryStack stack = stackPush()) {
                final var w = stack.mallocInt(1);
                final var h = stack.mallocInt(1);
                final var textureAt = Texture.readStreamToRaw(resourceLocation.getStream(), w, h, stack.mallocInt(1), 4);

                final ByteBuffer textureAtResized;
                try {
                    textureAtResized = Texture.resizeRaw(textureAt, w.get(0), h.get(0), tileSize, tileSize, 4);
                } catch (Exception e) {
                    throw new IllegalStateException(String.format("%s at: %s", e.getMessage(), resourceLocation.getFullPath()));
                } finally {
                    stbi_image_free(textureAt);
                }

                // Manual mipmapping
                for (var j = 0; j < mipmapCount; j++) {
                    atlasTexture.subTexture(textureAtResized, j, x * tileSize, y * tileSize, tileSize, tileSize);
                }
                stbi_image_free(textureAtResized);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            final var uvX = x * uvW;
            final var uvY = y * uvW;
            atlas.put(resourceLocation, new Rectanglef(uvX, uvY, uvX + uvW, uvY + uvW));

            x++;
            if (x >= textureWidth) {
                x = 0;
                y++;
            }
        }

        return new TextureAtlas(atlasTexture, textureWidth, atlas);
    }

}
