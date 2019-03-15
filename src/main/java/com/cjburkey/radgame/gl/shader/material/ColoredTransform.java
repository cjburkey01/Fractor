package com.cjburkey.radgame.gl.shader.material;

import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.gl.shader.Shader;
import org.joml.Vector4f;

/**
 * Created by CJ Burkey on 2019/03/05
 */
public class ColoredTransform extends TransformingMaterial {

    public final Vector4f color = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    public ColoredTransform(Shader shader) {
        super(shader);
        if (shader.lacksUniform("color")) {
            throw new IllegalArgumentException("Shader for textured material requires a tex uniform");
        }
    }

    @Override
    protected void updateUniforms(Transform transform) {
        shader.setUniform("color", color);
    }

}
