package com.cjburkey.radgame.voxel.chunk;

/**
 * Created by CJ Burkey on 2019/03/08
 */
public final class DefaultVoxelChunkGenerator implements IVoxelChunkGenerator {

    public void generate(final VoxelChunk chunk) {
        for (var i = 0; i < VoxelChunk.CHUNK_THICKNESS; i++) {
            for (var y = 0; y < VoxelChunk.CHUNK_SIZE; y++) {
                for (var x = 0; x < VoxelChunk.CHUNK_SIZE; x++) {
                    chunk.setVoxel(x, y, i, null);
                }
            }
        }
    }

}
