package com.cjburkey.radgame.util.event;

import com.cjburkey.radgame.util.concurrent.ThreadQueue;
import com.cjburkey.radgame.util.io.Log;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings("unused")
public final class EventHandler {

    private final Object2ObjectOpenHashMap<Class<? extends Event>, Object2ObjectOpenHashMap<UUID, IEventCallback<?>>> eventListeners = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<UUID, Class<? extends Event>> listenerMapping = new Object2ObjectOpenHashMap<>();
    private final ThreadQueue threadQueue;

    public EventHandler(boolean threadSafe) {
        threadQueue = (threadSafe ? new ThreadQueue() : null);
    }

    public void updateThreadSafe() {
        if (threadQueue != null) threadQueue.run();
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T extends Event> UUID addListener(Class<T> type, IEventCallback<T> eventCallback) {
        synchronized (listenerMapping) {
            synchronized (eventListeners) {
                final var uuid = UUID.randomUUID();
                getEventHandler(type).put(uuid, eventCallback);
                listenerMapping.put(uuid, type);
                return uuid;
            }
        }
    }

    public void removeListener(UUID listenerId) {
        // Note: should these objects be LOCKED, if "eventListeners" is locked first, the main thread will freeze.
        // DO NOT LOCK eventListeners, EVER!
        // Neither should need to be manually locked, anyway.
        synchronized (listenerMapping) {
            synchronized (eventListeners) {
                final var type = listenerMapping.getOrDefault(listenerId, null);
                if (type != null) {
                    getEventHandler(type).remove(listenerId);
                    listenerMapping.remove(listenerId);
                }
            }
        }
    }

    public <T extends Event> void invokeSafe(final T event) {
        if (threadQueue != null) threadQueue.queue(() -> rawInvoke(event));
        else rawInvoke(event);
    }

    public <T extends Event> void forceInvoke(final T event) {
        rawInvoke(event);
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> void rawInvoke(T event) {
        synchronized (eventListeners) {
            final var clazz = event.getClass();
            final var eventHandler = getEventHandler(clazz);
            try {
                for (IEventCallback<?> listener : eventHandler.values()) {
                    ((IEventCallback<T>) listener).accept(event);
                    if (event.isCancelled()) return;
                }
            } catch (Exception e) {
                Log.error("Unhandled exception while invoking event: \"{}\"", clazz.getSimpleName());
                Log.error("    Full name: \"{}\"", clazz.getName());
                Log.exception(e);
            }
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

    @FunctionalInterface
    public interface IEventCallback<T extends Event> extends Consumer<T> {

    }

}
