package com.cjburkey.radgame.util.math;

import org.joml.AABBf;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

/**
 * Created by CJ Burkey on 2019/03/17
 */
public final class Bounding {

    public static AABBf from(final Vector2fc min, final Vector2fc max) {
        return new AABBf(min.x(), min.y(), 0.0f, max.x(), max.y(), 0.0f);
    }

    public static AABBf from(final Vector2ic min, final Vector2ic max) {
        return new AABBf(min.x(), min.y(), 0.0f, max.x(), max.y(), 0.0f);
    }

    public static AABBf fromSize(final Vector2fc min, final Vector2fc size) {
        return new AABBf(min.x(), min.y(), 0.0f, min.x() + size.x(), min.y() + size.y(), 0.0f);
    }

    public static AABBf fromSize(final Vector2ic min, final Vector2ic size) {
        return new AABBf(min.x(), min.y(), 0.0f, min.x() + size.x(), min.y() + size.y(), 0.0f);
    }

}
