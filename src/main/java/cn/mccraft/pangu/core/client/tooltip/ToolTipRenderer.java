package cn.mccraft.pangu.core.client.tooltip;

import cn.mccraft.pangu.core.client.input.BindKeyPress;
import cn.mccraft.pangu.core.loader.AutoWired;
import cn.mccraft.pangu.core.util.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;

import static cn.mccraft.pangu.core.client.PGClient.PG_TOOLTIPS_TEXTURE;

@SideOnly(Side.CLIENT)
@AutoWired(registerCommonEventBus = true)
public enum ToolTipRenderer {
    INSTANCE;

    protected String text = "";
    protected int time;
    protected int duration;
    protected ToolTipStyle style = ToolTipStyle.NONE;
    protected boolean animated;
    private long animationStart = -1;
    private boolean endAnimation = false;
    private long endAnimationStart = -1;

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            if (time <= 0 && !endAnimation) return;
            if (Minecraft.getMinecraft().ingameGUI == null) return;

            int scaledWidth = event.getResolution().getScaledWidth();
            int scaledHeight = event.getResolution().getScaledHeight();

            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

            int textWidth = fontRenderer.getStringWidth(this.text);
            int extendWidth = 10;
            int width = textWidth + extendWidth;

            int x = -textWidth / 2;

            GlStateManager.pushMatrix();
            GlStateManager.translate(scaledWidth / 2F, scaledHeight - 62 + 62 * getVisibility(), 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            fontRenderer.drawString(this.text, -textWidth / 2F, style.getTextOffset(), style.getFontColor(), style.hasFontShadow());

            if (style != null && style != ToolTipStyle.NONE) {
                textureManager.bindTexture(PG_TOOLTIPS_TEXTURE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                RenderUtils.drawTexturedModalRect(
                        x - extendWidth / 2F, 0,
                        style.getX(), style.getY(),
                        width / 2F, style.getHeight());

                RenderUtils.drawTexturedModalRect(
                        x + textWidth / 2F, 0,
                        style.getWidth() - width / 2F, style.getY(),
                        width / 2F, style.getHeight());
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (time > 0) {
            time--;
            if (time == 0) endAnimation = true;
        }
    }

    private float getVisibility() {
        if (!animated) return 0;
        long systemTime = Minecraft.getSystemTime();
        long delta = systemTime - animationStart;
        if (delta <= 400F) {
            float f = MathHelper.clamp((float) (systemTime - this.animationStart) / 400F, 0.0F, 1.0F);
            return 1 - f * f;
        }
        if (endAnimation) {
            if (endAnimationStart == -1) endAnimationStart = systemTime;
            float f = MathHelper.clamp((float) (systemTime - this.endAnimationStart) / 400F, 0.0F, 1.0F);
            if (f >= 1) {
                endAnimation = false;
                endAnimationStart = -1;
            }
            return f * f;
        }
        return 0;
    }

    /**
     * Display ToolTip, will replace the previous one if exists.
     * ToolTip text width (from {@code FontRenderer.getStringWidth(String text)})
     * cannot be longer than 387. If text width is longer than 387, the beyond part
     * will be cut.
     */
    public void set(ToolTip toolTip) {
        text = fixStringWidth(toolTip.getText());
        style = toolTip.getStyle();
        time = toolTip.getDuration();
        duration = toolTip.getDuration();
        animated = toolTip.isAnimated();
        if (animated) {
            animationStart = Minecraft.getSystemTime();
        }
    }

    /**
     * Cut string into a displayable size
     */
    @Nonnull
    public String fixStringWidth(@Nonnull String text) {
        return fixStringWidth(text, 1);
    }

    /**
     * Cut string into a displayable size
     */
    @Nonnull
    public String fixStringWidth(@Nonnull String text, int level) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        if (fontRenderer.getStringWidth(text) > 387) {
            if (text.length() > 129)
                return fixStringWidth(text.substring(0, 129));
            else
                return fixStringWidth(text.substring(0, text.length() - level), level + 1);
        }
        return text;
    }
}