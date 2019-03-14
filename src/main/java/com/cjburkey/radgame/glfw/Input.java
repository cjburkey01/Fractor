package com.cjburkey.radgame.glfw;

import java.util.Objects;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by CJ Burkey on 2019/03/10
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Input {

    private static final InputHandler keyboardHandler = new InputHandler();
    private static final InputHandler mouseButtonHandler = new InputHandler();

    private static final Vector2d mousePos = new Vector2d();
    private static final Vector2d deltaMousePos = new Vector2d();
    private static final Vector2d deltaMouseScroll = new Vector2d();

    private static Window window;

    private Input() {
    }

    private static void addCallbacks() {
        glfwSetKeyCallback(window.window, (win, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) key().onDown(key);
            if (action == GLFW_RELEASE) key().onUp(key);
        });
        glfwSetMouseButtonCallback(window.window, (win, button, action, mods) -> {
            if (action == GLFW_PRESS) mouse().onDown(button);
            if (action == GLFW_RELEASE) mouse().onUp(button);
        });
        glfwSetCursorPosCallback(window.window, (win, x, y) -> {
            deltaMousePos.set(x, y).sub(mousePos);
            mousePos.set(x, y);
        });
        glfwSetScrollCallback(window.window, (win, x, y) -> deltaMouseScroll.set(x, y));
    }

    static void init(Window window) {
        if (Input.window == null) {
            Input.window = Objects.requireNonNull(window);
            addCallbacks();
        }
    }

    static void update() {
        key().update();
        mouse().update();

        deltaMousePos.zero();
        deltaMouseScroll.zero();
    }

    public static InputHandler key() {
        return keyboardHandler;
    }

    public static InputHandler mouse() {
        return mouseButtonHandler;
    }

    public static Vector2i mousePosi() {
        return new Vector2i((int) mousePos.x, (int) mousePos.y);
    }

    public static Vector2f mousePosf() {
        return new Vector2f((float) mousePos.x, (float) mousePos.y);
    }

    public static Vector2i deltaMousei() {
        return new Vector2i((int) deltaMousePos.x, (int) deltaMousePos.y);
    }

    public static Vector2f deltaMousef() {
        return new Vector2f((float) deltaMousePos.x, (float) deltaMousePos.y);
    }

    public static Vector2i scrolli() {
        return new Vector2i((int) deltaMouseScroll.x, (int) deltaMouseScroll.y);
    }

    public static Vector2f scrollf() {
        return new Vector2f((float) deltaMouseScroll.x, (float) deltaMouseScroll.y);
    }

}
