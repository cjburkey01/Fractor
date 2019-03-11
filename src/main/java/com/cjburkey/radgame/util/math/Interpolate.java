package com.cjburkey.radgame.util.math;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * Created by CJ Burkey on 2019/03/10
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class Interpolate {

    public static float linear(final float start,
                               final float end,
                               final float progress) {
        return ((progress * (end - start)) + start);
    }

    public static Vector2f linear(final Vector2fc start,
                                  final Vector2fc end,
                                  final float progress) {
        return new Vector2f(linear(start.x(), end.x(), progress),
                linear(start.y(), end.y(), progress));
    }

    public static Vector3f linear(final Vector3fc start,
                                  final Vector3fc end,
                                  final float progress) {
        return new Vector3f(linear(start.x(), end.x(), progress),
                linear(start.y(), end.y(), progress),
                linear(start.z(), end.z(), progress));
    }

    public static Vector4f linear(final Vector4fc start,
                                  final Vector4fc end,
                                  final float progress) {
        return new Vector4f(linear(start.x(), end.x(), progress),
                linear(start.y(), end.y(), progress),
                linear(start.z(), end.z(), progress),
                linear(start.w(), end.w(), progress));
    }

    public static float map(final float val,
                            final float inputMin,
                            final float inputMax,
                            final float outputMin,
                            final float outputMax) {
        return ((((val - inputMin) / (inputMax - inputMin)) * (outputMax - outputMin)) + outputMin);
    }

    public static double map(final double val,
                             final double inputMin,
                             final double inputMax,
                             final double outputMin,
                             final double outputMax) {
        return ((((val - inputMin) / (inputMax - inputMin)) * (outputMax - outputMin)) + outputMin);
    }

    public static double clamp(final double val, final double min, final double max) {
        return Double.max(Double.min(val, max), min);
    }

    public static float clamp(final float val, final float min, final float max) {
        return Float.max(Float.min(val, max), min);
    }

}
