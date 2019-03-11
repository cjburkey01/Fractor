package com.cjburkey.radgame.game;

import com.cjburkey.radgame.RadGame;
import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.Time;
import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.component.CameraZoom;
import com.cjburkey.radgame.component.KeyboardMove;
import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.gl.TextureAtlas;
import com.cjburkey.radgame.gl.shader.Shader;
import com.cjburkey.radgame.util.event.EventHandler;
import com.cjburkey.radgame.voxel.chunk.DefaultVoxelChunkGenerator;
import com.cjburkey.radgame.voxel.chunk.IVoxelChunkGenerator;
import com.cjburkey.radgame.voxel.chunk.VoxelChunkMesher;
import java.io.IOException;

/**
 * Created by CJ Burkey on 2019/03/03
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class GameManager extends Component {

    public static final EventHandler EVENT_BUS = new EventHandler();
    public static final GameManager MANAGER = new GameManager();
    private static Scene scene;

    private Shader texShader;
    private WorldHandler worldHandler;

    @Override
    public void onLoad() {
        try {
            initShaders();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create main camera
        scene.createObjectWith(new Camera(), new CameraZoom(), new KeyboardMove());
        Camera.main.halfHeight = 6.5f;

        initWorld(new DefaultVoxelChunkGenerator(15.0f, 75.0f, 0));
    }

    private void initWorld(IVoxelChunkGenerator generator) {
        final TextureAtlas atlas = TextureAtlas.create(32, new ResourceLocation("radgame", "texture/voxel/stone", "png"));
        if (atlas == null) RadGame.INSTANCE.close();

        worldHandler = new WorldHandler();
        worldHandler.init(scene, generator, texShader, atlas);

        for (var y = -5; y < 5; y++) {
            for (var x = -5; x < 5; x++) {
                final var chunkA = worldHandler.getVoxelWorld().getOrGenChunk(x, y);
                VoxelChunkMesher.generateMesh(chunkA);
            }
        }
    }

    @Override
    public void onRemove() {
        texShader.close();
    }

    @Override
    public void onRender() {
        RadGame.INSTANCE.window().setTitle(String.format("%.2f", 1.0f / Time.renderDeltaf()));
    }

    public WorldHandler getWorld() {
        return worldHandler;
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
        scene.createObjectWith(MANAGER);
    }

    public static Scene scene() {
        return scene;
    }

}
