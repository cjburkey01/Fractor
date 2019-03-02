package com.cjburkey.radgame.gl;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.HashSet;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;

public class Mesh {

    private static int currentMesh = -1;
    private final int vao;
    private final int ebo;
    private int triangles;
    private final HashMap<String, Integer> buffers = new HashMap<>();
    private final HashSet<Integer> attribs = new HashSet<>();

    public Mesh() {
        vao = glGenVertexArrays();
        ebo = glGenBuffers();
        buffers.put("ebo", ebo);
    }

    public Mesh setVertices(FloatBuffer data) {
        buffer("vbo", data, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 0, 3);
        return this;
    }

    public Mesh setVertices(float[] data) {
        try (MemoryStack stack = stackPush()) {
            setVertices(stack.mallocFloat(data.length).put(data).flip());
        }
        return this;
    }

    public Mesh setIndices(ShortBuffer data) {
        triangles = data.limit();
        buffer("ebo", data, GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW);
        return this;
    }

    public Mesh setIndices(short[] data) {
        try (MemoryStack stack = stackPush()) {
            setIndices(stack.mallocShort(data.length).put(data).flip());
        }
        return this;
    }

    public void buffer(String bufferName,
                       FloatBuffer data,
                       int type,
                       int usage,
                       int attribId,
                       int attribSize) {
        bind();
        int buffer = buffers.getOrDefault(bufferName, -1);
        if (buffer < 0) {
            buffer = glGenBuffers();
            buffers.put(bufferName, buffer);
        }
        bindBuffer(bufferName, type);
        glBufferData(type, data, usage);
        if (attribId >= 0 && attribSize > 0) {
            attribs.add(attribId);
            glVertexAttribPointer(attribId, attribSize, GL_FLOAT, false, 0, 0L);
        }
    }

    public void buffer(String bufferName,
                       ShortBuffer data,
                       int type,
                       int usage,
                       int attribId,
                       int attribSize) {
        bind();
        int buffer = buffers.getOrDefault(bufferName, -1);
        if (buffer < 0) {
            buffer = glGenBuffers();
            buffers.put(bufferName, buffer);
        }
        bindBuffer(bufferName, type);
        glBufferData(type, data, usage);
        if (attribId >= 0 && attribSize > 0) {
            attribs.add(attribId);
            glVertexAttribPointer(attribId, attribSize, GL_UNSIGNED_SHORT, false, 0, 0L);
        }
    }

    public void buffer(String bufferName,
                       FloatBuffer data,
                       int type,
                       int usage) {
        buffer(bufferName, data, type, usage, -1, -1);
    }

    public void buffer(String bufferName,
                       ShortBuffer data,
                       int type,
                       int usage) {
        buffer(bufferName, data, type, usage, -1, -1);
    }

    private void bindBuffer(String bufferName, int bufferLocation) {
        int buffer = buffers.getOrDefault(bufferName, -1);
        if (buffer >= 0) glBindBuffer(bufferLocation, buffer);
    }

    public void render() {
        bind();
        attribs.forEach(GL20::glEnableVertexAttribArray);
        glDrawElements(GL_TRIANGLES, triangles, GL_UNSIGNED_SHORT, 0L);
        attribs.forEach(GL20::glDisableVertexAttribArray);
    }

    public void destroy() {
        for (int buffer : buffers.values()) glDeleteBuffers(buffer);
        glDeleteVertexArrays(vao);
    }

    public void bind() {
        if (currentMesh != vao) {
            glBindVertexArray(vao);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            currentMesh = vao;
        }
    }

}
