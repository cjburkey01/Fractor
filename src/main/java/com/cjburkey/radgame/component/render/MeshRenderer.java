package com.cjburkey.radgame.component.render;

import com.cjburkey.radgame.RadGame;
import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.gl.Mesh;
import com.cjburkey.radgame.gl.MeshRenderHandler;
import java.util.ArrayList;

/**
 * Created by CJ Burkey on 2019/03/03
 */
public class MeshRenderer extends Component {

    public final Mesh mesh = new Mesh();
    public final ArrayList<Object> customData = new ArrayList<>();

    @Override
    public void render() {
        if (Camera.main == null) return;

        final var shaderRenderer = parent().getComponent(MaterialRenderer.class);
        if (shaderRenderer != null && shaderRenderer.material != null && shaderRenderer.material.shader != null) {
            MeshRenderHandler.render(customData, mesh, shaderRenderer.material, RadGame.INSTANCE.window(), Camera.main, parent().transform);
        }
    }

    @Override
    public void onRemove() {
        mesh.destroy();
    }

}
