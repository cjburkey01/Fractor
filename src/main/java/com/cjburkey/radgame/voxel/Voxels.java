package com.cjburkey.radgame.voxel;

import com.cjburkey.radgame.ResourceLocation;
import com.cjburkey.radgame.game.GameManager;
import com.cjburkey.radgame.game.VoxelTypeRegisterEvent;
import com.cjburkey.radgame.mesh.Mesh;
import com.cjburkey.radgame.world.VoxelState;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

/**
 * Created by CJ Burkey on 2019/03/08
 */
public final class Voxels {

    private static final ObjectOpenHashSet<Voxel> voxels = new ObjectOpenHashSet<>();

    public static final Voxel AIR = register(new Voxel(ResourceLocation.fromString("radgame:voxel/air", false)) {
        public void generateMesh(Mesh.MeshBuilder mesh, VoxelState voxelState) {
        }
    });
    public static final Voxel STONE = register(new TexturedSquareVoxel("radgame:voxel/stone", "radgame:texture/voxel/stone.png"));
    public static final Voxel DIRT = register(new TexturedSquareVoxel("radgame:voxel/dirt", "radgame:texture/voxel/dirt.png"));
    public static final Voxel GRASS = register(new VoxelGrass());

    private static boolean init = false;

    public static void init() {
        if (init) return;
        init = true;
        GameManager.EVENT_BUS.addListener(VoxelTypeRegisterEvent.class, e -> e.registry.registerItems(voxels));
    }

    private static Voxel register(Voxel a) {
        voxels.add(a);
        return a;
    }

}
