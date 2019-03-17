package com.cjburkey.radgame.voxel;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.mesh.Mesh;
import com.cjburkey.radgame.world.VoxelState;

import static com.cjburkey.radgame.voxel.TexturedSquareVoxel.*;

/**
 * Created by CJ Burkey on 2019/03/16
 */
@SuppressWarnings("WeakerAccess")
public class VoxelGrass extends Voxel implements ITexturedVoxel {

    private final ResourceLocation[] textures = new ResourceLocation[0b11];

    public VoxelGrass() {
        super(ResourceLocation.fromString("radgame:voxel/grass", false));

        for (int i = 0; i < 0b11; i++) {
            textures[i] = ResourceLocation.fromString(
                    String.format("radgame:texture/voxel/grass/grass_%2s.png", Integer.toBinaryString(i)).replace(' ', '0'),
                    true);
        }
    }

    public void generateMesh(Mesh.MeshBuilder mesh, VoxelState voxelState) {
        final var x = voxelState.posInChunk().x();
        final var y = voxelState.posInChunk().y();

        final var rd = voxelState.chunk().getVoxelState(x + 1, y - 1, voxelState.depth());
        final var ld = voxelState.chunk().getVoxelState(x - 1, y - 1, voxelState.depth());

        var bits = 0x00;
        if (ld != null && ld.getVoxel().equals(this)) bits |= 0b10;
        if (rd != null && rd.getVoxel().equals(this)) bits |= 0b01;

        addUVSquareToMesh(mesh, voxelState.posInChunk(), voxelState.depth(), voxelState.world().voxelTextureAtlas().getUv(textures[bits]));
    }

    public ResourceLocation[] getTextureIds() {
        return textures;
    }

    public ResourceLocation getPrimaryTextureId() {
        return textures[0b00];
    }

}
