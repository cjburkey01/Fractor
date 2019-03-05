package com.cjburkey.radgame.component;

import com.cjburkey.radgame.ecs.Component;

/**
 * Created by CJ Burkey on 2019/03/03
 */
public class Camera extends Component {

    public static Camera main;

    public float halfHeight = 1.5f;

    public Camera() {
        if (main == null) main = this;
    }

}
