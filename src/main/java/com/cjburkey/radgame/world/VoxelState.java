package com.cjburkey.radgame.world;

import com.cjburkey.radgame.chunk.VoxelChunk;
import com.cjburkey.radgame.util.math.Interpolate;
import com.cjburkey.radgame.voxel.Voxel;
import com.cjburkey.radgame.voxel.Voxels;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Objects;
import org.dyn4j.geometry.Vector2;
import org.joml.AABBf;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Created by CJ Burkey on 2019/03/06
 */
@SuppressWarnings("unused")
public final class VoxelState {

    public static final VoxelState AIR = new VoxelState();

    private static final AABBf[] EMPTY_COLLIDER = new AABBf[0];

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
        isAir = voxel.equals(Voxels.AIR);
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

    public float z() {
        return Interpolate.map(i, 0, VoxelChunk.CHUNK_THICKNESS, -1.0f, 0.0f);
    }

    public boolean isAir() {
        return isAir;
    }

    public void onAdd() {
        if (isAir) return;
        voxel.onAdd(this);
    }

    public void onRemove() {
        if (isAir) return;
        voxel.onRemove(this);
    }

    @SuppressWarnings("WeakerAccess")
    public AABBf[] getBoundingBoxes() {
        if (isAir) return EMPTY_COLLIDER;
        return voxel.getBoundingBoxes(this);
    }

    public final Vector2[] getVertices() {
        final var out = new ObjectOpenHashSet<Vector2>();
        try {
            for (AABBf boundingBox : getBoundingBoxes()) {
                out.add(new Vector2(boundingBox.minX, boundingBox.minY));
                out.add(new Vector2(boundingBox.maxX, boundingBox.minY));
                out.add(new Vector2(boundingBox.maxX, boundingBox.maxY));
                out.add(new Vector2(boundingBox.minX, boundingBox.maxY));
            }
            return out.toArray(Vector2[]::new);
        } finally {
            out.clear();
        }
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
