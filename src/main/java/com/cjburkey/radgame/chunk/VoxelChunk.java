package com.cjburkey.radgame.chunk;

import com.cjburkey.radgame.component.render.MaterialRenderer;
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
    public static final int CHUNK_THICKNESS = 3;

    private final Vector2ic chunkPos;
    private final Vector2ic posInWorld;
    public final VoxelWorld world;
    private boolean generated;

    private final VoxelState[] voxels = new VoxelState[CHUNK_SIZE * CHUNK_SIZE * CHUNK_THICKNESS];

    public VoxelChunk(final Vector2ic chunkPos,
                      final VoxelWorld world) {
        this.chunkPos = new Vector2i(Objects.requireNonNull(chunkPos));
        this.posInWorld = chunkPos.mul(CHUNK_SIZE, new Vector2i());
        this.world = Objects.requireNonNull(world);

        final var materialRenderer = new MaterialRenderer();
    }

    public void updateAllNeighborChunks() {
        for (var x = -1; x <= 1; x++) {
            for (var y = -1; y <= 1; y++) {
                if (x != 0 || y != 0) updateRelativeNeighbor(x, y);
            }
        }
    }

    public void updateAdjacentNeighborChunks() {
        for (var x = -1; x <= 1; x++) {
            for (var y = -1; y <= 1; y++) {
                if (x != y) updateRelativeNeighbor(x, y);
            }
        }
    }

    public void updateRelativeNeighbor(int x, int y) {
        if (x != 0 || y != 0) world.ifPresent(chunkPos.x() + x, chunkPos.y() + y, chunk -> chunk.onChunkUpdate(this));
    }

    public void onChunkUpdate(VoxelChunk updater) {
        world.eventHandler.invokeSafe(new EventChunkUpdate(updater, this));
    }

    public void setVoxel(final Vector2ic posInChunk, final int i, final Voxel voxel, boolean update) {
        final var x = posInChunk.x();
        final var y = posInChunk.y();

        if (isInvalid(x, y, i)) return;

        final var index = index(x, y, i);
        if (update) {
            final var oldState = voxels[index];
            if (oldState != null) oldState.onRemove();
        }

        final var newState = ((voxel == null) ? null : (new VoxelState(voxel, this, world, posInChunk, i)));
        voxels[index] = newState;
        if (newState != null) newState.onAdd();

        if (update) {
            onChunkUpdate(this);

            if (posInChunk.x() == 0) {
                updateRelativeNeighbor(-1, 0);
            }
            if (posInChunk.x() == (CHUNK_SIZE - 1)) {
                updateRelativeNeighbor(1, 0);
            }
            if (posInChunk.y() == 0) {
                updateRelativeNeighbor(0, -1);
            }
            if (posInChunk.y() == (CHUNK_SIZE - 1)) {
                updateRelativeNeighbor(0, 1);
            }
        }
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
        return new Vector2i(chunkPos);
    }

    public Vector2ic worldPos() {
        return new Vector2i(posInWorld);
    }

    public boolean isGenerated() {
        return generated;
    }

    public void markGenerated() {
        if (!generated) {
            generated = true;
            world.eventHandler.invokeSafe(new VoxelChunk.EventChunkUpdate(this));
            updateAllNeighborChunks();
        }
    }

    private static boolean isInvalid(final int x, final int y, final int i) {
        return (x < 0 || y < 0 || i < 0 || x >= CHUNK_SIZE || y >= CHUNK_SIZE || i >= CHUNK_THICKNESS);
    }

    private static int index(final int x, final int y, final int i) {
        return i * CHUNK_SIZE * CHUNK_SIZE + y * CHUNK_SIZE + x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoxelChunk that = (VoxelChunk) o;
        return chunkPos.equals(that.chunkPos) &&
                posInWorld.equals(that.posInWorld);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunkPos, posInWorld);
    }

    public static class EventChunkUpdate extends VoxelWorld.ChunkEvent {

        public final VoxelChunk updater;

        private EventChunkUpdate(VoxelChunk updater, VoxelChunk chunk) {
            super(chunk);

            this.updater = Objects.requireNonNull(updater);
        }

        private EventChunkUpdate(VoxelChunk chunk) {
            this(chunk, chunk);
        }

    }

}
