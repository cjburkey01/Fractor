package com.cjburkey.radgame.ecs;

import com.cjburkey.radgame.util.ConcurrentManager;
import java.util.function.Consumer;

public class Scene {

    private final ConcurrentManager<Void, GameObject> objects = new ConcurrentManager<>();

    public GameObject createGameObject() {
        GameObject obj = new GameObject();
        objects.queueAdd(null, obj);
        return obj;
    }

    public GameObjectBuilder buildGameObject() {

    }

    public void destroy(GameObject obj) {
        objects.queueRemove(null, obj);
    }

    public void foreach(Consumer<GameObject> consumer) {
        objects.foreach(consumer);
    }

    public static class GameObjectBuilder {

        private final

    }

}
