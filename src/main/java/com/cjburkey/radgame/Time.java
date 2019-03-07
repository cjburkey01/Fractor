package com.cjburkey.radgame;

/**
 * Created by CJ Burkey on 2019/03/03
 */
@SuppressWarnings("WeakerAccess")
public final class Time {

    private static final double NANO_TO_UNIT = 1.0E-9d;

    private static double renderDelta = 0.0d;

    public static double getTime() {
        return System.nanoTime() * NANO_TO_UNIT;
    }

    static void setRenderDelta(double delta) {
        renderDelta = delta;
    }

    public static double updateDelta() {
        return (1.0d / 60.0d);
    }

    public static float updateDeltaf() {
        return (1.0f / 60.0f);
    }

    public static double renderDelta() {
        return renderDelta;
    }

    public static float renderDeltaf() {
        return (float) renderDelta;
    }

}
