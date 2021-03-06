package cn.mccraft.pangu.core.client.render.entity;

import lombok.Setter;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderEntityLivingModel extends RenderLiving<EntityLiving> {
    @Setter
    private ITextureProvider textureProvider;

    public RenderEntityLivingModel(RenderManager renderManager, ModelBase modelBase, float shadowSize, ITextureProvider textureProvider) {
        super(renderManager, modelBase, shadowSize);
        this.textureProvider = textureProvider;
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(@Nonnull EntityLiving entity) {
        return textureProvider == null ? null : textureProvider.getTexture(entity);
    }
}
