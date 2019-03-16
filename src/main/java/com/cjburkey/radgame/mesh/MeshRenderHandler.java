package com.cjburkey.radgame.mesh;

import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.glfw.Window;
import com.cjburkey.radgame.shader.Material;

/**
 * Created by CJ Burkey on 2019/03/04
 */
public class MeshRenderHandler {

    private MeshRenderHandler() {
    }

    public static void render(Mesh mesh, Material material, Window window, Camera camera, Transform transform) {
        if (mesh == null || material == null || window == null || camera == null || transform == null) return;

        material.shader.bind();
        material.updateProjection(window, camera);
        material.updateView(camera);
        material.updateObject(transform);
        mesh.render();
    }

}
