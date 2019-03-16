package com.cjburkey.radgame.game;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.shader.Shader;
import com.cjburkey.radgame.texture.TextureAtlas;
import com.cjburkey.radgame.util.event.Event;
import com.cjburkey.radgame.util.io.Log;
import com.cjburkey.radgame.util.registry.Registry;
import com.cjburkey.radgame.voxel.ITexturedVoxel;
import com.cjburkey.radgame.voxel.Voxel;
import com.cjburkey.radgame.voxel.Voxels;
import com.cjburkey.radgame.world.VoxelWorld;
import com.cjburkey.radgame.world.generate.IVoxelChunkFeatureGenerator;
import com.cjburkey.radgame.world.generate.IVoxelChunkHeightmapGenerator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collections;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings("WeakerAccess")
public class WorldHandler {

    private VoxelWorld voxelWorld;
    private Registry<Voxel> voxels = new Registry<>();

    private final long seed;

    public WorldHandler(long seed) {
        this.seed = seed;
    }

    public void init(Scene scene, IVoxelChunkHeightmapGenerator voxelChunkGenerator, Shader chunkShader) {
        Voxels.init();
        registerVoxels();

        final var features = new ObjectArrayList<IVoxelChunkFeatureGenerator>();
        GameManager.EVENT_BUS.invoke(new EventRegisterFeatureGenerators(features));
        voxelWorld = new VoxelWorld(seed, scene, voxelChunkGenerator, features.toArray(new IVoxelChunkFeatureGenerator[0]), chunkShader, generateVoxelTextureAtlas());
    }

    private void registerVoxels() {
        GameManager.EVENT_BUS.invoke(new VoxelTypeRegisterEvent(voxels));
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

    public static class EventRegisterFeatureGenerators extends Event {

        private final ObjectArrayList<IVoxelChunkFeatureGenerator> features;

        private EventRegisterFeatureGenerators(ObjectArrayList<IVoxelChunkFeatureGenerator> features) {
            this.features = features;
        }

        public void register(IVoxelChunkFeatureGenerator feature) {
            features.add(feature);
        }

        public void register(IVoxelChunkFeatureGenerator... features) {
            Collections.addAll(this.features, features);
        }

    }

}
