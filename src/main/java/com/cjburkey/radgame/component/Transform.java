package com.cjburkey.radgame.component;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {

    public final Vector3f position = new Vector3f();
    public final Quaternionf rotation = new Quaternionf();
    public final Vector3f scale = new Vector3f(1.0f);

}
