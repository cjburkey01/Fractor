package com.cjburkey.radgame.ecs;

import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.util.ConcurrentManager;
import com.cjburkey.radgame.util.IConcurrentObject;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public final class GameObject implements IConcurrentObject {

    private final ConcurrentManager<Class<? extends Component>, Component> components = new ConcurrentManager<>();

    public final Transform transform = addComponent(new Transform());

    GameObject() {
    }

    public <T extends Component> T addComponent(T component) {
        if (component.parent() != null) return component;
        components.queueAdd(component.getClass(), component);
        component.setParent(this);
        return component;
    }

    public void addComponents(Component... components) {
        for (Component component : components) addComponent(component);
    }

    public void addComponents(Collection<Component> components) {
        components.forEach(this::addComponent);
    }

    public <T extends Component> T removeComponent(T component) {
        if (component.parent() == null) return component;
        components.queueRemove(component.getClass(), component);
        return component;
    }

    public <T extends Component> void removeComponents(Class<T> type) {
        components.queueRemove(type);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> List<T> getComponents(Class<T> type) {
        return (List<T>) components.getObjects(type);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        List<T> components = getComponents(type);
        if (components.size() > 0) return components.get(0);
        return null;
    }

    @Override
    public void onLoad() {
        flush();
    }

    @Override
    public void onRemove() {
        components.queueClear();
        flush();
    }

    @Override
    public int maxPerObject() {
        return Integer.MAX_VALUE;
    }

    public void foreach(Consumer<Component> consumer) {
        components.foreach(consumer);
    }

    public void foreach(Class<? extends Component> type, Consumer<Component> consumer) {
        components.foreach(type, consumer);
    }

    public void flush() {
        components.flush();
    }

}
