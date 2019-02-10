package cn.mccraft.pangu.core.client.ui;

import cn.mccraft.pangu.core.util.NonNullList;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
@Accessors(chain = true)
public abstract class Screen extends GuiScreen {
    @Setter
    @Getter
    protected GuiScreen parentScreen;

    @Getter
    @Setter
    protected boolean drawDefaultBackground = true;

    @Getter
    @Setter
    protected boolean doesGuiPauseGame = false;

    @Delegate(excludes = Component.class)
    protected Container rootContainer;

    public Screen() {
    }

    @Override
    public void initGui() {
        rootContainer = new Container(width, height);
        rootContainer.setScreen(this);
        init();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (drawDefaultBackground) drawDefaultBackground();
        draw();
        rootContainer.onDraw(partialTicks, mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        rootContainer.onMousePressed(mouseButton, mouseX, mouseY);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return doesGuiPauseGame;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        rootContainer.onMouseReleased(mouseX, mouseY);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) this.mc.setIngameFocus();
        } else {
            rootContainer.onKeyTyped(typedChar, keyCode);
        }
    }

    public void open() {
        setParentScreen(Minecraft.getMinecraft().currentScreen);
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    public void closeScreen() {
        Minecraft.getMinecraft().displayGuiScreen(parentScreen);
    }

    /**
     * Add your components in this method
     */
    public abstract void init();

    /**
     * Draw background or else
     */
    public abstract void draw();
}