package com.cjburkey.radgame.util.registry;

import com.cjburkey.radgame.ResourceLocation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by CJ Burkey on 2019/03/10
 */
public class Registry<T extends IRegistryItem> {

    private final HashMap<ResourceLocation, T> items = new HashMap<>();
    private boolean finished;

    public void registerItem(T item) {
        if (finished || item == null || item.getRegistryId() == null) return;
        items.put(item.getRegistryId(), item);
    }

    @SuppressWarnings("WeakerAccess")
    public boolean hasItem(ResourceLocation registryId) {
        return items.containsKey(registryId);
    }

    public boolean hasItem(T item) {
        return hasItem(item.getRegistryId());
    }

    public T getItem(ResourceLocation registryId) {
        return items.getOrDefault(registryId, null);
    }

    public void finish() {
        finished = true;
    }

    public boolean isFinished() {
        return finished;
    }

    public Collection<T> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public void foreach(Consumer<T> consumer) {
        items.values().forEach(consumer);
    }

    public void foreach(BiConsumer<ResourceLocation, T> consumer) {
        items.forEach(consumer);
    }

}
