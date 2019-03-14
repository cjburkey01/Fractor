package com.cjburkey.radgame.voxel.world;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.gl.Mesh;
import com.cjburkey.radgame.util.registry.IRegistryItem;
import java.util.Objects;

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

    public void onAdd(final VoxelState voxelState) {
    }

    public void onRemove(final VoxelState voxelState) {
    }

    @Override
    public ResourceLocation getRegistryId() {
        return registryId;
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
