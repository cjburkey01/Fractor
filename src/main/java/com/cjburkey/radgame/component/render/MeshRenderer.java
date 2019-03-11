package com.cjburkey.radgame.component.render;

import com.cjburkey.radgame.RadGame;
import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.gl.Mesh;
import com.cjburkey.radgame.gl.MeshRenderHandler;

/**
 * Created by CJ Burkey on 2019/03/03
 */
public class MeshRenderer extends Component {

    public final Mesh mesh = new Mesh();

    @Override
    public void onRender() {
        if (Camera.main == null) return;

        final var shaderRenderer = parent().getComponent(MaterialRenderer.class);
        if (shaderRenderer != null && shaderRenderer.material != null && shaderRenderer.material.shader != null) {
            MeshRenderHandler.render(mesh, shaderRenderer.material, RadGame.INSTANCE.window(), Camera.main, parent().transform);
        }
    }

    @Override
    public void onRemove() {
        mesh.destroy();
    }

}
