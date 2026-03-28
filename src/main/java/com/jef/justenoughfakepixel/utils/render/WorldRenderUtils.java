package com.jef.justenoughfakepixel.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public final class WorldRenderUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private WorldRenderUtils() {}

    public static void drawEspBox(double x, double y, double z, Color color) {
        drawEspBox(x, y, z,
                color.getRed()   / 255f,
                color.getGreen() / 255f,
                color.getBlue()  / 255f,
                color.getAlpha() / 255f);
    }

    public static void drawEspBox(double x, double y, double z, float r, float g, float b, float a) {
        final double[][] edges = {
                {0,0,0,1,0,0},{0,0,1,1,0,1},{0,0,0,0,0,1},{1,0,0,1,0,1},
                {0,1,0,1,1,0},{0,1,1,1,1,1},{0,1,0,0,1,1},{1,1,0,1,1,1},
                {0,0,0,0,1,0},{1,0,0,1,1,0},{0,0,1,0,1,1},{1,0,1,1,1,1}
        };

        int ri = (int)(r * 255), gi = (int)(g * 255), bi = (int)(b * 255), ai = (int)(a * 255);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (double[] e : edges) {
            wr.pos(x + e[0], y + e[1], z + e[2]).color(ri, gi, bi, ai).endVertex();
            wr.pos(x + e[3], y + e[4], z + e[5]).color(ri, gi, bi, ai).endVertex();
        }
        tess.draw();
    }

    public static void drawTracer(Vec3 target, float partialTicks, Color color) {
        if (mc.thePlayer == null) return;

        double vx = mc.getRenderManager().viewerPosX;
        double vy = mc.getRenderManager().viewerPosY;
        double vz = mc.getRenderManager().viewerPosZ;

        Vec3 eyes = mc.thePlayer.getPositionEyes(partialTicks);

        int ri = color.getRed(), gi = color.getGreen(), bi = color.getBlue(), ai = color.getAlpha();

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth();
        GL11.glDepthMask(false);
        GL11.glLineWidth(2f);

        GL11.glPushMatrix();
        GL11.glTranslated(-vx, -vy, -vz);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(eyes.xCoord, eyes.yCoord, eyes.zCoord).color(ri, gi, bi, ai).endVertex();
        wr.pos(target.xCoord, target.yCoord, target.zCoord).color(ri, gi, bi, ai).endVertex();
        tess.draw();

        GL11.glPopMatrix();

        GL11.glDepthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GL11.glLineWidth(1f);
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    public static void drawTextInWorld(String text, double x, double y, double z) {
        if (mc.fontRendererObj == null) return;
        int w = mc.fontRendererObj.getStringWidth(net.minecraft.util.StringUtils.stripControlCodes(text));
        float scale = 0.04f;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0f, 1f, 0f);
        GL11.glRotatef( mc.getRenderManager().playerViewX, 1f, 0f, 0f);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        mc.fontRendererObj.drawStringWithShadow(text, -w / 2f, 0f, 0xFFFFFF);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    public static void beginWorldRender(float lineWidth) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth();
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        GlStateManager.disableCull();
    }

    public static void endWorldRender() {
        GL11.glDepthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GL11.glLineWidth(1f);
        GlStateManager.color(1f, 1f, 1f, 1f);
    }
}