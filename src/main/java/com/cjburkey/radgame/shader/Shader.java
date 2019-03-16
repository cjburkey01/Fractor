package com.cjburkey.radgame.shader;

import com.cjburkey.radgame.RadGame;
import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.game.GameManager;
import com.cjburkey.radgame.util.io.Log;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.Closeable;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Collections;
import org.joml.Matrix3fc;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.*;

public class Shader implements Closeable {

    private static int currentProgram = -1;
    private final int shaderProgram;
    private final Object2IntOpenHashMap<String> uniformLocations;

    private Shader(final int shaderProgram, final Object2IntOpenHashMap<String> uniformLocations) {
        this.shaderProgram = shaderProgram;
        this.uniformLocations = new Object2IntOpenHashMap<>(uniformLocations);
        this.uniformLocations.defaultReturnValue(-1);

        GameManager.EVENT_BUS.addListener(RadGame.EventCleanup.class, ignored -> this.close());
    }

    public void setUniform(String name, int value) {
        int loc = getUniformLocation(name);
        if (loc < 0) return;
        glUniform1i(loc, value);
    }

    public void setUniform(String name, float value) {
        int loc = getUniformLocation(name);
        if (loc < 0) return;
        glUniform1f(loc, value);
    }

    public void setUniform(String name, Vector2fc value) {
        int loc = getUniformLocation(name);
        if (loc < 0) return;
        glUniform2f(loc, value.x(), value.y());
    }

    public void setUniform(String name, Vector3fc value) {
        int loc = getUniformLocation(name);
        if (loc < 0) return;
        glUniform3f(loc, value.x(), value.y(), value.z());
    }

    public void setUniform(String name, Vector4fc value) {
        int loc = getUniformLocation(name);
        if (loc < 0) return;
        glUniform4f(loc, value.x(), value.y(), value.z(), value.w());
    }

    public void setUniform(String name, Matrix3fc value) {
        int loc = getUniformLocation(name);
        if (loc < 0) return;
        try (MemoryStack stack = stackPush()) {
            FloatBuffer matrix = stack.mallocFloat(9);
            value.get(matrix);
            glUniformMatrix3fv(loc, false, matrix);
        }
    }

    public void setUniform(String name, Matrix4fc value) {
        int loc = getUniformLocation(name);
        if (loc < 0) return;
        try (MemoryStack stack = stackPush()) {
            FloatBuffer matrix = stack.mallocFloat(16);
            value.get(matrix);
            glUniformMatrix4fv(loc, false, matrix);
        }
    }

    public void bind() {
        if (currentProgram != shaderProgram) {
            glUseProgram(shaderProgram);
            currentProgram = shaderProgram;
        }
    }

    public boolean lacksUniform(String name) {
        return !uniformLocations.containsKey(name);
    }

    private int getUniformLocation(String name) {
        bind();
        int loc = uniformLocations.getInt(name);
        if (loc < 0) Log.error("Failed to locate uniform: \"{}\"", name);
        return loc;
    }

    @Override
    public void close() {
        if (currentProgram == shaderProgram) {
            currentProgram = -1;
            glUseProgram(0);
        }
        glDeleteProgram(shaderProgram);
    }

    public static ShaderBuilder builder() {
        return new ShaderBuilder();
    }

    @SuppressWarnings("WeakerAccess")
    public static final class ShaderBuilder {

        private String vertexShader;
        private String fragmentShader;
        private final ObjectArrayList<String> uniforms = new ObjectArrayList<>();

        private ShaderBuilder() {
        }

        public ShaderBuilder setVertexShader(String vertexShader) {
            this.vertexShader = vertexShader;
            return this;
        }

        public ShaderBuilder setVertexShader(ResourceLocation vertexShader) throws IOException {
            return setVertexShader(vertexShader.readResource());
        }

        public ShaderBuilder setFragmentShader(String fragmentShader) {
            this.fragmentShader = fragmentShader;
            return this;
        }

        public ShaderBuilder setFragmentShader(ResourceLocation fragmentShader) throws IOException {
            return setFragmentShader(fragmentShader.readResource());
        }

        public ShaderBuilder addUniforms(String... uniforms) {
            Collections.addAll(this.uniforms, uniforms);
            return this;
        }

        public Shader build() {
            if (vertexShader == null || fragmentShader == null) {
                throw new IllegalStateException("Shader requires a vertex and fragment shader");
            }
            final int shaderProgram = glCreateProgram();
            final int vertexShader = ((this.vertexShader != null) ? glCreateShader(GL_VERTEX_SHADER) : -1);
            final int fragmentShader = ((this.fragmentShader != null) ? glCreateShader(GL_FRAGMENT_SHADER) : -1);

            if (addShaderOrError(this.vertexShader, vertexShader, shaderProgram)) return null;
            if (addShaderOrError(this.fragmentShader, fragmentShader, shaderProgram)) return null;

            if (!link(shaderProgram)) return null;

            glDetachShader(shaderProgram, vertexShader);
            glDetachShader(shaderProgram, fragmentShader);
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);

            Object2IntOpenHashMap<String> uniformLocations = new Object2IntOpenHashMap<>();
            for (String uniform : uniforms) {
                int loc = glGetUniformLocation(shaderProgram, uniform);
                if (loc >= 0) uniformLocations.put(uniform, loc);
                else Log.error("Failed to locate uniform: \"{}\"", uniform);
            }

            return new Shader(shaderProgram, uniformLocations);
        }

        private static boolean addShaderOrError(String source, int shader, int program) {
            glShaderSource(shader, source);
            glCompileShader(shader);
            String error = glGetShaderInfoLog(shader).trim();
            if (!error.isEmpty()) {
                Log.error("Shader compile error: {}", error);
                return true;
            }
            glAttachShader(program, shader);
            return false;
        }

        private static boolean link(int program) {
            glValidateProgram(program);
            String error = glGetProgramInfoLog(program).trim();
            if (!error.isEmpty()) Log.error("Shader program validate error: {}", error);

            glLinkProgram(program);
            error = glGetProgramInfoLog(program).trim();
            if (!error.isEmpty()) {
                Log.error("Shader program link error: {}", error);
                return false;
            }
            return true;
        }

    }

}
