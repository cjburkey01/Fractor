package com.cjburkey.radgame;

import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.game.GameManager;
import com.cjburkey.radgame.glfw.Window;
import com.cjburkey.radgame.util.event.Event;
import com.cjburkey.radgame.util.io.Log;
import java.io.Closeable;

import static org.lwjgl.opengl.GL30.*;

@SuppressWarnings("FieldCanBeLocal")
public class RadGame implements Runnable, Closeable {

    public static RadGame INSTANCE = new RadGame();

    private boolean running = false;
    private Window window;

    private final Scene scene = new Scene();

    static {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Log.exception(e));

        // These are the default JOML settings; I can change them later if they become important
        System.setProperty("joml.debug", "false");
        System.setProperty("joml.nounsafe", "false");
        System.setProperty("joml.fastmath", "false");
        System.setProperty("joml.sinLookup", "false");
        System.setProperty("joml.sinLookup.bits", "14");
        System.setProperty("joml.format", "true");
        System.setProperty("joml.format.decimals", "2");

        Log.debug("Preinit complete");
    }

    public static void main(String[] args) {
        INSTANCE.run();
        INSTANCE.onExit();
    }

    private RadGame() {
    }

    private void initWindow() {
        window = new Window(300, 300, "Fractor 0.0.1");
        window.setWindowSizeRatioMonitor(2, 3);
        window.center();
        window.show();
    }

    private void startGameLoop() {
        final var targetUpdate = Time.updateDelta();
        var currentTime = Time.getTime();
        var accumulator = 0.0;
        scene.flush();

        while (running) {
            final var newTime = Time.getTime();
            final var frameTime = newTime - currentTime;
            currentTime = newTime;
            accumulator += frameTime;

            processInput();
            while (accumulator >= targetUpdate) {
                update();
                accumulator -= targetUpdate;
            }
            Time.setRenderDelta(frameTime);
            render();
        }
    }

    @Override
    public void run() {
        Log.debug("Initializing");

        initWindow();
        window.setClearColor(0.48f, 0.74f, 1.0f);   // Sky blue :)
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_TEXTURE);
        glActiveTexture(GL_TEXTURE0);

        init();
        running = true;

        Log.debug("Initialized");
        startGameLoop();
    }

    @Override
    public void close() {
        running = false;
    }

    private void onExit() {
        scene.clear();

        window.destroy();
        Window.terminate();
    }

    private void init() {
        GameManager.install(scene);
    }

    private void processInput() {
        window.pollEvents();
        if (window.getShouldClose()) {
            close();
        }
    }

    private void update() {
        scene.flush();
        scene.foreachComp(Component::onUpdate);
    }

    private void render() {
        window.clear();

        scene.flush();
        scene.foreachComp(Component::onRender);

        window.swapBuffers();
    }

    public Window window() {
        return window;
    }

    // This must be invoked by the game, the raw game engine won't do it.
    // I'm trying to delegate but I don't want cleanup to be completely separate
    //  from the engine itself, so this is my compromise: more complicated stuff ;)
    public static final class EventCleanup extends Event {

    }


}
