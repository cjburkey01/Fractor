package com.cjburkey.radgame.game;

import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.gl.TextureAtlas;
import com.cjburkey.radgame.gl.shader.Shader;
import com.cjburkey.radgame.voxel.chunk.IVoxelChunkGenerator;
import com.cjburkey.radgame.voxel.world.VoxelWorld;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings("WeakerAccess")
public class WorldHandler {

    private VoxelWorld voxelWorld;

    public void init(Scene scene, IVoxelChunkGenerator voxelChunkGenerator, Shader chunkShader, TextureAtlas chunkTextures) {
        voxelWorld = new VoxelWorld(scene, voxelChunkGenerator, chunkShader, chunkTextures);
    }

    public VoxelWorld getVoxelWorld() {
        return voxelWorld;
    }

}
