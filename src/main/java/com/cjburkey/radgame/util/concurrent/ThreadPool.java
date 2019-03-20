package com.cjburkey.radgame.util.concurrent;

import com.cjburkey.radgame.util.io.Log;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;

/**
 * Created by CJ Burkey on 2019/03/20
 */
public class ThreadPool {

    private static final int sleepMs = 5;

    private final ObjectArrayFIFOQueue<Runnable> queuedConcurrentActions = new ObjectArrayFIFOQueue<>();
    private final ThreadPoolThread[] threads;
    private boolean shouldStop = false;

    public ThreadPool(int threadCount) {
        if (threadCount < 2 || threadCount > 16)
            throw new IllegalArgumentException("Illegal number of threads in thread pool: " + threadCount + ", it must be between 2 and 16, inclusively.");

        threads = new ThreadPoolThread[threadCount];
        for (var i = 0; i < threadCount; i++) threads[i] = new ThreadPoolThread().init();
    }

    public void stop() {
        shouldStop = true;
    }

    public boolean isStopping() {
        return (shouldStop && !isStopped());
    }

    private boolean isStopped() {
        if (!shouldStop) return false;
        for (final var thread : threads) {
            if (!thread.stopped) return false;
        }
        return true;
    }

    public void queueAction(Runnable action) {
        if (shouldStop) return;
        synchronized (queuedConcurrentActions) {
            queuedConcurrentActions.enqueue(action);
        }
    }

    private final class ThreadPoolThread extends Thread {

        private boolean stopped = false;

        private ThreadPoolThread init() {
            start();
            return this;
        }

        public void run() {
            boolean needsToWait;
            while (!shouldStop) {
                synchronized (queuedConcurrentActions) {
                    if (!(needsToWait = queuedConcurrentActions.isEmpty())) {
                        queuedConcurrentActions.dequeue().run();
                    }
                }
                if (needsToWait) {
                    try {
                        Thread.sleep(sleepMs);
                    } catch (Exception e) {
                        Log.exception(e);
                    }
                }
            }
            stopped = true;
        }

    }

}
