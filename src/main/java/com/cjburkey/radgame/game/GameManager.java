package com.cjburkey.radgame.game;

import com.cjburkey.radgame.RadGame;
import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.gl.TextureAtlas;
import com.cjburkey.radgame.gl.shader.Shader;
import com.cjburkey.radgame.voxel.chunk.DefaultVoxelChunkGenerator;
import com.cjburkey.radgame.voxel.chunk.VoxelChunkMesher;
import com.cjburkey.radgame.voxel.world.VoxelWorld;
import java.io.IOException;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL13.*;

/**
 * Created by CJ Burkey on 2019/03/03
 */
@SuppressWarnings("WeakerAccess")
public class GameManager extends Component {

    public static final GameManager manager = new GameManager();
    private static Scene scene;

    private Shader texShader;
    private VoxelWorld voxelWorld;

    @Override
    public void onLoad() {
        glEnable(GL_TEXTURE);
        glActiveTexture(GL_TEXTURE0);

        try {
            initShaders();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scene.createObjectWith(new Camera());    // Create main camera
        Camera.main.halfHeight = 6.5f;

        initWorld();
    }

    private void initWorld() {
        final TextureAtlas atlas = TextureAtlas.create(32, new ResourceLocation("radgame", "texture/voxel/stone", "png"));
        if (atlas == null) RadGame.INSTANCE.close();

        voxelWorld = new VoxelWorld(scene, new DefaultVoxelChunkGenerator(), texShader, atlas);
        final var chunkA = voxelWorld.getOrGenChunk(new Vector2i());
        VoxelChunkMesher.generateMesh(chunkA);
    }

    @Override
    public void onRemove() {
        texShader.close();
    }

    public VoxelWorld getWorld() {
        return voxelWorld;
    }

    private void initShaders() throws IOException {
        texShader = Shader.builder()
                .setVertexShader(new ResourceLocation("radgame", "shader/texVert", "glsl"))
                .setFragmentShader(new ResourceLocation("radgame", "shader/texFrag", "glsl"))
                .addUniforms("projectionMatrix", "viewMatrix", "modelMatrix", "tex")
                .build();
    }

    // Initializes a GameManager object in the supplied scene
    public static void install(Scene scene) {
        if (GameManager.scene != null) return;
        GameManager.scene = scene;
        scene.createObjectWith(manager);
    }

    public static Scene scene() {
        return scene;
    }

}
