package com.cjburkey.radgame.world;

import com.cjburkey.radgame.chunk.VoxelChunk;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.shader.Shader;
import com.cjburkey.radgame.texture.TextureAtlas;
import com.cjburkey.radgame.voxel.Voxel;
import com.cjburkey.radgame.world.generate.IVoxelChunkFeatureGenerator;
import com.cjburkey.radgame.world.generate.IVoxelChunkHeightmapGenerator;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Objects;
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

    private final Scene scene;
    private final IVoxelChunkHeightmapGenerator voxelChunkGenerator;
    private final IVoxelChunkFeatureGenerator[] featureQueue;
    private final Shader voxelShader;
    private final TextureAtlas voxelTextureAtlas;
    private final Object2ObjectOpenHashMap<Vector2ic, VoxelChunk> chunks = new Object2ObjectOpenHashMap<>();
    public final long seed;

    public VoxelWorld(final long seed,
                      final Scene scene,
                      final IVoxelChunkHeightmapGenerator voxelChunkGenerator,
                      final IVoxelChunkFeatureGenerator[] features,
                      final Shader voxelShader,
                      final TextureAtlas voxelTextureAtlas) {
        this.seed = seed;
        this.scene = Objects.requireNonNull(scene);
        this.voxelChunkGenerator = Objects.requireNonNull(voxelChunkGenerator);
        System.arraycopy(Objects.requireNonNull(features), 0, featureQueue = new IVoxelChunkFeatureGenerator[features.length], 0, features.length);
        this.voxelShader = Objects.requireNonNull(voxelShader);
        this.voxelTextureAtlas = Objects.requireNonNull(voxelTextureAtlas);
    }

    // Only creates a new chunk, it won't be generated
    public VoxelChunk getChunkOrNewRaw(final Vector2ic chunkPos) {
        if (chunks.containsKey(chunkPos)) return chunks.get(chunkPos);
        final var chunk = new VoxelChunk(scene, chunkPos, this, voxelShader, voxelTextureAtlas);
        chunks.put(chunkPos, chunk);
        return chunk;
    }

    // Generates
    public VoxelChunk getOrGenChunk(final Vector2ic chunkPos) {
        final var chunk = getChunkOrNewRaw(chunkPos);
        if (!chunk.isGenerated()) {
            voxelChunkGenerator.generate(chunk);
            for (IVoxelChunkFeatureGenerator featureGenerator : featureQueue) featureGenerator.generate(chunk);
            chunk.markGenerated();
        }
        return chunk;
    }

    public VoxelChunk getOrGenChunk(final int x, int y) {
        return getOrGenChunk(new Vector2i(x, y));
    }

    public void unloadChunk(final Vector2ic chunkPos) {
        final var chunkAt = chunks.remove(chunkPos);
        if (chunkAt != null) chunkAt.onUnload();
    }

    public VoxelChunk getChunk(final Vector2i chunkPos) {
        return chunks.getOrDefault(chunkPos, null);
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

    public void setVoxel(final int worldX, final int worldY, final int i, final Voxel voxel) {
        final var atPos = worldPosToChunk(worldX, worldY);
        final var at = getChunk(atPos);
        if (at != null) at.setVoxel(worldPosToInChunk(atPos, worldX, worldY), i, voxel);
    }

    public void setVoxel(final Vector2ic worldPos, final int i, final Voxel voxel) {
        setVoxel(worldPos.x(), worldPos.y(), i, voxel);
    }

    public TextureAtlas voxelTextureAtlas() {
        return voxelTextureAtlas;
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

}
