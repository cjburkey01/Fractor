package com.cjburkey.radgame.gl.shader;

import com.cjburkey.radgame.Window;
import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.component.Transform;
import java.util.List;

public abstract class Material {

    public final Shader shader;

    public Material(Shader shader) {
        this.shader = shader;
    }

    public abstract void updateProjection(final List<Object> customData, final Window window, final Camera camera);

    public abstract void updateView(final List<Object> customData, final Camera camera);

    public abstract void updateObject(final List<Object> customData, final Transform transform);

}
