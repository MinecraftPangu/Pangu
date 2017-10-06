package cn.mccraft.pangu.core.util.function.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface ItemOnLeftClickEntityCallback {
    boolean apply(ItemStack stack, EntityPlayer player, Entity entity);
}