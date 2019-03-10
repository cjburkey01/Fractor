package com.cjburkey.radgame.voxel.chunk;

/**
 * Created by CJ Burkey on 2019/03/08
 */
public final class VoxelChunkMesher {

    public static void generateMesh(VoxelChunk chunk) {
        chunk.gameObject.transform.position.set(chunk.getPosInWorld().x(), chunk.getPosInWorld().y(), 0.0f);

        final var meshBuilder = chunk.meshRenderer.mesh.start();
        for (var i = 0; i < VoxelChunk.CHUNK_THICKNESS; i++) {
            for (var y = 0; y < VoxelChunk.CHUNK_SIZE; y++) {
                for (var x = 0; x < VoxelChunk.CHUNK_SIZE; x++) {
                    final var stateAt = chunk.getVoxelState(x, y, i);
                    if (stateAt != null) {
                        meshBuilder.startSubMesh();
                        stateAt.getVoxel().generateMesh(meshBuilder, stateAt);
                        meshBuilder.endSubMesh();
                    }
                }
            }
        }
        meshBuilder.end();
    }

}
