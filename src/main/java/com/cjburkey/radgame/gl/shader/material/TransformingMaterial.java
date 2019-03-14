package com.cjburkey.radgame.gl.shader.material;

import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.gl.shader.Material;
import com.cjburkey.radgame.gl.shader.Shader;
import com.cjburkey.radgame.glfw.Window;

import static com.cjburkey.radgame.util.math.TransformMath.*;

/**
 * Created by CJ Burkey on 2019/03/04
 */
@SuppressWarnings("WeakerAccess")
public abstract class TransformingMaterial extends Material {

    public TransformingMaterial(Shader shader) {
        super(shader);
        if (shader.lacksUniform("projectionMatrix") ||
                shader.lacksUniform("viewMatrix") ||
                shader.lacksUniform("modelMatrix")) {
            throw new IllegalArgumentException("Shader for transforming material requires a projection matrix, view matrix, and model matrix uniform");
        }
    }

    @Override
    public void updateProjection(Window window, Camera camera) {
        shader.setUniform("projectionMatrix", getOrthographicMatrix(camera.halfHeight, window.getAspectRatio()));
    }

    @Override
    public void updateView(Camera camera) {
        final var camTransform = camera.transform();
        shader.setUniform("viewMatrix", getViewMatrix(camTransform.position, camTransform.rotation));
    }

    @Override
    public void updateObject(Transform transform) {
        shader.setUniform("modelMatrix", getModelMatrix(transform.position, transform.rotation, transform.scale));
        updateUniforms(transform);
    }

    protected abstract void updateUniforms(Transform transform);

}
