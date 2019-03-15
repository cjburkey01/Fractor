package com.cjburkey.radgame.voxel;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.gl.Mesh;
import com.cjburkey.radgame.voxel.world.VoxelState;

/**
 * Created by CJ Burkey on 2019/03/06
 */
public class TexturedSquareVoxel extends SingleTexturedVoxel {

    private TexturedSquareVoxel(final ResourceLocation id, final ResourceLocation textureId) {
        super(id, textureId);
    }

    @SuppressWarnings("WeakerAccess")
    public TexturedSquareVoxel(final String id, final String textureId) {
        this(ResourceLocation.fromString(id, false), ResourceLocation.fromString(textureId, true));
    }

    @Override
    public void generateMesh(final Mesh.MeshBuilder mesh, final VoxelState voxelState) {
        final var cx = voxelState.getPosInChunk().x();
        final var cy = voxelState.getPosInChunk().y();
        final var uv = voxelState.getWorld().getVoxelTextureAtlas().getUv(getPrimaryTextureId());

        // TODO: Z
//        final var z = Interpolate.map(voxelState.getDepth(), );

        mesh
                // 0
                .vert(cx, cy + 1.0f).uv(uv.minX, uv.minY)
                // 1
                .vert(cx, cy).uv(uv.minX, uv.maxY)
                // 2
                .vert(cx + 1.0f, cy).uv(uv.maxX, uv.maxY)
                // 0, 2
                .verts(0, 2)
                // 3
                .vert(cx + 1.0f, cy + 1.0f).uv(uv.maxX, uv.minY);
    }

}
