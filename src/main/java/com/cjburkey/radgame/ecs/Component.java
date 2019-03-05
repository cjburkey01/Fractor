package com.cjburkey.radgame.ecs;

import com.cjburkey.radgame.util.IConcurrentObject;

public abstract class Component implements IConcurrentObject {

    private GameObject parent;
    protected int maxPerObject = Integer.MAX_VALUE;

    // Called when the object has been recognized as part of the Scene
    @Override
    public void onLoad() {
    }

    public void update() {
    }

    public void render() {
    }

    // Called just before the object is removed from the scene
    @Override
    public void onRemove() {
    }

    @Override
    public final int maxPerObject() {
        return maxPerObject;
    }

    void setParent(GameObject parent) {
        if (this.parent != null) return;
        this.parent = parent;
    }

    public GameObject parent() {
        return parent;
    }

}
