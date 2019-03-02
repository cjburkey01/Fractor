package com.cjburkey.radgame.gl.shader;

import com.cjburkey.radgame.gl.Material;
import java.util.Objects;

public class Shader<T extends Material> {

    private final RawShader shader;

    public Shader(RawShader shader) {
        this.shader = Objects.requireNonNull(shader);
    }

    public void bind() {
        shader.bind();
    }

    public void delete() {
        shader.close();
    }

}
