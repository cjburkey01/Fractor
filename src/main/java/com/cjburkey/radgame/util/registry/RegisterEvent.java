package com.cjburkey.radgame.util.registry;

import com.cjburkey.radgame.util.event.Event;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings("WeakerAccess")
public abstract class RegisterEvent<T extends IRegistryItem> extends Event {

    public final Registry<T> registry;

    public RegisterEvent(Registry<T> registry) {
        this.registry = registry;
    }

}
