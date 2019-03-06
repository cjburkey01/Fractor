package com.cjburkey.radgame.game;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.component.Camera;
import com.cjburkey.radgame.component.render.MaterialRenderer;
import com.cjburkey.radgame.component.render.MeshRenderer;
import com.cjburkey.radgame.ecs.Component;
import com.cjburkey.radgame.ecs.Scene;
import com.cjburkey.radgame.gl.Texture;
import com.cjburkey.radgame.gl.shader.Shader;
import com.cjburkey.radgame.gl.shader.material.TexturedTransform;
import java.io.IOException;

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

        // Test mesh object
        /*{
            MaterialRenderer materialRenderer = new MaterialRenderer();
            MeshRenderer meshRenderer = new MeshRenderer();

            meshRenderer.mesh
                    .start()
                    //  1---0
                    //  |  /|
                    //  | / |
                    //  |/  |
                    //  2---3
                    //
                    //  Triangles:  0, 1, 2
                    //              0, 2, 3
                    .vert(0.5f, 0.5f)       // 0
                    .vert(-0.5f, 0.5f)      // 1
                    .vert(-0.5f, -0.5f)     // 2
                    .verts(0, 2)
                    .vert(0.5f, -0.5f)      // 3
                    .end();

            materialRenderer.material = new TransformingMaterial(whiteShader) {
                protected void updateUniforms(Transform transform) {
                }
            };

            scene.createObjectWith(materialRenderer, meshRenderer);
        }*/

        // Test mesh object
        {
            MaterialRenderer materialRenderer = new MaterialRenderer();
            MeshRenderer meshRenderer = new MeshRenderer();

            meshRenderer.mesh
                    .start()
                    /*
                        1---0
                        |  /|
                        | / |
                        |/  |
                        2---3
                    
                        Triangles:  0, 1, 2
                                    0, 2, 3
                     */
                    .vert(0.5f, 0.5f).uv(1.0f, 0.0f)        // 0
                    .vert(-0.5f, 0.5f).uv(0.0f, 0.0f)       // 1
                    .vert(-0.5f, -0.5f).uv(0.0f, 1.0f)      // 2
                    .verts(0, 2)
                    .vert(0.5f, -0.5f).uv(1.0f, 1.0f)       // 3
                    .end();

            var texMat = new TexturedTransform(texShader);
            materialRenderer.material = texMat;
            try {
                texMat.texture = Texture.readStream(new ResourceLocation("radgame", "texture/test", "png").getStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            scene.createObjectWith(materialRenderer, meshRenderer);
        }
    }

    @Override
    public void onRemove() {
        whiteShader.close();
    }

    private static void initShaders() throws IOException {
        whiteShader = Shader.builder()
                .setVertexShader(new ResourceLocation("radgame", "shader/vertShader", "glsl"))
                .setFragmentShader(new ResourceLocation("radgame", "shader/fragShader", "glsl"))
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
