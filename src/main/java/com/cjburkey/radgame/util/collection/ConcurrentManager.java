package com.cjburkey.radgame.util.collection;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public final class ConcurrentManager<K, T extends ConcurrentManager.IConcurrentObject> {

    private final ConcurrentLinkedQueue<QueuedObject> toAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<QueuedObject> toRemove = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<K, ObjectArrayList<T>> objects = new ConcurrentHashMap<>();

    public void queueAdd(final K key, final T object) {
        toAdd.offer(new QueuedObject(key, object));
    }

    public void queueRemove(final K key, final T object) {
        toRemove.offer(new QueuedObject(key, object));
    }

    public void queueRemove(final K key) {
        toRemove.offer(new QueuedObject(key, null));
    }

    public void flush() {
        while (!toRemove.isEmpty()) {
            final var queuedObject = toRemove.poll();
            if (queuedObject != null) {
                if (queuedObject.object == null) {
                    final var objs = getObjects(queuedObject.key);
                    objs.forEach(obj -> {
                        obj.onRemove();
                        obj.invalidate();
                    });
                    objs.clear();
                    objects.remove(queuedObject.key);
                } else {
                    final var obj = queuedObject.object;
                    obj.onRemove();
                    obj.invalidate();
                    getList(queuedObject.key).remove(queuedObject.object);
                }
            }
        }
        while (!toAdd.isEmpty()) {
            final var queuedObject = toAdd.poll();
            if (queuedObject != null) {
                ObjectArrayList<T> list = getList(queuedObject.key);
                if (list.size() < queuedObject.object.maxPerObject()) {
                    list.add(queuedObject.object);
                    queuedObject.object.onLoad();
                }
            }
        }
    }

    public void foreach(final K key, final Consumer<T> consumer) {
        getList(key).forEach(consumer);
    }

    public void foreach(final Consumer<T> consumer) {
        objects.values().forEach(list -> list.forEach(consumer));
    }

    public void queueClear() {
        toRemove.clear();
        for (final var key : objects.keySet()) {
            for (final var object : getList(key)) queueRemove(key, object);
        }
    }

    public List<T> getObjects(final K key) {
        return Collections.unmodifiableList(getList(key));
    }

    private ObjectArrayList<T> getList(final K key) {
        var list = objects.getOrDefault(key, null);
        if (list == null) {
            list = new ObjectArrayList<>();
            objects.put(key, list);
        }
        return list;
    }

    public interface IConcurrentObject {

        void onLoad();

        void onRemove();

        int maxPerObject();

        @Override
        boolean equals(Object other);

        @Override
        int hashCode();

        default void invalidate() {
        }

    }

    private final class QueuedObject {

        private final K key;
        private final T object;

        private QueuedObject(K key, T object) {
            this.key = Objects.requireNonNull(key);
            this.object = object;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object other) {
            if (other instanceof ConcurrentManager.QueuedObject) {
                return key.equals(((QueuedObject) other).key);
            }
            return false;
        }

    }

}
