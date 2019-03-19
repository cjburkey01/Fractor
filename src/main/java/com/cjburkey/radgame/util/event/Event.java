package com.cjburkey.radgame.util.event;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Event {

    private boolean cancelled = false;
    protected boolean cancellable = false;

    public final boolean isCancelled() {
        return (cancellable && cancelled);
    }

    public final void cancel() {
        if (cancellable) cancelled = true;
    }

}
