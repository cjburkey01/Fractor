package com.cjburkey.radgame.mesh;

import com.cjburkey.radgame.RadGame;
import com.cjburkey.radgame.game.GameManager;
import com.cjburkey.radgame.util.collection.CollectionHelper;
import com.cjburkey.radgame.util.io.Log;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.IntConsumer;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public class Mesh implements Closeable {

    private static final int FLOATS_PER_VERTEX = 3;

    private static int currentMesh = -1;
    private final int vao;
    private final int ebo;
    private int triangles;
    private final Object2IntOpenHashMap<String> buffers = new Object2IntOpenHashMap<>();
    private final IntOpenHashSet attribs = new IntOpenHashSet();
    private boolean destroyed = false;

    public Mesh() {
        vao = glGenVertexArrays();
        ebo = glGenBuffers();
        buffers.put("ebo", ebo);

        GameManager.EVENT_BUS.addListener(RadGame.EventCleanup.class, ignored -> this.close());
    }

    public Mesh setVertices(ByteBuffer data) {
        if (destroyed) return this;
        bind();
        buffer("vbo", data, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 0, FLOATS_PER_VERTEX, GL_FLOAT);
        return this;
    }

    public Mesh setVertices(float[] data) {
        if (destroyed) return this;
        bind();
        try (MemoryStack stack = stackPush()) {
            ByteBuffer buffer = stack.malloc(data.length * Float.BYTES);
            for (float dat : data) buffer.putFloat(dat);
            setVertices(buffer.flip());
        }
        return this;
    }

    public Mesh setIndices(ByteBuffer data) {
        if (destroyed) return this;
        bind();
        triangles = data.limit() / Short.BYTES;
        buffer("ebo", data, GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW);
        return this;
    }

    public Mesh setIndices(short[] data) {
        if (destroyed) return this;
        bind();
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
            Log.error("Invalid buffer starting at position: {}", data.position());
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
        if (destroyed) return;
        bind();
        attribs.forEach((IntConsumer) GL20::glEnableVertexAttribArray);
        glDrawElements(GL_TRIANGLES, triangles, GL_UNSIGNED_SHORT, 0L);
        attribs.forEach((IntConsumer) GL20::glDisableVertexAttribArray);
    }

    @Override
    public void close() {
        if (destroyed) return;

        if (currentMesh == vao) {
            currentMesh = -1;
            glBindVertexArray(0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        buffers.values().forEach((IntConsumer) GL20::glDeleteBuffers);
        buffers.clear();
        attribs.clear();
        triangles = 0;
        glDeleteVertexArrays(vao);
        destroyed = true;
    }

    public void bind() {
        if (destroyed) return;
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

        private final ObjectArrayList<Vector3fc> vertices = new ObjectArrayList<>();
        private final ShortArrayList indices = new ShortArrayList();
        private final ObjectArrayList<Vector2fc> uvs = new ObjectArrayList<>();
        private final ShortArrayList subStack = new ShortArrayList();
        private final Mesh mesh;
        private short lastIndex = -1;
        private short maxIndex = -1;
        private short subStart = 0;

        private MeshBuilder(final Mesh mesh) {
            this.mesh = mesh;
        }

        public MeshBuilder vert(final float x, final float y, final float z, final short index) {
            CollectionHelper.setItemInList(vertices, index, new Vector3f(x, y, z));
            index((short) (index - subStart));
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

        public MeshBuilder uv(final float x, final float y, final short index) {
            CollectionHelper.setItemInList(uvs, index, new Vector2f(x, y));
            return this;
        }

        public MeshBuilder uv(final float x, final float y) {
            return uv(x, y, lastIndex);
        }

        public MeshBuilder uv(final Vector2fc uv, final short index) {
            return uv(uv.x(), uv.y(), index);
        }

        public MeshBuilder uv(final Vector2fc uv) {
            return uv(uv.x(), uv.y());
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
            final var trueValue = (short) (value + subStart);
            if (trueValue > maxIndex) maxIndex = trueValue;
            lastIndex = trueValue;
            CollectionHelper.setItemInList(this.indices, at, trueValue);
        }

        private void index(final short value) {
            index(indices.size(), value);
        }

        public short lastIndex() {
            return (short) (lastIndex - subStart);
        }

        public short maxIndex() {
            return maxIndex;
        }

        private void reset() {
            vertices.clear();
            indices.clear();
            uvs.clear();
            lastIndex = -1;
            maxIndex = -1;
            subStart = 0;
        }

        // Begins a "submesh" state in which index "0" refers to the index following the previous maximum index.
        // This now CAN be embedded!
        public MeshBuilder startSubMesh() {
            subStack.push(subStart);
            subStart = (short) (maxIndex + 1);
            return this;
        }

        public MeshBuilder endSubMesh() {
            if (!subStack.isEmpty()) subStart = subStack.popShort();
            else subStart = 0;
            return this;
        }

        public Mesh end() {
            final var vertices = memAlloc(this.vertices.size() * Float.BYTES * FLOATS_PER_VERTEX);
            final var indices = memAlloc(this.indices.size() * Short.BYTES);
            final var uvs = ((this.uvs.size() > 0) ? (memAlloc(this.uvs.size() * Float.BYTES * 2)) : null);
            try {
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
                    final var index = this.indices.getShort(i);
                    indices.putShort(i * Short.BYTES, Objects.requireNonNullElseGet(index, () -> (short) 0));
                }
                if (uvs != null) {
                    for (int i = 0; i < this.uvs.size(); i++) {
                        final var uv = this.uvs.get(i);
                        if (uv == null) {
                            uvs.putFloat(i * Float.BYTES * 2, 0.0f);
                            uvs.putFloat(i * Float.BYTES * 2 + Float.BYTES, 0.0f);
                        } else {
                            uv.get(i * Float.BYTES * 2, uvs);
                        }
                    }
                }

                mesh.setVertices(vertices);
                mesh.setIndices(indices);
                if (uvs != null) {
                    mesh.buffer("uvbo", uvs, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 1, 2, GL_FLOAT);
                }
            } finally {
                memFree(vertices);
                memFree(indices);
                memFree(uvs);
            }
            reset();
            return mesh;
        }

    }

}
