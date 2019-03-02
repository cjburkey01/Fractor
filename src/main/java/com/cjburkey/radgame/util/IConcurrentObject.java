package com.cjburkey.radgame.util;

public interface IConcurrentObject {

    void onLoad();

    void onRemove();

    boolean equals(Object other);

    int hashCode();

}
