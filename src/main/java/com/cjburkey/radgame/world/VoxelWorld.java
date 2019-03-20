package com.cjburkey.radgame.world;

import com.cjburkey.radgame.RadGame;
import com.cjburkey.radgame.chunk.VoxelChunk;
import com.cjburkey.radgame.game.GameManager;
import com.cjburkey.radgame.util.concurrent.ThreadPool;
import com.cjburkey.radgame.util.event.Event;
import com.cjburkey.radgame.util.event.EventHandler;
import com.cjburkey.radgame.world.generate.IVoxelChunkFeatureGenerator;
import com.cjburkey.radgame.world.generate.IVoxelChunkHeightmapGenerator;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Objects;
import java.util.function.Consumer;
import org.joml.Random;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

import static java.lang.Math.*;

/**
 * Created by CJ Burkey on 2019/03/08
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class VoxelWorld {

    public final EventHandler eventHandler;
    private final IVoxelChunkHeightmapGenerator voxelChunkGenerator;
    private final IVoxelChunkFeatureGenerator[] featureQueue;
    private final Object2ObjectOpenHashMap<Vector2ic, VoxelChunk> chunks = new Object2ObjectOpenHashMap<>();
    public final long seed;
    public final Random random;
    private final ThreadPool generationPool;

    public VoxelWorld(final EventHandler eventHandler,
                      final long seed,
                      final IVoxelChunkHeightmapGenerator voxelChunkGenerator,
                      final IVoxelChunkFeatureGenerator[] features) {
        this.eventHandler = Objects.requireNonNull(eventHandler);
        this.seed = seed;
        this.voxelChunkGenerator = Objects.requireNonNull(voxelChunkGenerator);
        System.arraycopy(Objects.requireNonNull(features), 0, featureQueue = new IVoxelChunkFeatureGenerator[features.length], 0, features.length);
        this.random = new Random(seed);

        chunks.defaultReturnValue(null);

        generationPool = new ThreadPool(4);

        GameManager.EVENT_BUS.addListener(RadGame.EventCleanup.class, (e) -> generationPool.stop());
    }

    // Only loads a new chunk, it won't be generated or populated
    public VoxelChunk getChunkOrLoadEmpty(final Vector2ic chunkPos) {
        if (chunks.containsKey(chunkPos)) return chunks.get(chunkPos);

        final var chunk = new VoxelChunk(chunkPos, this);
        chunks.put(chunkPos, chunk);
        eventHandler.invokeSafe(new EventChunkLoad(chunk));
        return chunk;
    }

    // Generates
    public VoxelChunk getChunkOrLoadAndGen(final Vector2ic chunkPos) {
        final var chunk = getChunkOrLoadEmpty(chunkPos);
        generateChunk(chunk);
        return chunk;
    }

    public void generateChunk(final VoxelChunk chunk) {
        if (!chunk.isGenerated()) {
            eventHandler.invokeSafe(new EventChunkGenerating(chunk));
            generationPool.queueAction(() -> {
                voxelChunkGenerator.generate(chunk);
                for (IVoxelChunkFeatureGenerator featureGenerator : featureQueue) featureGenerator.generate(chunk);
                chunk.markGenerated();
                eventHandler.invokeSafe(new EventChunkGenerated(chunk));
            });
        }
    }

    public VoxelChunk getChunkOrLoadAndGen(final int x, int y) {
        return getChunkOrLoadAndGen(new Vector2i(x, y));
    }

    public void ifPresent(Vector2ic chunk, Consumer<VoxelChunk> ifPresent) {
        final var chunkAt = getChunk(chunk);
        if (chunkAt != null) ifPresent.accept(chunkAt);
    }

    public void ifPresent(int x, int y, Consumer<VoxelChunk> ifPresent) {
        final var chunkAt = getChunk(x, y);
        if (chunkAt != null) ifPresent.accept(chunkAt);
    }

    public void ifNotPresent(Vector2ic chunk, Runnable ifPresent) {
        final var chunkAt = getChunk(chunk);
        if (chunkAt == null) ifPresent.run();
    }

    public void ifNotPresent(int x, int y, Runnable ifPresent) {
        final var chunkAt = getChunk(x, y);
        if (chunkAt == null) ifPresent.run();
    }

    public void unloadChunk(final Vector2ic chunkPos) {
        final var chunkAt = chunks.remove(chunkPos);
        if (chunkAt != null) eventHandler.invokeSafe(new EventChunkUnload(chunkAt));
    }

    public VoxelChunk getChunk(final Vector2ic chunkPos) {
        return chunks.get(chunkPos);
    }

    public VoxelChunk getChunk(int chunkX, int chunkY) {
        return getChunk(new Vector2i(chunkX, chunkY));
    }

    public VoxelState getVoxelState(final int worldX, final int worldY, final int i) {
        final var atPos = worldPosToChunk(worldX, worldY);
        final var at = getChunk(atPos);
        if (at == null) return null;

        return at.getVoxelState(worldPosToInChunk(atPos, worldX, worldY), i);
    }

    public VoxelState getVoxelState(final Vector2ic worldPos, final int i) {
        return getVoxelState(worldPos.x(), worldPos.y(), i);
    }

    public void setVoxel(final int worldX, final int worldY, final int i, final Voxel voxel, boolean update) {
        final var atPos = worldPosToChunk(worldX, worldY);
        getChunkOrLoadEmpty(atPos).setVoxel(worldPosToInChunk(atPos, worldX, worldY), i, voxel, update);
    }

    public void setVoxel(final Vector2ic worldPos, final int i, final Voxel voxel, boolean update) {
        setVoxel(worldPos.x(), worldPos.y(), i, voxel, update);
    }

    /**
     * Converts the given block position within the world into the chunk position of its containing chunk
     */
    public static Vector2i worldPosToChunk(final int worldX, final int worldY) {
        return new Vector2i(floorDiv(worldX, VoxelChunk.CHUNK_SIZE), floorDiv(worldY, VoxelChunk.CHUNK_SIZE));
    }

    /**
     * Converts the given block position within the world into the chunk position of its containing chunk
     */
    public static Vector2i worldPosToChunk(final Vector2ic worldPos) {
        return worldPosToChunk(worldPos.x(), worldPos.y());
    }

    /**
     * Converts the given block position within the world into the chunk position of its containing chunk
     */
    public static Vector2i worldPosToChunk(final Vector3ic worldPos) {
        return worldPosToChunk(worldPos.x(), worldPos.y());
    }

    /**
     * Converts the given block position within the world into the chunk position of its containing chunk
     */
    public static Vector2i worldPosToChunk(final Vector2fc worldPos) {
        return worldPosToChunk((int) worldPos.x(), (int) worldPos.y());
    }

    /**
     * Converts the given block position within the world into the chunk position of its containing chunk
     */
    public static Vector2i worldPosToChunk(final Vector3fc worldPos) {
        return worldPosToChunk((int) worldPos.x(), (int) worldPos.y());
    }

    /**
     * Converts the given block position within the world into the block position withgin its containing chunk
     */
    private static Vector2i worldPosToInChunk(final Vector2ic chunkPos, final int worldX, final int worldY) {
        Vector2i ret = new Vector2i(chunkPos).mul(VoxelChunk.CHUNK_SIZE);
        return ret.set(worldX - ret.x(), worldY - ret.y());
    }

    /**
     * Converts the given block position within the world into the block position withgin its containing chunk
     */
    private static Vector2i worldPosToInChunk(final Vector2ic chunkPos, final Vector2ic worldPos) {
        return worldPosToInChunk(chunkPos, worldPos.x(), worldPos.y());
    }

    /**
     * Converts the given block position within the world into the block position withgin its containing chunk
     */
    public static Vector2i worldPosToInChunk(final int worldX, final int worldY) {
        return worldPosToInChunk(worldPosToChunk(worldX, worldY), worldX, worldY);
    }

    /**
     * Converts the given block position within the world into the block position withgin its containing chunk
     */
    public static Vector2i worldPosToInChunk(final Vector2ic worldPos) {
        return worldPosToInChunk(worldPos.x(), worldPos.y());
    }

    public static abstract class ChunkEvent extends Event {

        public final VoxelChunk chunk;

        public ChunkEvent(final VoxelChunk chunk) {
            this.chunk = Objects.requireNonNull(chunk);
        }

    }

    public static class EventChunkLoad extends ChunkEvent {

        private EventChunkLoad(VoxelChunk chunk) {
            super(chunk);
        }

    }

    public static class EventChunkGenerating extends ChunkEvent {

        private EventChunkGenerating(VoxelChunk chunk) {
            super(chunk);
        }

    }

    public static class EventChunkGenerated extends ChunkEvent {

        private EventChunkGenerated(VoxelChunk chunk) {
            super(chunk);
        }

    }

    public static class EventChunkUnload extends ChunkEvent {

        private EventChunkUnload(VoxelChunk chunk) {
            super(chunk);
        }

    }

}
