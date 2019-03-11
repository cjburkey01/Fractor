package com.cjburkey.radgame.util.noise;

import java.util.Objects;
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

    public double amplitude = 1.0d;
    public double noiseScale = 1.0d;
    public double offsetX = 0.0d;
    public double offsetY = 0.0d;
    public double offsetZ = 0.0d;
    public double offsetW = 0.0d;

    public NoiseState() {
    }

    public NoiseState(double amplitude, double noiseScale) {
        this.amplitude = amplitude;
        this.noiseScale = noiseScale;
    }

    public NoiseState(double amplitude, double noiseScale, double offsetX, double offsetY) {
        this(amplitude, noiseScale);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public NoiseState(double amplitude, double noiseScale, double offsetX, double offsetY, double offsetZ) {
        this(amplitude, noiseScale, offsetX, offsetY);
        this.offsetZ = offsetZ;
    }

    public NoiseState(double amplitude, double noiseScale, double offsetX, double offsetY, double offsetZ, double offsetW) {
        this(amplitude, noiseScale, offsetX, offsetY, offsetZ);
        this.offsetW = offsetW;
    }

    public NoiseState(double amplitude, double noiseScale, Vector2dc offset) {
        this(amplitude, noiseScale, offset.x(), offset.y());
    }

    public NoiseState(double amplitude, double noiseScale, Vector3dc offset) {
        this(amplitude, noiseScale, offset.x(), offset.y(), offset.z());
    }

    public NoiseState(double amplitude, double noiseScale, Vector4dc offset) {
        this(amplitude, noiseScale, offset.x(), offset.y(), offset.z(), offset.w());
    }

    public NoiseState(double amplitude, double noiseScale, Vector2fc offset) {
        this(amplitude, noiseScale, offset.x(), offset.y());
    }

    public NoiseState(double amplitude, double noiseScale, Vector3fc offset) {
        this(amplitude, noiseScale, offset.x(), offset.y(), offset.z());
    }

    public NoiseState(double amplitude, double noiseScale, Vector4fc offset) {
        this(amplitude, noiseScale, offset.x(), offset.y(), offset.z(), offset.w());
    }

    public float get(double x, double y) {
        return Noise.get(amplitude, noiseScale, x + offsetX, y + offsetY);
    }

    public float get(double x, double y, double z) {
        return Noise.get(amplitude, noiseScale, x + offsetX, y + offsetY, z + offsetZ);
    }

    public float get(double x, double y, double z, double w) {
        return Noise.get(amplitude, noiseScale, x + offsetX, y + offsetY, z + offsetZ, w + offsetW);
    }

    public float get(Vector2dc pos) {
        return Noise.get(amplitude, noiseScale, pos);
    }

    public float get(Vector3dc pos) {
        return Noise.get(amplitude, noiseScale, pos);
    }

    public float get(Vector4dc pos) {
        return Noise.get(amplitude, noiseScale, pos);
    }

    public float get(Vector2fc pos) {
        return Noise.get(amplitude, noiseScale, pos);
    }

    public float get(Vector3fc pos) {
        return Noise.get(amplitude, noiseScale, pos);
    }

    public float get(Vector4fc pos) {
        return Noise.get(amplitude, noiseScale, pos);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoiseState that = (NoiseState) o;
        return Double.compare(that.amplitude, amplitude) == 0 &&
                Double.compare(that.noiseScale, noiseScale) == 0;
    }

    public int hashCode() {
        return Objects.hash(amplitude, noiseScale);
    }

}
