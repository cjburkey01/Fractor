package com.cjburkey.radgame.ecs;

import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.util.collection.IConcurrentObject;
import java.util.Objects;

public abstract class Component implements IConcurrentObject {

    private GameObject parent;
    private Transform parentTransform;

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
    public int maxPerObject() {
        return 1;
    }

    void setParent(GameObject parent) {
        if (this.parent == null && parentTransform == null) {
            this.parent = Objects.requireNonNull(parent);
            parentTransform = parent.transform;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public GameObject parent() {
        return parent;
    }

    public Transform transform() {
        return parentTransform;
    }

}
