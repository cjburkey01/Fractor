package com.cjburkey.radgame.voxel;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.mesh.Mesh;
import com.cjburkey.radgame.world.Voxel;
import com.cjburkey.radgame.world.VoxelState;
import org.joml.AABBf;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import static com.cjburkey.radgame.voxel.TexturedSquareVoxel.*;

/**
 * Created by CJ Burkey on 2019/03/16
 */
@SuppressWarnings("WeakerAccess")
public class VoxelGrass extends Voxel implements ITexturedVoxel {

    private static final Vector2ic SIZE = new Vector2i(1);

    private final ResourceLocation[] textures = new ResourceLocation[0b100];

    public VoxelGrass() {
        super(ResourceLocation.fromString("radgame:voxel/grass", false));

        for (int i = 0; i < 0b100; i++) {
            textures[i] = ResourceLocation.fromString(
                    String.format("radgame:texture/voxel/grass/grass_%2s.png", Integer.toBinaryString(i)).replace(' ', '0'),
                    true);
        }
    }

    @Override
    public AABBf[] getBoundingBoxes(VoxelState voxelState) {
        return getSquareBoundingBox(voxelState.posInWorld(), SIZE);
    }

    public void generateMesh(Mesh.MeshBuilder mesh, VoxelState voxelState) {
        final var x = voxelState.posInWorld().x();
        final var y = voxelState.posInWorld().y();
        final var i = voxelState.depth();

        final var rd = voxelState.world().getVoxelState(x + 1, y - 1, i);
        final var ld = voxelState.world().getVoxelState(x - 1, y - 1, i);

        var bits = 0x00;
        if (ld != null && ld.getVoxel().equals(this)) bits |= 0b10;
        if (rd != null && rd.getVoxel().equals(this)) bits |= 0b01;

        addUVSquareToMesh(mesh, voxelState.posInChunk(), voxelState.z(), voxelState.world().voxelTextureAtlas().getUv(textures[bits]));
    }

    public ResourceLocation[] getTextureIds() {
        return textures;
    }

    public ResourceLocation getPrimaryTextureId() {
        return textures[0b00];
    }

}
