package com.cjburkey.radgame.util;

import com.cjburkey.radgame.util.math.Interpolate;
import org.joml.Random;

/**
 * Created by CJ Burkey on 2019/03/16
 */
public final class Rand {

    public final int inclusive(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public final float exclusive(Random random, float min, float max) {
        return Interpolate.map(random.nextFloat(), 0.0f, 1.0f, min, max);
    }

}
