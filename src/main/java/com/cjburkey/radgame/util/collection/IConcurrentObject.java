package com.cjburkey.radgame.util.collection;

public interface IConcurrentObject {

    void onLoad();

    void onRemove();

    int maxPerObject();

    @Override
    boolean equals(Object other);

    @Override
    int hashCode();

}
