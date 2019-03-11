package com.cjburkey.radgame.glfw;

import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Set;
import java.util.function.IntConsumer;

/**
 * Created by CJ Burkey on 2019/03/10
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class InputHandler {

    private final Int2BooleanOpenHashMap down = new Int2BooleanOpenHashMap();
    private final IntOpenHashSet up = new IntOpenHashSet();

    InputHandler() {
        down.defaultReturnValue(false);
    }

    void update() {
        down.keySet().forEach((IntConsumer) (i -> down.put(i, false)));
        up.clear();
    }

    void onDown(int code) {
        down.put(code, true);
        up.remove(code);
    }

    void onUp(int code) {
        down.remove(code);
        up.add(code);
    }

    public boolean isDown(int code) {
        return down.containsKey(code);
    }

    public boolean areAllDown(int... codes) {
        for (final var code : codes) {
            if (!isDown(code)) return false;
        }
        return true;
    }

    public boolean isOneDown(int... codes) {
        for (final var code : codes) {
            if (isDown(code)) return true;
        }
        return false;
    }

    public boolean areAllDown(Set<Integer> codes) {
        for (final var code : codes) {
            if (!isDown(code)) return false;
        }
        return true;
    }

    public boolean isOneDown(Set<Integer> codes) {
        for (final var code : codes) {
            if (isDown(code)) return true;
        }
        return false;
    }

    public boolean wasPressed(int code) {
        return down.get(code);
    }

    public boolean wereAllPressed(int... codes) {
        for (final var code : codes) {
            if (!wasPressed(code)) return false;
        }
        return true;
    }

    public boolean wasOnePressed(int... codes) {
        for (final var code : codes) {
            if (wasPressed(code)) return true;
        }
        return false;
    }

    public boolean wereAllPressed(Set<Integer> codes) {
        for (final var code : codes) {
            if (!wasPressed(code)) return false;
        }
        return true;
    }

    public boolean wasOnePressed(Set<Integer> codes) {
        for (final var code : codes) {
            if (wasPressed(code)) return true;
        }
        return false;
    }

    public boolean wasUp(int code) {
        return up.contains(code);
    }

    public boolean wereAllUp(int... codes) {
        for (final var code : codes) {
            if (!wasUp(code)) return false;
        }
        return true;
    }

    public boolean wasOneUp(int... codes) {
        for (final var code : codes) {
            if (wasUp(code)) return true;
        }
        return false;
    }

    public boolean wereAllUp(Set<Integer> codes) {
        for (final var code : codes) {
            if (!wasUp(code)) return false;
        }
        return true;
    }

    public boolean wasOneUp(Set<Integer> codes) {
        for (final var code : codes) {
            if (wasUp(code)) return true;
        }
        return false;
    }

}
