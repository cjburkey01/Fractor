package com.cjburkey.radgame.util.noise;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.joml.Vector2dc;
import org.joml.Vector2fc;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector4dc;
import org.joml.Vector4fc;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class NoiseState {

    private static final Long2ObjectOpenHashMap<OpenSimplexNoise> noises = new Long2ObjectOpenHashMap<>();

    public double amplitude = 1.0d;
    public double noiseScale = 1.0d;
    public double offsetX = 0.0d;
    public double offsetY = 0.0d;
    public double offsetZ = 0.0d;
    public double offsetW = 0.0d;
    private final long seed;
    public final OpenSimplexNoise noise;

    public NoiseState(OpenSimplexNoise noise) {
        this.noise = noise;
        seed = Long.MIN_VALUE;
    }

    public NoiseState(OpenSimplexNoise noise, double amplitude, double noiseScale) {
        this(noise);
        this.amplitude = amplitude;
        this.noiseScale = noiseScale;
    }

    public NoiseState(OpenSimplexNoise noise, double amplitude, double noiseScale, double offsetX, double offsetY) {
        this(noise, amplitude, noiseScale);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public NoiseState(OpenSimplexNoise noise, double amplitude, double noiseScale, double offsetX, double offsetY, double offsetZ) {
        this(noise, amplitude, noiseScale, offsetX, offsetY);
        this.offsetZ = offsetZ;
    }

    public NoiseState(OpenSimplexNoise noise, double amplitude, double noiseScale, double offsetX, double offsetY, double offsetZ, double offsetW) {
        this(noise, amplitude, noiseScale, offsetX, offsetY, offsetZ);
        this.offsetW = offsetW;
    }

    public NoiseState(OpenSimplexNoise noise, double amplitude, double noiseScale, Vector2dc offset) {
        this(noise, amplitude, noiseScale, offset.x(), offset.y());
    }

    public NoiseState(OpenSimplexNoise noise, double amplitude, double noiseScale, Vector3dc offset) {
        this(noise, amplitude, noiseScale, offset.x(), offset.y(), offset.z());
    }

    public NoiseState(OpenSimplexNoise noise, double amplitude, double noiseScale, Vector4dc offset) {
        this(noise, amplitude, noiseScale, offset.x(), offset.y(), offset.z(), offset.w());
    }

    public NoiseState(OpenSimplexNoise noise, double amplitude, double noiseScale, Vector2fc offset) {
        this(noise, amplitude, noiseScale, offset.x(), offset.y());
    }

    public NoiseState(OpenSimplexNoise noise, double amplitude, double noiseScale, Vector3fc offset) {
        this(noise, amplitude, noiseScale, offset.x(), offset.y(), offset.z());
    }

    public NoiseState(OpenSimplexNoise noise, double amplitude, double noiseScale, Vector4fc offset) {
        this(noise, amplitude, noiseScale, offset.x(), offset.y(), offset.z(), offset.w());
    }

    public NoiseState(long seed) {
        if (noises.containsKey(seed)) {
            this.noise = noises.get(seed);
        } else {
            noises.put(seed, this.noise = new OpenSimplexNoise(seed));
        }
        this.seed = seed;
    }

    public NoiseState(long seed, double amplitude, double noiseScale) {
        this(seed);
        this.amplitude = amplitude;
        this.noiseScale = noiseScale;
    }

    public NoiseState(long seed, double amplitude, double noiseScale, double offsetX, double offsetY) {
        this(seed, amplitude, noiseScale);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public NoiseState(long seed, double amplitude, double noiseScale, double offsetX, double offsetY, double offsetZ) {
        this(seed, amplitude, noiseScale, offsetX, offsetY);
        this.offsetZ = offsetZ;
    }

    public NoiseState(long seed, double amplitude, double noiseScale, double offsetX, double offsetY, double offsetZ, double offsetW) {
        this(seed, amplitude, noiseScale, offsetX, offsetY, offsetZ);
        this.offsetW = offsetW;
    }

    public NoiseState(long seed, double amplitude, double noiseScale, Vector2dc offset) {
        this(seed, amplitude, noiseScale, offset.x(), offset.y());
    }

    public NoiseState(long seed, double amplitude, double noiseScale, Vector3dc offset) {
        this(seed, amplitude, noiseScale, offset.x(), offset.y(), offset.z());
    }

    public NoiseState(long seed, double amplitude, double noiseScale, Vector4dc offset) {
        this(seed, amplitude, noiseScale, offset.x(), offset.y(), offset.z(), offset.w());
    }

    public NoiseState(long seed, double amplitude, double noiseScale, Vector2fc offset) {
        this(seed, amplitude, noiseScale, offset.x(), offset.y());
    }

    public NoiseState(long seed, double amplitude, double noiseScale, Vector3fc offset) {
        this(seed, amplitude, noiseScale, offset.x(), offset.y(), offset.z());
    }

    public NoiseState(long seed, double amplitude, double noiseScale, Vector4fc offset) {
        this(seed, amplitude, noiseScale, offset.x(), offset.y(), offset.z(), offset.w());
    }

    public float get(double x, double y) {
        return Noise.get(noise, amplitude, noiseScale, x + offsetX, y + offsetY);
    }

    public void destroy() {
        if (seed != Long.MIN_VALUE) noises.remove(seed);
    }

    public float get(double x, double y, double z) {
        return Noise.get(noise, amplitude, noiseScale, x + offsetX, y + offsetY, z + offsetZ);
    }

    public float get(double x, double y, double z, double w) {
        return Noise.get(noise, amplitude, noiseScale, x + offsetX, y + offsetY, z + offsetZ, w + offsetW);
    }

    public float get(Vector2dc pos) {
        return Noise.get(noise, amplitude, noiseScale, pos);
    }

    public float get(Vector3dc pos) {
        return Noise.get(noise, amplitude, noiseScale, pos);
    }

    public float get(Vector4dc pos) {
        return Noise.get(noise, amplitude, noiseScale, pos);
    }

    public float get(Vector2fc pos) {
        return Noise.get(noise, amplitude, noiseScale, pos);
    }

    public float get(Vector3fc pos) {
        return Noise.get(noise, amplitude, noiseScale, pos);
    }

    public float get(Vector4fc pos) {
        return Noise.get(noise, amplitude, noiseScale, pos);
    }

}
