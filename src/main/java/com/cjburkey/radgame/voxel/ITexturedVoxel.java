package com.cjburkey.radgame.voxel;

import com.cjburkey.radgame.ResourceLocation;

/**
 * Created by CJ Burkey on 2019/03/11
 */
public interface ITexturedVoxel {

    ResourceLocation[] getTextureIds();

    ResourceLocation getPrimaryTextureId();

}
