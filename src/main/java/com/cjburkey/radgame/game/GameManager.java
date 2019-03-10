package com.cjburkey.radgame.game;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.ecs.GameObject;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.gl.Texture;
import com.cjburkey.radgame.gl.TextureAtlas;
import com.cjburkey.radgame.gl.shader.Shader;
import com.cjburkey.radgame.voxel.TexturedVoxel;
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

    private static Shader whiteShader;
    private static Shader texShader;

    private GameObject objTest;

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

        {
            final TextureAtlas atlas;
            try {
                atlas = new TextureAtlas(
                        Texture.readStream(new ResourceLocation("radgame", "texture/test", "png").getStream()),
                        1);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            final var stone = new TexturedVoxel(new ResourceLocation("radgame", "voxel/stone"), new Vector2i(0, 0));

            final var world = new VoxelWorld(scene, new DefaultVoxelChunkGenerator(), texShader, atlas);
            final var chunkA = world.getOrGenChunk(new Vector2i());
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    world.setVoxel(x, y, 1, stone);
                }
            }
            VoxelChunkMesher.generateMesh(chunkA);
        }
    }

    @Override
    public void onRemove() {
        whiteShader.close();
        texShader.close();
    }

    private static void initShaders() throws IOException {
        whiteShader = Shader.builder()
                .setVertexShader(new ResourceLocation("radgame", "shader/whiteVert", "glsl"))
                .setFragmentShader(new ResourceLocation("radgame", "shader/whiteFrag", "glsl"))
                .addUniforms("projectionMatrix", "viewMatrix", "modelMatrix")
                .build();
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
