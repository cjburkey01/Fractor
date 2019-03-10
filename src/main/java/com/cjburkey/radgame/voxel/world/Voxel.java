package com.cjburkey.radgame.voxel.world;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.gl.Mesh;
import java.util.Objects;

/**
 * Created by CJ Burkey on 2019/03/06
 */
public abstract class Voxel {

    private final ResourceLocation id;

    public Voxel(final ResourceLocation id) {
        this.id = Objects.requireNonNull(id);
    }

    // A submesh state is created in the mesh builder, so it's necessary to use index 0 as the start of this mesh's vertices
    public abstract void generateMesh(final Mesh.MeshBuilder mesh, final VoxelState voxelState);

    public void onAdd(final VoxelState voxelState) {
    }

    public void onRemove(final VoxelState voxelState) {
    }

    public ResourceLocation getId() {
        return id;
    }

    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voxel voxel = (Voxel) o;
        return id.equals(voxel.id);
    }

    public final int hashCode() {
        return Objects.hash(id);
    }

}
