package com.cjburkey.radgame.shader.material;

import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.shader.Shader;
import com.cjburkey.radgame.texture.Texture;

/**
 * Created by CJ Burkey on 2019/03/05
 */
public class TexturedTransform extends TransformingMaterial {

    public Texture texture;

    public TexturedTransform(Shader shader) {
        super(shader);
        if (shader.lacksUniform("tex")) {
            throw new IllegalArgumentException("Shader for textured material requires a tex uniform");
        }
    }

    @Override
    protected void updateUniforms(Transform transform) {
        shader.setUniform("tex", 0);
        if (texture != null) texture.bind();
    }

}
