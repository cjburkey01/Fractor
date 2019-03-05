package com.cjburkey.radgame;

import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.game.GameManager;
import java.io.Closeable;

import static org.lwjgl.opengl.GL30.*;

@SuppressWarnings("FieldCanBeLocal")
public class RadGame implements Runnable, Closeable {

    public static RadGame INSTANCE = new RadGame();

    private boolean running = false;
    private Window window;

    private final Scene scene = new Scene();

    static {
        // These are the default JOML settings; I can change them later if they become important
        System.setProperty("joml.debug", "false");
        System.setProperty("joml.nounsafe", "false");
        System.setProperty("joml.fastmath", "false");
        System.setProperty("joml.sinLookup", "false");
        System.setProperty("joml.sinLookup.bits", "14");
        System.setProperty("joml.format", "true");
        System.setProperty("joml.format.decimals", "3");
    }

    public static void main(String[] args) {
        INSTANCE.run();
        INSTANCE.close();
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
        initWindow();
        window.setClearColor(0.1f, 0.1f, 0.1f);
        glEnable(GL_DEPTH_TEST);

        init();
        running = true;
        startGameLoop();
    }

    @Override
    public void close() {
        exit();

        window.destroy();
        Window.terminate();
    }

    private void init() {
        GameManager.install(scene);
    }

    private void processInput() {
        window.pollEvents();
        if (window.getShouldClose()) {
            running = false;
        }
    }

    private void update() {
        scene.flush();
        scene.foreachComp(Component::update);
    }

    private void render() {
        window.clear();

        scene.flush();
        scene.foreachComp(Component::render);

        window.swapBuffers();
    }

    private void exit() {
        scene.clear();
    }

    public Window window() {
        return window;
    }


}
