package com.cjburkey.radgame.game;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.gl.TextureAtlas;
import com.cjburkey.radgame.gl.shader.Shader;
import com.cjburkey.radgame.util.io.Log;
import com.cjburkey.radgame.util.registry.Registry;
import com.cjburkey.radgame.voxel.ITexturedVoxel;
import com.cjburkey.radgame.voxel.Voxels;
import com.cjburkey.radgame.voxel.chunk.IVoxelChunkGenerator;
import com.cjburkey.radgame.voxel.world.Voxel;
import com.cjburkey.radgame.voxel.world.VoxelWorld;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collections;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings("WeakerAccess")
public class WorldHandler {

    private VoxelWorld voxelWorld;
    private Registry<Voxel> voxels = new Registry<>();

    public void init(Scene scene, IVoxelChunkGenerator voxelChunkGenerator, Shader chunkShader) {
        Voxels.init();
        registerVoxels();
        voxelWorld = new VoxelWorld(scene, voxelChunkGenerator, chunkShader, generateVoxelTextureAtlas());
    }

    private void registerVoxels() {
        GameManager.EVENT_BUS.invoke(VoxelTypeRegisterEvent.class, new VoxelTypeRegisterEvent(voxels));
        voxels.finish();
    }

    private TextureAtlas generateVoxelTextureAtlas() {
        final var textureLocations = new ObjectOpenHashSet<ResourceLocation>();
        voxels.foreach(voxel -> {
            if (voxel instanceof ITexturedVoxel) {
                Collections.addAll(textureLocations, ((ITexturedVoxel) voxel).getTextureIds());
            }
        });
        final var textureAtlas = TextureAtlas.create(32, textureLocations.toArray(ResourceLocation[]::new));
        if (textureAtlas == null) Log.error("Failed to generate voxel texture atlas");
        else Log.info("Generated voxel texture atlas with {}x{} tiles", textureAtlas.width, textureAtlas.width);
        return textureAtlas;
    }

    public VoxelWorld getVoxelWorld() {
        return voxelWorld;
    }

}
