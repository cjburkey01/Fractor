package com.cjburkey.radgame.world;

import com.cjburkey.radgame.chunk.VoxelChunk;
import com.cjburkey.radgame.voxel.Voxel;
import java.util.Objects;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Created by CJ Burkey on 2019/03/06
 */
@SuppressWarnings("unused")
public final class VoxelState {

    public static final VoxelState AIR = new VoxelState();

    private final Voxel voxel;
    private final VoxelChunk chunk;
    private final VoxelWorld world;
    private final Vector2ic posInChunk;
    private final Vector2ic posInWorld;
    private final int i;
    private final boolean isAir;

    private VoxelState() {
        voxel = null;
        chunk = null;
        world = null;
        posInChunk = null;
        posInWorld = null;
        i = -1;
        isAir = true;
    }

    public VoxelState(final Voxel voxel, final VoxelChunk chunk, final VoxelWorld world, final Vector2ic posInChunk, final int i) {
        this.voxel = Objects.requireNonNull(voxel);
        this.chunk = Objects.requireNonNull(chunk);
        this.world = Objects.requireNonNull(world);
        this.posInChunk = new Vector2i(Objects.requireNonNull(posInChunk));
        posInWorld = chunk.worldPos().add(posInChunk, new Vector2i());
        this.i = i;
        isAir = false;
    }

    public Voxel getVoxel() {
        return voxel;
    }

    public VoxelChunk chunk() {
        return chunk;
    }

    public VoxelWorld world() {
        return world;
    }

    public Vector2ic posInChunk() {
        return posInChunk;
    }

    public Vector2ic posInWorld() {
        return posInWorld;
    }

    public int depth() {
        return i;
    }

    public boolean isAir() {
        return isAir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoxelState that = (VoxelState) o;
        return isAir == that.isAir &&
                Objects.equals(voxel, that.voxel) &&
                Objects.equals(chunk, that.chunk) &&
                Objects.equals(posInChunk, that.posInChunk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voxel, chunk, posInChunk, isAir);
    }

}
