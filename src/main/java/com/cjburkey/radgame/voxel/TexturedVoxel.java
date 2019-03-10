package com.cjburkey.radgame.voxel;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.gl.Mesh;
import com.cjburkey.radgame.voxel.world.Voxel;
import com.cjburkey.radgame.voxel.world.VoxelState;
import java.util.Objects;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Created by CJ Burkey on 2019/03/06
 */
public class TexturedVoxel extends Voxel {

    private final Vector2ic atlasPos;

    public TexturedVoxel(final ResourceLocation id, final Vector2ic atlasPos) {
        super(id);

        this.atlasPos = new Vector2i(Objects.requireNonNull(atlasPos));
    }

    public void generateMesh(final Mesh.MeshBuilder mesh, final VoxelState voxelState) {
        final var cx = voxelState.getPosInChunk().x();
        final var cy = voxelState.getPosInChunk().y();
        final var uv = voxelState.getWorld().getVoxelTextureAtlas().getUv(atlasPos);

        mesh.vert(cx, cy + 1.0f).uv(uv.minX, uv.minY);
        mesh.vert(cx, cy).uv(uv.minX, uv.maxY);
        mesh.vert(cx + 1.0f, cy).uv(uv.maxX, uv.maxY);
        mesh.verts(0, 2);
        mesh.vert(cx + 1.0f, cy + 1.0f).uv(uv.maxX, uv.minY);
    }

    public Vector2ic getAtlasPos() {
        return atlasPos;
    }

}
