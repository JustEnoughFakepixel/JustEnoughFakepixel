package com.jef.justenoughfakepixel.utils.render;

import com.jef.justenoughfakepixel.events.RenderEntityModelEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public final class EntityHighlight {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private EntityHighlight() {
    }

    public static void renderEntityOutline(RenderEntityModelEvent event, Color color) {
        EntityLivingBase entity = event.getEntity();

        float gamma = mc.gameSettings.gammaSetting;
        boolean fancy = mc.gameSettings.fancyGraphics;
        mc.gameSettings.gammaSetting = Float.MAX_VALUE;
        mc.gameSettings.fancyGraphics = false;

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        GlStateManager.scale(1.03f, 1.03f, 1.03f);

        event.getModel().render(entity, event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();

        mc.gameSettings.gammaSetting = gamma;
        mc.gameSettings.fancyGraphics = fancy;
    }
}