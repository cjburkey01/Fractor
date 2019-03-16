package com.cjburkey.radgame.voxel;

import com.cjburkey.radgame.ResourceLocation;
import java.util.Objects;

/**
 * Created by CJ Burkey on 2019/03/11
 */
@SuppressWarnings("WeakerAccess")
public abstract class SingleTexturedVoxel extends Voxel implements ITexturedVoxel {

    private final ResourceLocation[] textureIds;
    private final ResourceLocation textureId;

    public SingleTexturedVoxel(final ResourceLocation id, final ResourceLocation textureId) {
        super(id);
        this.textureId = Objects.requireNonNull(textureId);
        textureIds = new ResourceLocation[] {textureId};
    }

    @Override
    public ResourceLocation[] getTextureIds() {
        return textureIds;
    }

    @Override
    public ResourceLocation getPrimaryTextureId() {
        return textureId;
    }

}
