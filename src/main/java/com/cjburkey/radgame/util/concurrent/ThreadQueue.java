package com.cjburkey.radgame.util.concurrent;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;

/**
 * Created by CJ Burkey on 2019/03/20
 */
public class ThreadQueue {

    private final ObjectArrayFIFOQueue<Runnable> queuedMainActions = new ObjectArrayFIFOQueue<>();
    private boolean shouldStop = false;

    public void run() {
        synchronized (queuedMainActions) {
            while (!shouldStop && !queuedMainActions.isEmpty()) {
                queuedMainActions.dequeue().run();
            }
            if (shouldStop) queuedMainActions.clear();
        }
    }

    public void stop() {
        shouldStop = true;
    }

    public boolean isStopping() {
        return shouldStop;
    }

    public void queue(Runnable action) {
        if (shouldStop) return;
        synchronized (queuedMainActions) {
            queuedMainActions.enqueue(action);
        }
    }

}
