package com.cjburkey.radgame.chunk;

import com.cjburkey.radgame.component.render.MaterialRenderer;
import com.cjburkey.radgame.component.render.MeshRenderer;
import com.cjburkey.radgame.ecs.GameObject;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.shader.Shader;
import com.cjburkey.radgame.shader.material.TexturedTransform;
import com.cjburkey.radgame.texture.TextureAtlas;
import com.cjburkey.radgame.world.Voxel;
import com.cjburkey.radgame.world.VoxelState;
import com.cjburkey.radgame.world.VoxelWorld;
import java.util.Objects;
import java.util.Optional;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Created by CJ Burkey on 2019/03/08
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class VoxelChunk {

    public static final int CHUNK_SIZE = 32;
    static final int CHUNK_THICKNESS = 3;

    private final Vector2ic chunkPos;
    private final Vector2ic posInWorld;
    public final VoxelWorld world;
    private final Scene scene;
    final MeshRenderer meshRenderer;
    final GameObject gameObject;

    private final VoxelState[] voxels = new VoxelState[CHUNK_SIZE * CHUNK_SIZE * CHUNK_THICKNESS];

    public VoxelChunk(final Scene scene,
                      final Vector2ic chunkPos,
                      final VoxelWorld world,
                      final Shader shader,
                      final TextureAtlas atlasMaterial) {
        this.chunkPos = new Vector2i(Objects.requireNonNull(chunkPos));
        this.posInWorld = chunkPos.mul(CHUNK_SIZE, new Vector2i());
        this.world = Objects.requireNonNull(world);
        this.scene = Objects.requireNonNull(scene);

        final var materialRenderer = new MaterialRenderer();
        final var mat = new TexturedTransform(shader);
        mat.texture = atlasMaterial.getTexture();
        materialRenderer.material = mat;

        meshRenderer = new MeshRenderer();
        gameObject = scene.createObjectWith(materialRenderer, meshRenderer);
    }

    public void onUnload() {
        scene.destroy(gameObject);
    }

    public void setVoxel(final Vector2ic posInChunk, final int i, final Voxel voxel, boolean update) {
        final var x = posInChunk.x();
        final var y = posInChunk.y();

        if (isInvalid(x, y, i)) return;

        final var index = index(x, y, i);
        if (update) {
            final var oldState = voxels[index];
            if (oldState != null) oldState.getVoxel().onRemove(oldState);
        }

        final var newState = ((voxel == null) ? null : (new VoxelState(voxel, this, world, posInChunk, i)));
        voxels[index] = newState;
        if (newState != null) voxel.onAdd(newState);
    }

    public void setVoxel(final Vector2ic posInChunk, final int i, final Voxel voxel) {
        setVoxel(posInChunk, i, voxel, true);
    }

    public void setVoxel(final int x, final int y, final int i, final Voxel voxel, boolean update) {
        setVoxel(new Vector2i(x, y), i, voxel, update);
    }

    public void setVoxel(final int x, final int y, final int i, final Voxel voxel) {
        setVoxel(x, y, i, voxel, true);
    }

    public VoxelState getVoxelState(final int x, final int y, final int i) {
        if (isInvalid(x, y, i)) return null;
        return voxels[index(x, y, i)];
    }

    public VoxelState getVoxelState(final Vector2ic posInChunk, final int i) {
        return getVoxelState(posInChunk.x(), posInChunk.y(), i);
    }

    public Optional<VoxelState> getStateSafe(final int x, final int y, final int i) {
        if (isInvalid(x, y, i)) return Optional.empty();

        final var at = voxels[index(x, y, i)];
        if (at == null) return Optional.of(VoxelState.AIR);

        return Optional.of(at);
    }

    public Optional<VoxelState> getStateSafe(final Vector2ic posInChunk, final int i) {
        return getStateSafe(posInChunk.x(), posInChunk.y(), i);
    }

    public Vector2ic getChunkPos() {
        return chunkPos;
    }

    public Vector2ic getPosInWorld() {
        return posInWorld;
    }

    private static boolean isInvalid(final int x, final int y, final int i) {
        return (x < 0 || y < 0 || i < 0 || x >= CHUNK_SIZE || y >= CHUNK_SIZE || i >= CHUNK_THICKNESS);
    }

    private static int index(final int x, final int y, final int i) {
        return i * CHUNK_SIZE * CHUNK_SIZE + y * CHUNK_SIZE + x;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoxelChunk that = (VoxelChunk) o;
        return chunkPos.equals(that.chunkPos) &&
                posInWorld.equals(that.posInWorld);
    }

    public int hashCode() {
        return Objects.hash(chunkPos, posInWorld);
    }

}
