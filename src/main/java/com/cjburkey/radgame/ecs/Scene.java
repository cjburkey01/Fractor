package com.cjburkey.radgame.ecs;

import com.cjburkey.radgame.util.ConcurrentManager;
import java.util.function.Consumer;

public class Scene {

    private byte KEY = (byte) 0;
    private final ConcurrentManager<Byte, GameObject> objects = new ConcurrentManager<>();

    public GameObject createObject() {
        GameObject obj = new GameObject();
        objects.queueAdd(KEY, obj);
        return obj;
    }

    public GameObject createObjectWith(Component... components) {
        var object = createObject();
        object.addComponents(components);
        return object;
    }

    public void destroy(GameObject obj) {
        objects.queueRemove(KEY, obj);
    }

    public void foreach(Consumer<GameObject> consumer) {
        objects.foreach(consumer);
    }

    public void foreachComp(Consumer<Component> consumer) {
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
