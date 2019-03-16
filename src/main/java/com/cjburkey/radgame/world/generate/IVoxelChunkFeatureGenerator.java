package com.cjburkey.radgame.world.generate;

import com.cjburkey.radgame.chunk.VoxelChunk;

/**
 * Created by CJ Burkey on 2019/03/08
 */
public interface IVoxelChunkFeatureGenerator {

    void generate(final VoxelChunk chunk);

}
