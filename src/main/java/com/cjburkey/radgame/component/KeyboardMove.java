package com.cjburkey.radgame.component;

import com.cjburkey.radgame.Time;
import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.glfw.Input;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Collections;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class KeyboardMove extends Component {

    public float speed = 5.0f;

    public final IntOpenHashSet upKeys = new IntOpenHashSet();
    public final IntOpenHashSet downKeys = new IntOpenHashSet();
    public final IntOpenHashSet rightKeys = new IntOpenHashSet();
    public final IntOpenHashSet leftKeys = new IntOpenHashSet();

    private final Vector2f velocity = new Vector2f();

    private Camera referenceCamera;

    public KeyboardMove(boolean addDefaultKeys) {
        if (addDefaultKeys) {
            Collections.addAll(upKeys, GLFW_KEY_W, GLFW_KEY_UP);
            Collections.addAll(downKeys, GLFW_KEY_S, GLFW_KEY_DOWN);
            Collections.addAll(rightKeys, GLFW_KEY_D, GLFW_KEY_RIGHT);
            Collections.addAll(leftKeys, GLFW_KEY_A, GLFW_KEY_LEFT);
        }
    }

    public KeyboardMove() {
        this(true);
    }

    @Override
    public void onLoad() {
        referenceCamera = parent().getComponent(Camera.class);
    }

    @Override
    public void onUpdate() {
        velocity.zero();

        if (Input.key().isOneDown(upKeys)) velocity.y += 1.0f;
        if (Input.key().isOneDown(downKeys)) velocity.y -= 1.0f;
        if (Input.key().isOneDown(rightKeys)) velocity.x += 1.0f;
        if (Input.key().isOneDown(leftKeys)) velocity.x -= 1.0f;

        if (!velocity.equals(0.0f, 0.0f)) velocity.normalize().mul(speed() * Time.updateDeltaf());

        transform().position.add(velocity.x, velocity.y, 0.0f);
    }

    private float speed() {
        if (referenceCamera == null) return speed;
        return speed * referenceCamera.halfHeight;
    }

}
