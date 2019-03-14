package com.cjburkey.radgame.game;

import com.cjburkey.radgame.RadGame;
import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.Time;
import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.component.CameraZoom;
import com.cjburkey.radgame.component.KeyboardMove;
import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.gl.shader.Shader;
import com.cjburkey.radgame.glfw.Input;
import com.cjburkey.radgame.util.event.EventHandler;
import com.cjburkey.radgame.voxel.chunk.DefaultVoxelChunkGenerator;
import com.cjburkey.radgame.voxel.chunk.IVoxelChunkGenerator;
import com.cjburkey.radgame.voxel.chunk.VoxelChunkMesher;
import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;

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

    private double lastTitleUpdateTime = Time.getTime();

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

        initWorld(new DefaultVoxelChunkGenerator(15.0f, 75.0f, 0, 3));
    }

    private void initWorld(IVoxelChunkGenerator generator) {
        worldHandler = new WorldHandler(0L);
        worldHandler.init(scene, generator, texShader);

        final var chunkGenTest = 9;
        for (var y = -chunkGenTest; y < chunkGenTest; y++) {
            for (var x = -chunkGenTest; x < chunkGenTest; x++) {
                final var chunkA = worldHandler.getVoxelWorld().getOrGenChunk(x, y);
                VoxelChunkMesher.generateMesh(chunkA);
            }
        }
    }

    @Override
    public void onRemove() {
        texShader.close();

        EVENT_BUS.invoke(new RadGame.EventCleanup());
    }

    @Override
    public void onUpdate() {
        if (Input.key().wasPressed(GLFW_KEY_ESCAPE)) RadGame.INSTANCE.close();
    }

    @Override
    public void onRender() {
        final var now = Time.getTime();
        if ((now - lastTitleUpdateTime) >= (1.0f / 10.0f)) {
            lastTitleUpdateTime = now;
            RadGame.INSTANCE.window().setTitle(String.format("Fractor 0.0.1 | %.2f FPS | %.2f UPS",
                    1.0f / Time.renderDeltaf(),
                    1.0f / Time.updateDeltaf()));
        }
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
