package com.cjburkey.radgame.ecs;

import com.cjburkey.radgame.util.collection.IConcurrentObject;

public abstract class Component implements IConcurrentObject {

    private GameObject parent;

    // Called when the object has been recognized as part of the Scene
    @Override
    public void onLoad() {
    }

    public void onUpdate() {
    }

    public void onRender() {
    }

    // Called just before the object is removed from the scene
    @Override
    public void onRemove() {
    }

    @Override
    public final int maxPerObject() {
        return 1;
    }

    void setParent(GameObject parent) {
        if (this.parent != null) return;
        this.parent = parent;
    }

    public GameObject parent() {
        return parent;
    }

}
