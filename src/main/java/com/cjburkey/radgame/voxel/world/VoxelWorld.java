package com.cjburkey.radgame.voxel.world;

import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.gl.TextureAtlas;
import com.cjburkey.radgame.gl.shader.Shader;
import com.cjburkey.radgame.voxel.chunk.IVoxelChunkGenerator;
import com.cjburkey.radgame.voxel.chunk.VoxelChunk;
import java.util.HashMap;
import java.util.Objects;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import static java.lang.Math.*;

/**
 * Created by CJ Burkey on 2019/03/08
 */
@SuppressWarnings("WeakerAccess")
public final class VoxelWorld {

    private final Scene scene;
    private final IVoxelChunkGenerator voxelChunkGenerator;
    private final Shader voxelShader;
    private final TextureAtlas voxelTextureAtlas;
    private final HashMap<Vector2ic, VoxelChunk> chunks = new HashMap<>();

    public VoxelWorld(final Scene scene,
                      final IVoxelChunkGenerator voxelChunkGenerator,
                      final Shader voxelShader,
                      final TextureAtlas voxelTextureAtlas) {
        this.scene = Objects.requireNonNull(scene);
        this.voxelChunkGenerator = Objects.requireNonNull(voxelChunkGenerator);
        this.voxelShader = Objects.requireNonNull(voxelShader);
        this.voxelTextureAtlas = Objects.requireNonNull(voxelTextureAtlas);
    }

    public VoxelChunk getOrGenChunk(final Vector2ic chunkPos) {
        if (chunks.containsKey(chunkPos)) return chunks.get(chunkPos);

        final var chunk = new VoxelChunk(scene, chunkPos, this, voxelShader, voxelTextureAtlas);
        chunks.put(chunkPos, chunk);
        voxelChunkGenerator.generate(chunk);
        return chunk;
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

    public TextureAtlas getVoxelTextureAtlas() {
        return voxelTextureAtlas;
    }

    public static Vector2i worldPosToChunk(final int worldX, final int worldY) {
        return new Vector2i(floorDiv(worldX, VoxelChunk.CHUNK_SIZE), floorDiv(worldY, VoxelChunk.CHUNK_SIZE));
    }

    public static Vector2i worldPosToChunk(final Vector2ic worldPos) {
        return worldPosToChunk(worldPos.x(), worldPos.y());
    }

    private static Vector2i worldPosToInChunk(final Vector2ic chunkPos, final int worldX, final int worldY) {
        Vector2i ret = new Vector2i(chunkPos).mul(VoxelChunk.CHUNK_SIZE);
        return ret.set(worldX - ret.x(), worldY - ret.y());
    }

    private static Vector2i worldPosToInChunk(final Vector2ic chunkPos, final Vector2ic worldPos) {
        return worldPosToInChunk(chunkPos, worldPos.x(), worldPos.y());
    }

    public static Vector2i worldPosToInChunk(final int worldX, final int worldY) {
        return worldPosToInChunk(worldPosToChunk(worldX, worldY), worldX, worldY);
    }

    public static Vector2i worldPosToInChunk(final Vector2ic worldPos) {
        return worldPosToInChunk(worldPos.x(), worldPos.y());
    }

}