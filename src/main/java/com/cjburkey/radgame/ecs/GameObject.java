package com.cjburkey.radgame.ecs;

import com.cjburkey.radgame.util.ConcurrentManager;
import com.cjburkey.radgame.util.IConcurrentObject;
import java.util.function.Consumer;

public final class GameObject implements IConcurrentObject {

    private final ConcurrentManager<Class<? extends Component>, Component> components = new ConcurrentManager<>();

    GameObject() {
    }

    public <T extends Component> void addComponent(Class<T> type, T component) {
        components.queueAdd(type, component);
    }

    public <T extends Component> void removeComponent(Class<T> type, T component) {
        components.queueRemove(type, component);
    }

    public void onLoad() {
        components.update();
    }

    public void onRemove() {
        components.clear();
        components.update();
    }

    public void foreach(Consumer<Component> consumer) {
        components.foreach(consumer);
    }

    public void foreach(Class<? extends Component> type, Consumer<Component> consumer) {
        components.foreach(type, consumer);
    }

}
