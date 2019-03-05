package com.cjburkey.radgame.gl.shader.material;

import com.cjburkey.radgame.Window;
import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.gl.shader.Material;
import com.cjburkey.radgame.gl.shader.Shader;
import java.util.List;

import static com.cjburkey.radgame.util.TransformMath.*;

/**
 * Created by CJ Burkey on 2019/03/04
 */
public abstract class TransformingMaterial extends Material {

    public TransformingMaterial(Shader shader) {
        super(shader);
    }

    public void updateProjection(List<Object> customData, Window window, Camera camera) {
        shader.setUniform("projectionMatrix", getOrthographicMatrix(camera.halfHeight, window.getAspectRatio()));
    }

    public void updateView(List<Object> customData, Camera camera) {
        final var camTransform = camera.parent().transform;
        shader.setUniform("viewMatrix", getViewMatrix(camTransform.position, camTransform.rotation));
    }

    public void updateObject(List<Object> customData, Transform transform) {
        shader.setUniform("modelMatrix", getModelMatrix(transform.position, transform.rotation, transform.scale));
        updateUniforms(customData, transform);
    }

    protected abstract void updateUniforms(List<Object> customData, Transform transform);

}
