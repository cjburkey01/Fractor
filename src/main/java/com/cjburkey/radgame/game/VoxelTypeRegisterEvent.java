package com.cjburkey.radgame.game;

import com.cjburkey.radgame.util.registry.RegisterEvent;
import com.cjburkey.radgame.util.registry.Registry;
import com.cjburkey.radgame.world.Voxel;

/**
 * Created by CJ Burkey on 2019/03/11
 */
public class VoxelTypeRegisterEvent extends RegisterEvent<Voxel> {

    VoxelTypeRegisterEvent(Registry<Voxel> registry) {
        super(registry);
    }

}
