package com.cjburkey.radgame.component;

import com.cjburkey.radgame.RadGame;
import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.glfw.Input;
import com.cjburkey.radgame.glfw.Window;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import static com.cjburkey.radgame.util.math.TransformMath.*;

/**
 * Created by CJ Burkey on 2019/03/16
 */
public class CursorVoxelPicker extends Component {

    private Window window;
    private final Vector2i blockPos = new Vector2i();

    public void onLoad() {
        window = RadGame.INSTANCE.window();
    }

    public void onUpdate() {
        final var norm = screenToNormalized(window.getWidth(), window.getHeight(), Input.mousePosf());
        final var trans = normalizedToOrthoWorld(norm,
                getOrthographicMatrix(Camera.main.halfHeight, window.getAspectRatio()),
                getViewMatrix(Camera.main.transform().position, Camera.main.transform().rotation));
        blockPos.set((int) Math.floor(trans.x), (int) Math.floor(trans.y));

        transform().position.set(blockPos.x, blockPos.y, transform().position.z);
    }

    public Vector2ic getBlockPos() {
        return blockPos;
    }

}
