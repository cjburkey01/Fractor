package com.cjburkey.radgame.game;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.Time;
import com.cjburkey.radgame.chunk.VoxelChunkMesher;
import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.ecs.Component;
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
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collections;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings("WeakerAccess")
public class WorldHandler extends Component {

    private final VoxelWorld voxelWorld;
    private final Registry<Voxel> voxels = new Registry<>();

    public int loadRadius = 6;
    public float loadTimeout = 5.0f;
    public double checkFrequency = 0.1f;
    private double lastCheck = (Time.getTime() - checkFrequency);
    public final ObjectOpenHashSet<Transform> chunkLoaders = new ObjectOpenHashSet<>();
    private final Object2FloatOpenHashMap<Vector2ic> chunks = new Object2FloatOpenHashMap<>();
    private final ObjectOpenHashSet<Vector2ic> chunksToLoad = new ObjectOpenHashSet<>();
    private final ObjectOpenHashSet<Vector2ic> chunksToUnLoad = new ObjectOpenHashSet<>();

    public WorldHandler(long seed, Scene scene, IVoxelChunkHeightmapGenerator voxelChunkGenerator, Shader chunkShader) {
        Voxels.init();
        registerVoxels();

        final var features = new ObjectArrayList<IVoxelChunkFeatureGenerator>();
        GameManager.EVENT_BUS.invoke(new EventRegisterFeatureGenerators(features));
        voxelWorld = new VoxelWorld(seed,
                scene,
                voxelChunkGenerator,
                features.toArray(new IVoxelChunkFeatureGenerator[0]),
                chunkShader,
                generateVoxelTextureAtlas());
    }

    @Override
    public void onUpdate() {
        final var now = Time.getTime();
        if ((now - lastCheck) >= checkFrequency) {
            for (final var chunkLoader : chunkLoaders) {
                final var chunkLoaderChunkPos = VoxelWorld.worldPosToChunk(chunkLoader.position);
                for (var x = -loadRadius; x < loadRadius; x++) {
                    for (var y = -loadRadius; y < loadRadius; y++) {
                        chunksToLoad.add(new Vector2i(x, y).add(chunkLoaderChunkPos));
                    }
                }
            }
            chunks.forEach((chunk, time) -> {
                if (time <= 0.0f && !chunksToLoad.contains(chunk)) chunksToUnLoad.add(chunk);
                else chunks.put(chunk, time - (float) checkFrequency);
            });

            chunksToUnLoad.forEach(chunk -> {
                chunks.removeFloat(chunk);
                voxelWorld.unloadChunk(chunk);
            });
            chunksToUnLoad.clear();

            chunksToLoad.forEach(chunk -> {
                if (!chunks.containsKey(chunk)) {
                    VoxelChunkMesher.generateMesh(voxelWorld.getOrGenChunk(chunk));
                }
                chunks.put(chunk, loadTimeout);
            });
            chunksToLoad.clear();

            lastCheck = now;
        }
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
