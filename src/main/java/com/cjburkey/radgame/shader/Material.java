package com.cjburkey.radgame.shader;

import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.glfw.Window;

public abstract class Material {

    public final Shader shader;
    public boolean wireframe;

    public Material(Shader shader) {
        this.shader = shader;
    }

    public abstract void updateProjection(final Window window, final Camera camera);

    public abstract void updateView(final Camera camera);

    public abstract void updateObject(final Transform transform);

}
