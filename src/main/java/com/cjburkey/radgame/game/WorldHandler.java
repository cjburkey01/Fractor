package com.cjburkey.radgame.game;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.Time;
import com.cjburkey.radgame.chunk.VoxelChunk;
import com.cjburkey.radgame.chunk.VoxelChunkMesher;
import com.cjburkey.radgame.component.Transform;
import com.cjburkey.radgame.component.render.MaterialRenderer;
import com.cjburkey.radgame.component.render.MeshRenderer;
import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.ecs.GameObject;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.shader.Material;
import com.cjburkey.radgame.shader.Shader;
import com.cjburkey.radgame.shader.material.TexturedTransform;
import com.cjburkey.radgame.texture.TextureAtlas;
import com.cjburkey.radgame.util.event.Event;
import com.cjburkey.radgame.util.io.Log;
import com.cjburkey.radgame.util.registry.Registry;
import com.cjburkey.radgame.voxel.ITexturedVoxel;
import com.cjburkey.radgame.voxel.Voxels;
import com.cjburkey.radgame.world.Voxel;
import com.cjburkey.radgame.world.VoxelWorld;
import com.cjburkey.radgame.world.generate.IVoxelChunkFeatureGenerator;
import com.cjburkey.radgame.world.generate.IVoxelChunkHeightmapGenerator;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
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
    private final ObjectOpenHashSet<Vector2ic> chunksToLoad = new ObjectOpenHashSet<>();
    private final ObjectOpenHashSet<Vector2ic> chunksToUnLoad = new ObjectOpenHashSet<>();
    private final Object2ObjectOpenHashMap<Vector2ic, LoadedChunk> loadedChunks = new Object2ObjectOpenHashMap<>();
    private final Scene scene;
    private final Shader chunkShader;
    private final TextureAtlas textureAtlas;

    public WorldHandler(final long seed,
                        final Scene scene,
                        final IVoxelChunkHeightmapGenerator voxelChunkGenerator,
                        final Shader chunkShader) {
        Voxels.init();
        registerVoxels();

        this.scene = Objects.requireNonNull(scene);
        this.chunkShader = Objects.requireNonNull(chunkShader);

        final var features = new ObjectArrayList<GenerationFeature>();
        GameManager.EVENT_BUS.forceInvoke(new EventRegisterFeatureGenerators(features));
        features.sort(Comparator.comparingInt(GenerationFeature::weight));

        voxelWorld = new VoxelWorld(GameManager.EVENT_BUS,
                seed,
                voxelChunkGenerator,
                features.stream()
                        .map(GenerationFeature::generator)
                        .toArray(IVoxelChunkFeatureGenerator[]::new));

        textureAtlas = generateVoxelTextureAtlas();

        voxelWorld.eventHandler.addListener(VoxelChunk.EventChunkUpdate.class, e -> loadedChunks.get(e.chunk.getChunkPos()).generateMesh());
        voxelWorld.eventHandler.addListener(VoxelWorld.EventChunkUnload.class, e -> loadedChunks.get(e.chunk.getChunkPos()).onUnload());
    }

    private void registerVoxels() {
        GameManager.EVENT_BUS.forceInvoke(new VoxelTypeRegisterEvent(voxels));
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
            loadedChunks.forEach((pos, chunk) -> {
                if (chunk.time <= 0.0f && !chunksToLoad.contains(pos)) chunksToUnLoad.add(pos);
                else chunk.time -= checkFrequency;
            });

            chunksToUnLoad.forEach(voxelWorld::unloadChunk);
            chunksToUnLoad.clear();

            chunksToLoad.forEach(chunk -> voxelWorld.ifNotPresent(chunk, () -> {
                final var at = loadedChunks.get(chunk);
                final var chunkAt = voxelWorld.getChunkOrLoadEmpty(chunk);
                if (at == null) loadedChunks.put(chunk, new LoadedChunk(chunkAt));
                voxelWorld.generateChunk(chunkAt);
            }));
            chunksToLoad.clear();

            lastCheck = now;
        }
    }

    public VoxelWorld world() {
        return voxelWorld;
    }

    public LoadedChunk getChunk(Vector2ic chunkPos) {
        return loadedChunks.getOrDefault(chunkPos, null);
    }

    public LoadedChunk getChunk(int x, int y) {
        return getChunk(new Vector2i(x, y));
    }

    public class LoadedChunk {

        private float time = loadTimeout;
        private final Vector2ic chunk;
        private final VoxelChunk chunkAt;
        private final TexturedTransform material;
        private final MeshRenderer meshRenderer;
        private final GameObject gameObject;

        private LoadedChunk(final VoxelChunk chunk) {
            this.chunk = Objects.requireNonNull(chunk).getChunkPos();
            chunkAt = chunk;
            material = new TexturedTransform(chunkShader);
            meshRenderer = new MeshRenderer();

            final var materialRenderer = new MaterialRenderer();
            materialRenderer.material = material;
            material.texture = textureAtlas.getTexture();
            gameObject = scene.createObjectWith(materialRenderer, meshRenderer);
        }

        private void generateMesh() {
            if (chunkAt.isGenerated()) {
                try (final var builder = meshRenderer.mesh.start()) {
                    VoxelChunkMesher.generateMesh(gameObject.transform, builder, textureAtlas, chunkAt);
                }
            }
        }

        private void onUnload() {
            loadedChunks.remove(chunk);
            scene.destroy(gameObject);
        }

        public Material material() {
            return material;
        }

    }

    public static class EventRegisterFeatureGenerators extends Event {

        private final ObjectArrayList<GenerationFeature> features;

        private EventRegisterFeatureGenerators(ObjectArrayList<GenerationFeature> features) {
            this.features = features;
        }

        // Higher weight means an earlier call
        public void register(int weight, IVoxelChunkFeatureGenerator feature) {
            features.add(new GenerationFeature(weight, feature));
        }

        // Higher weight means an earlier call
        public void register(int weight, IVoxelChunkFeatureGenerator... features) {
            for (IVoxelChunkFeatureGenerator feature : features) register(weight, feature);
        }

    }

    public static final class GenerationFeature {

        private final int weight;
        private final IVoxelChunkFeatureGenerator generator;

        private GenerationFeature(int weight, IVoxelChunkFeatureGenerator generator) {
            this.weight = -weight;
            this.generator = generator;
        }

        private int weight() {
            return weight;
        }

        private IVoxelChunkFeatureGenerator generator() {
            return generator;
        }

    }

}
