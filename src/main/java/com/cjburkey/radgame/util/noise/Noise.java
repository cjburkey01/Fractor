package com.cjburkey.radgame.util.noise;

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

    public static float getRaw(OpenSimplexNoise noise, double x, double y) {
        return (float) noise.eval(x, y);
    }

    public static float getRaw(OpenSimplexNoise noise, double x, double y, double z) {
        return (float) noise.eval(x, y, z);
    }

    public static float getRaw(OpenSimplexNoise noise, double x, double y, double z, double w) {
        return (float) noise.eval(x, y, z, w);
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
