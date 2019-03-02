package com.cjburkey.radgame.gl.shader;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.util.IO;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.*;

public class RawShader implements AutoCloseable {

    private static int currentProgram = -1;
    private final int shaderProgram;
    private final HashMap<String, Integer> uniformLocations;

    private RawShader(final int shaderProgram, final HashMap<String, Integer> uniformLocations) {
        this.shaderProgram = shaderProgram;
        this.uniformLocations = uniformLocations;
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

    private int getUniformLocation(String name) {
        int loc = uniformLocations.getOrDefault(name, -1);
        if (loc < 0) System.err.println("Failed to locate uniform: \"" + name + "\"");
        return loc;
    }

    public void bind() {
        if (currentProgram != shaderProgram) {
            glUseProgram(shaderProgram);
            currentProgram = shaderProgram;
        }
    }

    @Override
    public void close() {
        glDeleteProgram(shaderProgram);
    }

    public boolean hasUniform(String name) {
        return uniformLocations.containsKey(name);
    }

    public static ShaderBuilder builder() {
        return new ShaderBuilder();
    }

    public static final class ShaderBuilder {

        private String vertexShader;
        private String fragmentShader;
        private final List<String> uniforms = new ArrayList<>();

        private ShaderBuilder() {
        }

        public String getVertexShader() {
            return vertexShader;
        }

        public ShaderBuilder setVertexShader(String vertexShader) {
            this.vertexShader = vertexShader;
            return this;
        }

        public ShaderBuilder setVertexShader(ResourceLocation vertexShader) {
            return setVertexShader(IO.readResource(vertexShader));
        }

        public String getFragmentShader() {
            return fragmentShader;
        }

        public ShaderBuilder setFragmentShader(String fragmentShader) {
            this.fragmentShader = fragmentShader;
            return this;
        }

        public ShaderBuilder setFragmentShader(ResourceLocation fragmentShader) {
            return setFragmentShader(IO.readResource(fragmentShader));
        }

        public String[] getUniforms() {
            return uniforms.toArray(new String[0]);
        }

        public ShaderBuilder addUniforms(String... uniforms) {
            this.uniforms.addAll(Arrays.asList(uniforms));
            return this;
        }

        public RawShader build() {
            if (vertexShader == null || fragmentShader == null) {
                throw new IllegalStateException("Shader requires a vertex and fragment shader");
            }
            final int shaderProgram = glCreateProgram();
            final int vertexShader = ((this.vertexShader != null) ? glCreateShader(GL_VERTEX_SHADER) : -1);
            final int fragmentShader = ((this.fragmentShader != null) ? glCreateShader(GL_FRAGMENT_SHADER) : -1);

            if (addShaderErrors(this.vertexShader, vertexShader, shaderProgram)) return null;
            if (addShaderErrors(this.fragmentShader, fragmentShader, shaderProgram)) return null;

            if (!link(shaderProgram)) return null;

            glDetachShader(shaderProgram, vertexShader);
            glDetachShader(shaderProgram, fragmentShader);
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);

            HashMap<String, Integer> uniformLocations = new HashMap<>();
            for (String uniform : uniforms) {
                int loc = glGetUniformLocation(shaderProgram, uniform);
                if (loc >= 0) uniformLocations.put(uniform, loc);
                else System.err.printf("Failed to locate uniform: \"%s\"\n", uniform);
            }

            return new RawShader(shaderProgram, uniformLocations);
        }

        private static boolean addShaderErrors(String source, int shader, int program) {
            glShaderSource(shader, source);
            glCompileShader(shader);
            String error = glGetShaderInfoLog(shader).trim();
            if (!error.isEmpty()) {
                System.err.println("Shader compile error: " + error);
                return true;
            }
            glAttachShader(program, shader);
            return false;
        }

        private static boolean link(int program) {
            glValidateProgram(program);
            String error = glGetProgramInfoLog(program).trim();
            if (!error.isEmpty()) System.err.println("Shader program validate error: " + error);

            glLinkProgram(program);
            error = glGetProgramInfoLog(program).trim();
            if (!error.isEmpty()) {
                System.err.println("Shader program link error: " + error);
                return false;
            }
            return true;
        }

    }

}
