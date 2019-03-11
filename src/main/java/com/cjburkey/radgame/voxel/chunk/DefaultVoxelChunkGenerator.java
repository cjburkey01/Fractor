package com.cjburkey.radgame.voxel.chunk;

import com.cjburkey.radgame.util.noise.NoiseState;
import com.cjburkey.radgame.voxel.VoxelTypes;

/**
 * Created by CJ Burkey on 2019/03/08
 */
public final class DefaultVoxelChunkGenerator implements IVoxelChunkGenerator {

    private final double noiseAmplitude;
    private final double noiseScale;
    private final int worldBaseHeight;

    public DefaultVoxelChunkGenerator(double noiseAmplitude,
                                      double noiseScale,
                                      int worldBaseHeight) {
        this.noiseAmplitude = noiseAmplitude;
        this.noiseScale = noiseScale;
        this.worldBaseHeight = worldBaseHeight;
    }

    @Override
    public void generate(final VoxelChunk chunk) {
        final var h = chunk.getPosInWorld().y();
        final var noise = new NoiseState(noiseAmplitude, noiseScale, chunk.getPosInWorld().x(), 0.0d);

        // The world is all generated on the "midground" layer, which is i=1
        for (var x = 0; x < VoxelChunk.CHUNK_SIZE; x++) {
            var noiseAt = getNoise(noise, x);
            var localY = (noiseAt - h);
            if (localY >= 0) {
                if (localY >= VoxelChunk.CHUNK_SIZE) localY = (VoxelChunk.CHUNK_SIZE - 1);
                for (var y = localY; y >= 0; y--) {
                    chunk.setVoxel(x, y, 1, VoxelTypes.STONE, false);
                }
            }
        }
    }

    private int getNoise(NoiseState noise, int x) {
        return (int) Math.ceil(noise.get(x, 0.0d)) + worldBaseHeight;
    }

}
