package com.cjburkey.radgame.game;

import com.cjburkey.radgame.RadGame;
import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.Time;
import com.cjburkey.radgame.chunk.VoxelChunk;
import com.cjburkey.radgame.chunk.VoxelChunkMesher;
import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.component.CameraZoom;
import com.cjburkey.radgame.component.CursorVoxelPicker;
import com.cjburkey.radgame.component.KeyboardMove;
import com.cjburkey.radgame.component.render.MaterialRenderer;
import com.cjburkey.radgame.component.render.MeshRenderer;
import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.glfw.Input;
import com.cjburkey.radgame.shader.Shader;
import com.cjburkey.radgame.shader.material.ColoredTransform;
import com.cjburkey.radgame.util.event.EventHandler;
import com.cjburkey.radgame.util.io.Log;
import com.cjburkey.radgame.util.math.Interpolate;
import com.cjburkey.radgame.util.noise.NoiseState;
import com.cjburkey.radgame.voxel.Voxels;
import com.cjburkey.radgame.world.generate.IVoxelChunkHeightmapGenerator;
import com.cjburkey.radgame.world.generate.VoxelChunkHeightmapGenerator;
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
    private Shader colorShader;
    private WorldHandler worldHandler;
    private CursorVoxelPicker voxelPicker;

    private double lastTitleUpdateTime = Time.getTime();
    private final Runtime runtime = Runtime.getRuntime();

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

        initWorld(new VoxelChunkHeightmapGenerator(15.0f, 75.0f, 0, 3));
    }

    private void initWorld(IVoxelChunkHeightmapGenerator generator) {
        buildCursorVoxelPicker();

        // TODO: EXPERIMENT! THIS IS NOT PRODUCTION-LEVEL CODE HERE!
        EVENT_BUS.addListener(WorldHandler.EventRegisterFeatureGenerators.class, e -> e.register(chunk -> {
            final var worldPos = chunk.getPosInWorld();
            final var noiseState = new NoiseState(chunk.world.seed, 1.0f, 35.0f, worldPos.x(), worldPos.y());

            for (var y = 0; y < VoxelChunk.CHUNK_SIZE; y++) {
                for (var x = 0; x < VoxelChunk.CHUNK_SIZE; x++) {
                    final var noise = Interpolate.map(noiseState.get(x, y), -1.0f, 1.0f, 0.0f, 1.0f);
                    final var voxelAt = chunk.getVoxelState(x, y, 1);
                    if (voxelAt != null && voxelAt.getVoxel().equals(Voxels.STONE) && noise >= 0.9f)
                        chunk.setVoxel(x, y, 1, Voxels.DIRT);
                }
            }
        }));

        worldHandler = new WorldHandler(0L);
        worldHandler.init(scene, generator, texShader);

        final var chunkGenTest = 15;
        Log.debug("Generating debug chunks: {}x{}", chunkGenTest * 2, chunkGenTest * 2);
        for (var y = -chunkGenTest; y < chunkGenTest; y++) {
            for (var x = -chunkGenTest; x < chunkGenTest; x++) {
                final var chunkA = worldHandler.getVoxelWorld().getOrGenChunk(x, y);
                VoxelChunkMesher.generateMesh(chunkA);
            }
        }
        Log.debug("Generated debug chunks: {},{} to {},{}", -chunkGenTest, -chunkGenTest, chunkGenTest - 1, chunkGenTest - 1);
    }

    private void buildCursorVoxelPicker() {
        voxelPicker = new CursorVoxelPicker();
        final var materialRenderer = new MaterialRenderer();
        final var meshRenderer = new MeshRenderer();
        final var object = scene.createObjectWith(materialRenderer, meshRenderer, voxelPicker);
        object.transform.position.z = 0.6f;

        final var material = new ColoredTransform(colorShader);
        materialRenderer.material = material;
        material.color.set(1.0f);

        final var thickness = 0.075f;
        meshRenderer.mesh.start()
                // Top
                .vert(0.0f, 1.0f)
                .vert(0.0f, 1.0f - thickness)
                .vert(1.0f, 1.0f - thickness)
                .verts(0, 2)
                .vert(1.0f, 1.0f)

                // Bottom
                .vert(0.0f, thickness)
                .vert(0.0f, 0.0f)
                .vert(1.0f, 0.0f)
                .verts(4, 6)
                .vert(1.0f, thickness)

                // Left
                .vert(0.0f, 1.0f - thickness)
                .vert(0.0f, thickness)
                .vert(thickness, thickness)
                .verts(8, 10)
                .vert(thickness, 1.0f - thickness)

                // Right
                .vert(1.0f - thickness, 1.0f - thickness)
                .vert(1.0f - thickness, thickness)
                .vert(1.0f, thickness)
                .verts(12, 14)
                .vert(1.0f, 1.0f - thickness)
                .end();
    }

    @Override
    public void onRemove() {
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
            RadGame.INSTANCE.window().setTitle(
                    String.format("Fractor 0.0.1 | %.2f FPS | %.2f UPS | %sMB / %sMB",
                            1.0f / Time.renderDeltaf(),
                            1.0f / Time.updateDeltaf(),
                            (runtime.totalMemory() - runtime.freeMemory()) / 1000000,
                            runtime.totalMemory() / 1000000));
        }
    }

    public WorldHandler getWorld() {
        return worldHandler;
    }

    public CursorVoxelPicker getVoxelPicker() {
        return voxelPicker;
    }

    private void initShaders() throws IOException {
        texShader = Shader.builder()
                .setVertexShader(new ResourceLocation("radgame", "shader/texVert", "glsl"))
                .setFragmentShader(new ResourceLocation("radgame", "shader/texFrag", "glsl"))
                .addUniforms("projectionMatrix", "viewMatrix", "modelMatrix", "tex")
                .build();
        colorShader = Shader.builder()
                .setVertexShader(new ResourceLocation("radgame", "shader/colorVert", "glsl"))
                .setFragmentShader(new ResourceLocation("radgame", "shader/colorFrag", "glsl"))
                .addUniforms("projectionMatrix", "viewMatrix", "modelMatrix", "color")
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
