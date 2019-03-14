package com.cjburkey.radgame.glfw;

import java.util.Objects;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by CJ Burkey on 2019/03/03
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Window {

    long window;
    private final Vector2i windowSize = new Vector2i();
    private final Vector2i windowPos = new Vector2i();
    private final Vector3f clearColor = new Vector3f();
    private float aspectRatio;
    private String title;
    private boolean shouldClose;
    private boolean vsync;

    static {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            glfwTerminate();
            throw new IllegalStateException("Failed to initialize GLFW");
        }
    }

    public Window(int width, int height, String title) {
        windowSize.set(width, height);
        this.title = title;

        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(windowSize.x, windowSize.y, title, NULL, NULL);
        if (window == NULL) {
            throw new IllegalStateException("Failed to initialize GLFW window");
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glfwSetFramebufferSizeCallback(window, (win, w, h) -> {
            glViewport(0, 0, w, h);
            windowSize.set(w, h);
            aspectRatio = (float) w / h;
        });
        glfwSetWindowPosCallback(window, (win, x, y) -> windowPos.set(x, y));
        Input.init(this);

        setVsync(true);
    }

    public void show() {
        glfwShowWindow(window);
    }

    public void hide() {
        glfwHideWindow(window);
    }

    public void destroy() {
        hide();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }

    public void pollEvents() {
        Input.update();
        glfwPollEvents();
        shouldClose = glfwWindowShouldClose(window);
    }

    public void setSize(int width, int height) {
        glfwSetWindowSize(window, width, height);
    }

    public void setPos(int x, int y) {
        glfwSetWindowPos(window, x, y);
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(window, title);
        this.title = title;
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }

    public void setWindowSizeRatioMonitor(int num, int denom) {
        var monitorSize = getPrimaryWindowSize();
        monitorSize.set(monitorSize.x * num / denom, monitorSize.y * num / denom);
        setSize(monitorSize.x, monitorSize.y);
    }

    public void center() {
        Vector2i monitorSize = getPrimaryWindowSize();
        setPos((monitorSize.x - windowSize.x) / 2, (monitorSize.y - windowSize.y) / 2);
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        glfwSwapInterval(vsync ? 1 : 0);
    }

    public void clear(int mask) {
        glClear(mask);
    }

    public void clear() {
        clear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    }

    public void setClearColor(float r, float g, float b) {
        glClearColor(r, g, b, 1.0f);
        clearColor.set(r, g, b);
    }

    public boolean getShouldClose() {
        return shouldClose;
    }

    public int getWidth() {
        return windowSize.x;
    }

    public int getHeight() {
        return windowSize.y;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public int getPosX() {
        return windowPos.x;
    }

    public int getPosY() {
        return windowPos.y;
    }

    public String getTitle() {
        return title;
    }

    public long getPrimaryMonitor() {
        return glfwGetPrimaryMonitor();
    }

    public Vector2i getWindowSize(long window) {
        var vidMode = Objects.requireNonNull(glfwGetVideoMode(window));
        return new Vector2i(vidMode.width(), vidMode.height());
    }

    public Vector2i getPrimaryWindowSize() {
        return getWindowSize(getPrimaryMonitor());
    }

    public boolean getVsync() {
        return vsync;
    }

    public Vector3f getClearColor() {
        return new Vector3f(clearColor);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Window window1 = (Window) o;
        return window == window1.window;
    }

    public int hashCode() {
        return Objects.hash(window);
    }

    public static void terminate() {
        glfwTerminate();

        final var a = glfwSetErrorCallback(null);
        if (a != null) a.free();
    }

}
