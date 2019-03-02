package com.cjburkey.radgame;

import com.cjburkey.radgame.gl.Mesh;
import com.cjburkey.radgame.gl.shader.RawShader;
import java.io.Closeable;
import java.lang.Math;
import java.util.Objects;
import org.joml.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

@SuppressWarnings("FieldCanBeLocal")
public class RadGame implements Runnable, Closeable {

    private static Matrix4f projectionMatrix = new Matrix4f();
    private static Matrix4f viewMatrix = new Matrix4f();
    private static Matrix4f modelMatrix = new Matrix4f();

    private long window = 0L;
    private boolean running = false;
    private final Vector2i windowSize = new Vector2i(300, 300);

    private RawShader rawShader;
    private Mesh mesh;

    public static void main(String[] args) {
        var game = new RadGame();
        game.run();
        game.close();
    }

    private RadGame() {
    }

    private void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(windowSize.x, windowSize.y, "RadGame 0.0.1", NULL, NULL);
        if (window == NULL) {
            throw new IllegalStateException("Failed to initialize window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        GL.createCapabilities();

        glfwSetFramebufferSizeCallback(window, (win, w, h) -> {
            glViewport(0, 0, w, h);
            windowSize.set(w, h);
        });

        glfwShowWindow(window);
    }

    private void initShader() {
        rawShader = RawShader.builder()
                .setVertexShader(new ResourceLocation("radgame", "shader/vertShader", "glsl"))
                .setFragmentShader(new ResourceLocation("radgame", "shader/fragShader", "glsl"))
                .addUniforms("projectionMatrix", "viewMatrix", "modelMatrix")
                .build();
    }

    private void startGameLoop() {
        running = true;
        while (running) {
            glfwPollEvents();
            if (glfwWindowShouldClose(window)) {
                running = false;
            }
            update();
            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
            render();
            glfwSwapBuffers(window);
        }
    }

    @Override
    public void run() {
        initWindow();

        glEnable(GL_DEPTH_TEST);
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        initShader();

        init();
        startGameLoop();
    }

    private void initTestMesh() {
        var vertices = new float[] {
                0.0f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
        };

        var indices = new short[] {
                0, 1, 2,
        };

        mesh = new Mesh().setVertices(vertices).setIndices(indices);
    }

    private void renderTestMesh() {
        rawShader.bind();
        rawShader.setUniform("projectionMatrix", getProjectionMatrix(90.0f,
                windowSize.x,
                windowSize.y,
                0.01f,
                100.0f));
        rawShader.setUniform("viewMatrix", getViewMatrix(new Vector3f(0.0f, 0.0f, 1.0f),
                new Quaternionf()));
        rawShader.setUniform("modelMatrix", getModelMatrix(new Vector3f(),
                new Quaternionf(),
                new Vector3f(1.0f)));

        mesh.render();
    }

    private void deleteTestMesh() {
        mesh.destroy();
    }

    @Override
    public void close() {
        exit();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void init() {
        initTestMesh();
    }

    private void render() {
        renderTestMesh();
    }

    private void update() {

    }

    private void exit() {
        deleteTestMesh();
    }

    private static Matrix4fc getProjectionMatrix(final float fovDegrees,
                                                 final int windowWidth,
                                                 final int windowHeight,
                                                 final float near,
                                                 final float far) {
        final var fovRad = (float) Math.toRadians(fovDegrees);
        final var aspect = (float) windowWidth / windowHeight;
        return projectionMatrix
                .identity()
                .perspective(fovRad, aspect, near, far);
    }

    private static Matrix4fc getViewMatrix(final Vector3fc cameraPosition,
                                           final Quaternionfc cameraRotation) {
        return viewMatrix
                .identity()
                .translate(-cameraPosition.x(), -cameraPosition.y(), -cameraPosition.z())
                .rotate(cameraRotation.invert(new Quaternionf()));
    }

    private static Matrix4fc getModelMatrix(final Vector3fc position,
                                            final Quaternionfc rotation,
                                            final Vector3fc scale) {
        return modelMatrix.
                identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
    }

}
