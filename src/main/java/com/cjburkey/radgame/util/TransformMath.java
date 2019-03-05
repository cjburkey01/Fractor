package com.cjburkey.radgame.util;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

/**
 * Created by CJ Burkey on 2019/03/03
 */
public class TransformMath {

    private static Matrix4f projectionMatrix = new Matrix4f();
    private static Matrix4f viewMatrix = new Matrix4f();
    private static Matrix4f modelMatrix = new Matrix4f();

    public static Matrix4fc getProjectionMatrix(final float fovDegrees,
                                                final float aspectRatio,
                                                final float near,
                                                final float far) {
        final var fovRad = (float) Math.toRadians(fovDegrees);
        return projectionMatrix
                .identity()
                .perspective(fovRad, aspectRatio, near, far);
    }

    public static Matrix4fc getOrthographicMatrix(final float hSize,
                                                  final float aspectRatio) {
        final var hA = aspectRatio * hSize;
        return projectionMatrix
                .identity()
                .ortho2D(-hA, hA, -hSize, hSize);
    }

    public static Matrix4fc getViewMatrix(final Vector3fc cameraPosition,
                                          final Quaternionfc cameraRotation) {
        return viewMatrix
                .identity()
                .translate(-cameraPosition.x(), -cameraPosition.y(), -cameraPosition.z())
                .rotate(cameraRotation.invert(new Quaternionf()));
    }

    public static Matrix4fc getModelMatrix(final Vector3fc position,
                                           final Quaternionfc rotation,
                                           final Vector3fc scale) {
        return modelMatrix.
                identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
    }

}
