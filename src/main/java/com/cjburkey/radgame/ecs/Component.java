package com.cjburkey.radgame.ecs;

import com.cjburkey.radgame.util.IConcurrentObject;

public abstract class Component implements IConcurrentObject {

    // Called when the object has been recognized as part of the Scene
    public void onLoad() {
    }

    public void update() {
    }

    public void render() {
    }

    // Called just before the object is removed
    public void onRemove() {
    }

}
