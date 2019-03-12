package com.cjburkey.radgame.game;

import com.cjburkey.radgame.util.registry.RegisterEvent;
import com.cjburkey.radgame.util.registry.Registry;
import com.cjburkey.radgame.voxel.world.Voxel;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings("WeakerAccess")
public class VoxelTypeRegisterEvent extends RegisterEvent<Voxel> {

    public VoxelTypeRegisterEvent(Registry<Voxel> registry) {
        super(registry);
    }

}
