package com.cjburkey.radgame.gl;

import com.cjburkey.radgame.util.CollectionHelper;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;

public class Mesh {

    private static final int FLOATS_PER_VERTEX = 3;

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

    public Mesh setVertices(ByteBuffer data) {
        buffer("vbo", data, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 0, FLOATS_PER_VERTEX, GL_FLOAT);
        return this;
    }

    public Mesh setVertices(float[] data) {
        try (MemoryStack stack = stackPush()) {
            ByteBuffer buffer = stack.malloc(data.length * Float.BYTES);
            for (float dat : data) buffer.putFloat(dat);
            setVertices(buffer.flip());
        }
        return this;
    }

    public Mesh setIndices(ByteBuffer data) {
        triangles = data.limit() / Short.BYTES;
        buffer("ebo", data, GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW);
        return this;
    }

    public Mesh setIndices(short[] data) {
        try (MemoryStack stack = stackPush()) {
            ByteBuffer buffer = stack.malloc(data.length * Short.BYTES);
            for (short dat : data) buffer.putShort(dat);
            setIndices(buffer.flip());
        }
        return this;
    }

    public void buffer(String bufferName,
                       ByteBuffer data,
                       int type,
                       int usage,
                       int attribId,
                       int attribSize,
                       int attribType) {
        if (data == null) return;
        if (data.position() != 0) data.flip();
        if (data.position() != 0) {
            System.err.printf("Invalid buffer starting at position: %s\n", data.position());
            return;
        }
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
            glVertexAttribPointer(attribId, attribSize, attribType, false, 0, 0L);
        }
    }

    public void buffer(String bufferName,
                       ByteBuffer data,
                       int type,
                       int usage) {
        buffer(bufferName, data, type, usage, -1, -1, -1);
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

    public void clear() {
        for (int buffer : buffers.values()) {
            if (buffer != ebo) glDeleteBuffers(buffer);
        }
        buffers.clear();
        buffers.put("ebo", ebo);
        attribs.clear();
        triangles = 0;
    }

    public void destroy() {
        if (currentMesh == vao) {
            currentMesh = -1;
            glBindVertexArray(0);
        }

        clear();
        buffers.clear();
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
    }

    public void bind() {
        if (currentMesh != vao) {
            glBindVertexArray(vao);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            currentMesh = vao;
        }
    }

    public MeshBuilder start() {
        return new MeshBuilder(this);
    }

    public static final class MeshBuilder {

        private final ArrayList<Vector3fc> vertices = new ArrayList<>();
        private final ArrayList<Short> indices = new ArrayList<>();
        private final Mesh mesh;
        private short lastIndex = -1;
        private short maxIndex = -1;

        private MeshBuilder(final Mesh mesh) {
            this.mesh = mesh;
        }

        public MeshBuilder vert(final float x, final float y, final float z, final short index) {
            CollectionHelper.setItemInList(vertices, index, new Vector3f(x, y, z));
            index(index);
            return this;
        }

        public MeshBuilder vert(final float x, final float y, final float z) {
            return vert(x, y, z, (short) (maxIndex + 1));
        }

        public MeshBuilder vert(final Vector3fc vertex) {
            return vert(vertex.x(), vertex.y(), vertex.z());
        }

        public MeshBuilder vert(final Vector2fc vertex, final float z) {
            return vert(vertex.x(), vertex.y(), z);
        }

        public MeshBuilder vert(final float x, final float y) {
            return vert(x, y, 0.0f);
        }

        public MeshBuilder vert(final Vector2fc vertex) {
            return vert(vertex.x(), vertex.y());
        }

        public MeshBuilder vert(final Vector3fc vertex, final short index) {
            return vert(vertex.x(), vertex.y(), vertex.z(), index);
        }

        public MeshBuilder vert(final Vector2fc vertex, final float z, final short index) {
            return vert(vertex.x(), vertex.y(), z, index);
        }

        public MeshBuilder vert(final float x, final float y, final short index) {
            return vert(x, y, 0.0f, index);
        }

        public MeshBuilder vert(final Vector2fc vertex, final short index) {
            return vert(vertex.x(), vertex.y(), index);
        }

        public MeshBuilder verts(final short... indices) {
            final var start = this.indices.size();
            for (int i = 0; i < indices.length; i++) {
                index(i + start, indices[i]);
            }
            return this;
        }

        public MeshBuilder verts(final int... indices) {
            final var shorts = new short[indices.length];
            for (int i = 0; i < indices.length; i++) shorts[i] = (short) indices[i];
            return verts(shorts);
        }

        private void index(final int at, final short value) {
            if (value > maxIndex) maxIndex = value;
            lastIndex = value;
            CollectionHelper.setItemInList(this.indices, at, value);
        }

        private void index(final short value) {
            index(indices.size(), value);
        }

        public short lastIndex() {
            return lastIndex;
        }

        public short maxIndex() {
            return maxIndex;
        }

        public Mesh end(final boolean clearCurrentMesh) {
            try (MemoryStack stack = stackPush()) {
                final var vertices = stack.malloc(this.vertices.size() * Float.BYTES * FLOATS_PER_VERTEX);
                final var indices = stack.malloc(this.indices.size() * Short.BYTES);

                for (int i = 0; i < this.vertices.size(); i++) {
                    final var vert = this.vertices.get(i);
                    if (vert == null) {
                        vertices.putFloat(i * Float.BYTES * FLOATS_PER_VERTEX, 0.0f);
                        vertices.putFloat(i * Float.BYTES * FLOATS_PER_VERTEX + Float.BYTES, 0.0f);
                        vertices.putFloat(i * Float.BYTES * FLOATS_PER_VERTEX + (2 * Float.BYTES), 0.0f);
                    } else {
                        vert.get(i * Float.BYTES * FLOATS_PER_VERTEX, vertices);
                    }
                }
                for (int i = 0; i < this.indices.size(); i++) {
                    final var index = this.indices.get(i);
                    if (index == null) {
                        indices.putShort(i * Short.BYTES, (short) 0);
                    } else {
                        indices.putShort(i * Short.BYTES, index);
                    }
                }

                if (clearCurrentMesh) mesh.clear();
                mesh.setVertices(vertices);
                mesh.setIndices(indices);
            }

            vertices.clear();
            indices.clear();
            return mesh;
        }

        public Mesh end() {
            return end(true);
        }

    }

}
