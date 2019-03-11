package com.cjburkey.radgame.util.event;

import java.util.function.Consumer;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings("WeakerAccess")
@FunctionalInterface
public interface IEventCallback<T extends Event> extends Consumer<T> {

}
