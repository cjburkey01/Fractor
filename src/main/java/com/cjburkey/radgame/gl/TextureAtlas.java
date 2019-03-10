package com.cjburkey.radgame.gl;

import org.joml.Rectanglef;
import org.joml.Vector2ic;

/**
 * Created by CJ Burkey on 2019/03/09
 */
public class TextureAtlas {

    private final Texture texture;
    private final int width;

    // Width = number of subimages
    public TextureAtlas(final Texture texture, final int width) {
        if (width <= 0)
            throw new IllegalArgumentException(String.format("Invalid texture atlas size: %sx%s", width, width));
        if (texture.width != texture.height)
            throw new IllegalArgumentException(String.format("Invalid texture size: %sx%s", texture.width, texture.height));

        this.texture = texture;
        this.width = width;
    }

    public Texture getTexture() {
        return texture;
    }

    @SuppressWarnings("WeakerAccess")
    public Rectanglef getUv(final int x, final int y) {
        if (x < 0 || y < 0 || x >= width || y >= width) return new Rectanglef();

        final var w = (1.0f / width);
        final var atX = w * x;
        final var atY = w * y;
        return new Rectanglef(atX, atY, atX + w, atY + w);
    }

    public Rectanglef getUv(final Vector2ic atlasPos) {
        return getUv(atlasPos.x(), atlasPos.y());
    }

}
