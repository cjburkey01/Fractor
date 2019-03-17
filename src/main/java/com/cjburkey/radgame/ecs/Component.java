package com.cjburkey.radgame.ecs;

import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.util.collection.ConcurrentManager;
import java.util.Objects;

public abstract class Component implements ConcurrentManager.IConcurrentObject {

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

    @Override
    public final void invalidate() {
        setParent(null);
    }

    void setParent(GameObject parent) {
        if (parent != null && this.parent == null && parentTransform == null) {
            this.parent = Objects.requireNonNull(parent);
            parentTransform = parent.transform;
        } else if (parent == null) {
            this.parent = null;
            parentTransform = null;
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
