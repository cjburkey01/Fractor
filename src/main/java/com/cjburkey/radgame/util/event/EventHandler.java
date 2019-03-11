package com.cjburkey.radgame.util.event;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.UUID;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings("unused")
public class EventHandler {

    private final Object2ObjectOpenHashMap<Class<? extends Event>, Object2ObjectOpenHashMap<UUID, IEventCallback<?>>> eventListeners = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<UUID, Class<? extends Event>> listenerMapping = new Object2ObjectOpenHashMap<>();

    public <T extends Event> UUID addListener(Class<T> type, IEventCallback<T> eventCallback) {
        final var uuid = UUID.randomUUID();
        getEventHandler(type).put(uuid, eventCallback);
        listenerMapping.put(uuid, type);
        return uuid;
    }

    public void removeListener(UUID listenerId) {
        final var type = listenerMapping.getOrDefault(listenerId, null);
        if (type != null) {
            getEventHandler(type).remove(listenerId);
            listenerMapping.remove(listenerId);
        }
    }

    public <T extends Event> void removeListeners(Class<T> type) {
        eventListeners.remove(type);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void invoke(Class<T> type, T event) {
        final var eventHandler = getEventHandler(type);
        for (IEventCallback<?> listener : eventHandler.values()) {
            ((IEventCallback<T>) listener).accept(event);
            if (event.isCancelled()) return;
        }
    }

    private <T extends Event> Object2ObjectOpenHashMap<UUID, IEventCallback<?>> getEventHandler(Class<T> type) {
        var eventHandler = eventListeners.getOrDefault(type, null);
        if (eventHandler == null) {
            eventHandler = new Object2ObjectOpenHashMap<>();
            eventListeners.put(type, eventHandler);
        }
        return eventHandler;
    }

}
