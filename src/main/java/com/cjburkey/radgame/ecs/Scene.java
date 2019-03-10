package com.cjburkey.radgame.ecs;

import com.cjburkey.radgame.util.ConcurrentManager;
import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public final class Scene {

    private static final byte KEY = (byte) 0;

    private final ConcurrentManager<Byte, GameObject> objects = new ConcurrentManager<>();

    public GameObject createObject() {
        final var obj = new GameObject();
        objects.queueAdd(KEY, obj);
        return obj;
    }

    public GameObject createObjectWith(final Component... components) {
        final var object = createObject();
        object.addComponents(components);
        return object;
    }

    public void destroy(final GameObject obj) {
        objects.queueRemove(KEY, obj);
    }

    public void foreach(final Consumer<GameObject> consumer) {
        objects.foreach(consumer);
    }

    public void foreachComp(final Consumer<Component> consumer) {
        foreach(obj -> obj.foreach(consumer));
    }

    public void flush() {
        objects.flush();
        objects.foreach(GameObject::flush);
    }

    public void clear() {
        foreach(this::destroy);
        flush();
    }

}
