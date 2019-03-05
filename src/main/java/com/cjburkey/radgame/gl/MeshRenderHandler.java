package com.cjburkey.radgame.gl;

import com.cjburkey.radgame.Window;
import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.gl.shader.Material;
import java.util.List;

/**
 * Created by CJ Burkey on 2019/03/04
 */
public class MeshRenderHandler {

    public static void render(List<Object> customData, Mesh mesh, Material material, Window window, Camera camera, Transform transform) {
        if (mesh == null || material == null || window == null || camera == null || transform == null) return;

        material.shader.bind();
        material.updateProjection(customData, window, camera);
        material.updateView(customData, camera);
        material.updateObject(customData, transform);
        mesh.render();
    }

}
