package com.cjburkey.radgame.util.noise;

import com.cjburkey.radgame.util.math.Interpolate;
import org.joml.Vector2dc;
import org.joml.Vector2fc;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector4dc;
import org.joml.Vector4fc;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class Noise {

    private static final double RANGE = Math.sqrt(3.0d) / 2.0d;

    // Maps to -1.0, 1.0
    public static float getRaw(OpenSimplexNoise noise, double x, double y) {
        return (float) Interpolate.map(noise.eval(x, y), -RANGE, RANGE, -1.0d, 1.0d);
    }

    // Maps to -1.0, 1.0
    public static float getRaw(OpenSimplexNoise noise, double x, double y, double z) {
        return (float) Interpolate.map(noise.eval(x, y, y), -RANGE, RANGE, -1.0d, 1.0d);
    }

    // Maps to -1.0, 1.0
    public static float getRaw(OpenSimplexNoise noise, double x, double y, double z, double w) {
        return (float) Interpolate.map(noise.eval(x, y, z, w), -RANGE, RANGE, -1.0d, 1.0d);
    }

    public static float getRaw(OpenSimplexNoise noise, Vector2dc pos) {
        return getRaw(noise, pos.x(), pos.y());
    }

    public static float getRaw(OpenSimplexNoise noise, Vector3dc pos) {
        return getRaw(noise, pos.x(), pos.y(), pos.z());
    }

    public static float getRaw(OpenSimplexNoise noise, Vector4dc pos) {
        return getRaw(noise, pos.x(), pos.y(), pos.z(), pos.w());
    }

    public static float getRaw(OpenSimplexNoise noise, Vector2fc pos) {
        return getRaw(noise, pos.x(), pos.y());
    }

    public static float getRaw(OpenSimplexNoise noise, Vector3fc pos) {
        return getRaw(noise, pos.x(), pos.y(), pos.z());
    }

    public static float getRaw(OpenSimplexNoise noise, Vector4fc pos) {
        return getRaw(noise, pos.x(), pos.y(), pos.z(), pos.w());
    }

    public static float get(OpenSimplexNoise noise, double amplitude, double noiseScale, double x, double y) {
        return (float) (getRaw(noise, x / noiseScale, y / noiseScale) * amplitude);
    }

    public static float get(OpenSimplexNoise noise, double amplitude, double noiseScale, double x, double y, double z) {
        return (float) (getRaw(noise, x / noiseScale, y / noiseScale, z / noiseScale) * amplitude);
    }

    public static float get(OpenSimplexNoise noise, double amplitude, double noiseScale, double x, double y, double z, double w) {
        return (float) (getRaw(noise, x / noiseScale, y / noiseScale, z / noiseScale, w / noiseScale) * amplitude);
    }

    public static float get(OpenSimplexNoise noise, double amplitude, double noiseScale, Vector2dc pos) {
        return get(noise, amplitude, noiseScale, pos.x(), pos.y());
    }

    public static float get(OpenSimplexNoise noise, double amplitude, double noiseScale, Vector3dc pos) {
        return get(noise, amplitude, noiseScale, pos.x(), pos.y(), pos.z());
    }

    public static float get(OpenSimplexNoise noise, double amplitude, double noiseScale, Vector4dc pos) {
        return get(noise, amplitude, noiseScale, pos.x(), pos.y(), pos.z(), pos.w());
    }

    public static float get(OpenSimplexNoise noise, double amplitude, double noiseScale, Vector2fc pos) {
        return get(noise, amplitude, noiseScale, pos.x(), pos.y());
    }

    public static float get(OpenSimplexNoise noise, double amplitude, double noiseScale, Vector3fc pos) {
        return get(noise, amplitude, noiseScale, pos.x(), pos.y(), pos.z());
    }

    public static float get(OpenSimplexNoise noise, double amplitude, double noiseScale, Vector4fc pos) {
        return get(noise, amplitude, noiseScale, pos.x(), pos.y(), pos.z(), pos.w());
    }

}
