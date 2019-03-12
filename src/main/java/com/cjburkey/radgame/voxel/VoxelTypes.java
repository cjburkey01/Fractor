package com.cjburkey.radgame.voxel;

import com.cjburkey.radgame.game.GameManager;
import com.cjburkey.radgame.game.VoxelTypeRegisterEvent;
import com.cjburkey.radgame.voxel.world.Voxel;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

/**
 * Created by CJ Burkey on 2019/03/08
 */
public final class VoxelTypes {

    private static final ObjectOpenHashSet<Voxel> voxels = new ObjectOpenHashSet<>();

    public static final Voxel STONE = add(new TexturedSquareVoxel("radgame:voxel/stone", "radgame:texture/voxel/stone.png"));
    public static final Voxel DIRT = add(new TexturedSquareVoxel("radgame:voxel/dirt", "radgame:texture/voxel/dirt.png"));
    public static final Voxel GRASS = add(new TexturedSquareVoxel("radgame:voxel/grass", "radgame:texture/voxel/grass.png"));

    private static boolean init = false;

    public static void init() {
        if (init) return;
        init = true;
        GameManager.EVENT_BUS.addListener(VoxelTypeRegisterEvent.class, e -> e.registry.registerItems(voxels));
    }

    private static Voxel add(Voxel a) {
        voxels.add(a);
        return a;
    }

}
