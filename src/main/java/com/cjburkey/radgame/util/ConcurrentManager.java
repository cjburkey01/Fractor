package com.cjburkey.radgame.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public final class ConcurrentManager<K, T extends IConcurrentObject> {

    private final ConcurrentLinkedQueue<QueuedObject> toAdd = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<QueuedObject> toRemove = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<K, ArrayList<T>> objects = new ConcurrentHashMap<>();

    public void queueAdd(K key, T object) {
        toAdd.offer(new QueuedObject(key, object));
    }

    public void queueRemove(K key, T object) {
        toRemove.offer(new QueuedObject(key, object));
    }

    public void queueRemove(K key) {
        toRemove.offer(new QueuedObject(key, null));
    }

    public void flush() {
        while (!toRemove.isEmpty()) {
            var queuedObject = toRemove.poll();
            if (queuedObject != null) {
                if (queuedObject.object == null) {
                    objects.remove(queuedObject.key);
                } else {
                    queuedObject.object.onRemove();
                    getList(queuedObject.key).remove(queuedObject.object);
                }
            }
        }
        while (!toAdd.isEmpty()) {
            var queuedObject = toAdd.poll();
            if (queuedObject != null) {
                List<T> list = getList(queuedObject.key);
                if (list.size() < queuedObject.object.maxPerObject()) {
                    list.add(queuedObject.object);
                    queuedObject.object.onLoad();
                }
            }
        }
    }

    public void foreach(K key, Consumer<T> consumer) {
        getList(key).forEach(consumer);
    }

    public void foreach(Consumer<T> consumer) {
        objects.values().forEach(list -> list.forEach(consumer));
    }

    public void queueClear() {
        toRemove.clear();
        for (K key : objects.keySet()) {
            for (T object : getList(key)) queueRemove(key, object);
        }
    }

    public List<T> getObjects(K key) {
        return Collections.unmodifiableList(getList(key));
    }

    private ArrayList<T> getList(K key) {
        var list = objects.getOrDefault(key, null);
        if (list == null) {
            list = new ArrayList<>();
            objects.put(key, list);
        }
        return list;
    }

    private final class QueuedObject {

        private final K key;
        private final T object;

        private QueuedObject(K key, T object) {
            this.key = Objects.requireNonNull(key);
            this.object = object;
        }

        public int hashCode() {
            return key.hashCode();
        }

        @SuppressWarnings("unchecked")
        public boolean equals(Object other) {
            if (other instanceof ConcurrentManager.QueuedObject) {
                return key.equals(((QueuedObject) other).key);
            }
            return false;
        }

    }

}
