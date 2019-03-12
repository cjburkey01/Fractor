package com.cjburkey.radgame.util.registry;

import com.cjburkey.radgame.ResourceLocation;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by CJ Burkey on 2019/03/10
 */
@SuppressWarnings("WeakerAccess")
public final class Registry<T extends IRegistryItem> {

    private final Object2ObjectOpenHashMap<ResourceLocation, T> items = new Object2ObjectOpenHashMap<>();
    private boolean finished;

    public void registerItem(final T item) {
        if (finished || item == null || item.getRegistryId() == null) return;
        items.put(item.getRegistryId(), item);
    }

    @SafeVarargs
    public final void registerItems(final T... items) {
        for (T item : items) registerItem(item);
    }

    public void registerItems(Collection<T> items) {
        items.forEach(this::registerItem);
    }

    @SuppressWarnings("WeakerAccess")
    public boolean hasItem(final ResourceLocation registryId) {
        return items.containsKey(registryId);
    }

    public boolean hasItem(final T item) {
        return hasItem(item.getRegistryId());
    }

    public T getItem(final ResourceLocation registryId) {
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
