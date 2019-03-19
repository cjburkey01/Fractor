package com.cjburkey.radgame.chunk;

import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.mesh.Mesh;
import com.cjburkey.radgame.texture.TextureAtlas;

/**
 * Created by CJ Burkey on 2019/03/08
 */
public final class VoxelChunkMesher {

    public static void generateMesh(final Transform transform,
                                    final Mesh.MeshBuilder meshBuilder,
                                    final TextureAtlas atlas,
                                    final VoxelChunk chunk) {
        transform.position.set(chunk.worldPos().x(), chunk.worldPos().y(), 0.0f);

        for (var i = 0; i < VoxelChunk.CHUNK_THICKNESS; i++) {
            for (var y = 0; y < VoxelChunk.CHUNK_SIZE; y++) {
                for (var x = 0; x < VoxelChunk.CHUNK_SIZE; x++) {
                    final var stateAt = chunk.getVoxelState(x, y, i);
                    if (stateAt != null) {
                        meshBuilder.startSubMesh();
                        stateAt.getVoxel().generateMesh(meshBuilder, atlas, stateAt);
                        meshBuilder.endSubMesh();
                    }
                }
            }
        }
    }

}
