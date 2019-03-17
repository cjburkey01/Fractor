package com.cjburkey.radgame.voxel;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.mesh.Mesh;
import com.cjburkey.radgame.world.VoxelState;
import org.joml.AABBf;
import org.joml.Rectanglef;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Created by CJ Burkey on 2019/03/06
 */
public class TexturedSquareVoxel extends SingleTexturedVoxel {

    private static final Vector2ic SIZE = new Vector2i(1);

    private TexturedSquareVoxel(final ResourceLocation id, final ResourceLocation textureId) {
        super(id, textureId);
    }

    @SuppressWarnings("WeakerAccess")
    public TexturedSquareVoxel(final String id, final String textureId) {
        this(ResourceLocation.fromString(id, false), ResourceLocation.fromString(textureId, true));
    }

    @Override
    public void generateMesh(final Mesh.MeshBuilder mesh, final VoxelState voxelState) {
        addUVSquareToMesh(mesh, voxelState.posInChunk(), voxelState.z(), voxelState.world().voxelTextureAtlas().getUv(getPrimaryTextureId()));
    }

    @Override
    public AABBf[] getBoundingBoxes(VoxelState voxelState) {
        return getSquareBoundingBox(voxelState.posInWorld(), SIZE);
    }

    static void addUVSquareToMesh(final Mesh.MeshBuilder mesh, final Vector2ic chunkPos, final float z, final Rectanglef uv) {
        final var startI = (mesh.lastIndex() + 1);
        mesh.startSubMesh()
                // 0
                .vert(chunkPos.x(), chunkPos.y() + 1.0f, z).uv(uv.minX, uv.minY)
                // 1
                .vert(chunkPos.x(), chunkPos.y(), z).uv(uv.minX, uv.maxY)
                // 2
                .vert(chunkPos.x() + 1.0f, chunkPos.y(), z).uv(uv.maxX, uv.maxY)
                // 0, 2
                .verts(startI, startI + 2)
                // 3
                .vert(chunkPos.x() + 1.0f, chunkPos.y() + 1.0f, z).uv(uv.maxX, uv.minY)

                .endSubMesh();
    }

}
