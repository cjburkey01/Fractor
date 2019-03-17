package com.cjburkey.radgame.world;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.mesh.Mesh;
import com.cjburkey.radgame.util.math.Bounding;
import com.cjburkey.radgame.util.registry.IRegistryItem;
import java.util.Objects;
import org.joml.AABBf;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Created by CJ Burkey on 2019/03/06
 */
public abstract class Voxel implements IRegistryItem {

    private final ResourceLocation registryId;

    public Voxel(final ResourceLocation id) {
        this.registryId = Objects.requireNonNull(id);
    }

    // A submesh state is created in the mesh builder, so index 0 is the start of this mesh's vertices
    public abstract void generateMesh(final Mesh.MeshBuilder mesh, final VoxelState voxelState);

    @SuppressWarnings("WeakerAccess")
    protected void onAdd(final VoxelState voxelState) {
    }

    @SuppressWarnings("WeakerAccess")
    protected void onRemove(final VoxelState voxelState) {
    }

    public abstract AABBf[] getBoundingBoxes(final VoxelState voxelState);

    @Override
    public ResourceLocation getRegistryId() {
        return registryId;
    }

    protected final AABBf[] getSquareBoundingBox(final int x, final int y, final int width, final int height) {
        return new AABBf[] {
                Bounding.fromSize(new Vector2i(x, y), new Vector2i(width, height)),
        };
    }

    protected final AABBf[] getSquareBoundingBox(final Vector2ic pos, final Vector2ic size) {
        return new AABBf[] {
                Bounding.fromSize(pos, size),
        };
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voxel voxel = (Voxel) o;
        return registryId.equals(voxel.registryId);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(registryId);
    }

}
