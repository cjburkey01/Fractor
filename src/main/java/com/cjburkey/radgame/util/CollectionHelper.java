package com.cjburkey.radgame.util;

import java.util.List;

/**
 * Created by CJ Burkey on 2019/03/04
 */
public final class CollectionHelper {

    public static <T> void setItemInList(List<T> list, int index, T item) {
        if (index < 0) throw new IndexOutOfBoundsException();

        while (list.size() < index) list.add(null);
        if (index == list.size()) {
            list.add(item);
        } else {
            list.set(index, item);
        }
    }

}
