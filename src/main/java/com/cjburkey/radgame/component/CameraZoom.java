package com.cjburkey.radgame.component;

import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.glfw.Input;
import com.cjburkey.radgame.util.io.Log;
import com.cjburkey.radgame.util.math.Interpolate;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings("WeakerAccess")
public class CameraZoom extends Component {

    public float speed = 1.0f;
    public float min = 2.35f;
    public float max = 170.0f;

    private Camera camera;

    @Override
    public void onLoad() {
        camera = parent().getComponent(Camera.class);
        if (camera == null) Log.error("Failed to locate Camera on CameraZoom object");
    }

    @Override
    public void onUpdate() {
        if (camera == null) return;

        camera.halfHeight = Interpolate.clamp((camera.halfHeight + (speed * -Input.scrollf().y)), min, max);
    }

}
