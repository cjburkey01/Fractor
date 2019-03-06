package com.cjburkey.radgame.gl.shader;

import com.cjburkey.radgame.Window;
import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.component.Transform;

public abstract class Material {

    public final Shader shader;

    public Material(Shader shader) {
        this.shader = shader;
    }

    public abstract void updateProjection(final Window window, final Camera camera);

    public abstract void updateView(final Camera camera);

    public abstract void updateObject(final Transform transform);

}
