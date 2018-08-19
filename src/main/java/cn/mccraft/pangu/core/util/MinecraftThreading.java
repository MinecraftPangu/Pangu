package cn.mccraft.pangu.core.util;

import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public interface MinecraftThreading {
    @SideOnly(Side.CLIENT)
    static IThreadListener client() {
        return Minecraft.getMinecraft();
    }

    @SideOnly(Side.SERVER)
    static IThreadListener server() {
        return FMLServerHandler.instance().getServer();
    }

    static IThreadListener safe() {
        return Environment.side().isServer() ? server() : client();
    }

    static ListenableFuture<Object> submit(Runnable runnable) {
        return safe().addScheduledTask(runnable);
    }
}