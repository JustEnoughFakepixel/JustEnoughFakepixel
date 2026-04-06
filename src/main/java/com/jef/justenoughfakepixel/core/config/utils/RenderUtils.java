package com.jef.justenoughfakepixel.core.config.utils;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public final class RenderUtils {

    private RenderUtils() {}

    public static void drawWorldCircle(double radius, int steps, float lineWidth, float r, float g, float b, float a) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableDepth();
        GL11.glLineWidth(lineWidth);
        GL11.glColor4f(r, g, b, a);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        for (int i = 0; i <= steps; i++) {
            double angle = (Math.PI * 2) * i / steps;
            wr.pos(Math.cos(angle) * radius, 0, Math.sin(angle) * radius).endVertex();
        }
        tess.draw();

        GL11.glColor4f(1f, 1f, 1f, 1f);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void drawFloatingRectDark(int x, int y, int width, int height) {
        drawFloatingRectDark(x, y, width, height, true);
    }

    public static void drawFloatingRectDark(int x, int y, int width, int height, boolean shadow) {
        int alpha = OpenGlHelper.isFramebufferEnabled() ? 0xf0000000 : 0xff000000;
        int main  = alpha | 0x202026;
        int light = 0xff303036;
        int dark  = 0xff101016;
        Gui.drawRect(x,           y,            x + 1,         y + height,     light);
        Gui.drawRect(x + 1,       y,            x + width,     y + 1,          light);
        Gui.drawRect(x + width-1, y + 1,        x + width,     y + height,     dark);
        Gui.drawRect(x + 1,       y + height-1, x + width-1,   y + height,     dark);
        Gui.drawRect(x + 1,       y + 1,        x + width-1,   y + height-1,   main);
        if (shadow) {
            Gui.drawRect(x + width, y + 2,  x + width + 2, y + height + 2, 0x70000000);
            Gui.drawRect(x + 2,     y + height, x + width, y + height + 2, 0x70000000);
        }
    }

    public static void drawFloatingRect(int x, int y, int width, int height) {
        drawFloatingRectWithAlpha(x, y, width, height, 0xFF, true);
    }

    public static void drawFloatingRectWithAlpha(int x, int y, int width, int height, int alpha, boolean shadow) {
        int main  = (alpha << 24) | 0xc0c0c0;
        int light = (alpha << 24) | 0xf0f0f0;
        int dark  = (alpha << 24) | 0x909090;
        Gui.drawRect(x,           y,            x + 1,       y + height,   light);
        Gui.drawRect(x + 1,       y,            x + width,   y + 1,        light);
        Gui.drawRect(x + width-1, y + 1,        x + width,   y + height,   dark);
        Gui.drawRect(x + 1,       y + height-1, x + width-1, y + height,   dark);
        Gui.drawRect(x + 1,       y + 1,        x + width-1, y + height-1, main);
        if (shadow) {
            Gui.drawRect(x + width, y + 2,  x + width + 2, y + height + 2, (alpha * 3 / 5) << 24);
            Gui.drawRect(x + 2,     y + height, x + width, y + height + 2, (alpha * 3 / 5) << 24);
        }
    }

    public static void drawInnerBox(int left, int top, int width, int height) {
        Gui.drawRect(left,           top,          left + width,     top + height,     0x6008080E);
        Gui.drawRect(left,           top,          left + 1,         top + height,     0xff08080E);
        Gui.drawRect(left,           top,          left + width,     top + 1,          0xff08080E);
        Gui.drawRect(left + width-1, top,          left + width,     top + height,     0xff28282E);
        Gui.drawRect(left,           top + height-1, left + width,   top + height,     0xff28282E);
    }

    public static void drawTexturedRect(float x, float y, float width, float height) {
        drawTexturedRect(x, y, width, height, 0, 1, 0, 1);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, int filter) {
        drawTexturedRect(x, y, width, height, 0, 1, 0, 1, filter);
    }

    public static void drawTexturedRect(float x, float y, float width, float height,
                                        float uMin, float uMax, float vMin, float vMax) {
        drawTexturedRect(x, y, width, height, uMin, uMax, vMin, vMax, GL11.GL_NEAREST);
    }

    public static void drawTexturedRect(float x, float y, float width, float height,
                                        float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawTexturedRectNoBlend(x, y, width, height, uMin, uMax, vMin, vMax, filter);
        GlStateManager.disableBlend();
    }

    public static void drawTexturedRectNoBlend(float x, float y, float width, float height,
                                               float uMin, float uMax, float vMin, float vMax, int filter) {
        GlStateManager.enableTexture2D();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

        Tessellator t = Tessellator.getInstance();
        WorldRenderer wr = t.getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX);
        wr.pos(x,         y + height, 0).tex(uMin, vMax).endVertex();
        wr.pos(x + width, y + height, 0).tex(uMax, vMax).endVertex();
        wr.pos(x + width, y,          0).tex(uMax, vMin).endVertex();
        wr.pos(x,         y,          0).tex(uMin, vMin).endVertex();
        t.draw();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        final double f = 0.00390625;
        Tessellator t = Tessellator.getInstance();
        WorldRenderer wr = t.getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX);
        wr.pos(x,         y + height, 0).tex((textureX)         * f, (textureY + height) * f).endVertex();
        wr.pos(x + width, y + height, 0).tex((textureX + width) * f, (textureY + height) * f).endVertex();
        wr.pos(x + width, y,          0).tex((textureX + width) * f, (textureY)          * f).endVertex();
        wr.pos(x,         y,          0).tex((textureX)         * f, (textureY)          * f).endVertex();
        t.draw();
    }


    public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom,
                                        int startColor, int endColor) {
        float sA = (startColor >> 24 & 255) / 255f, sR = (startColor >> 16 & 255) / 255f;
        float sG = (startColor >>  8 & 255) / 255f, sB = (startColor       & 255) / 255f;
        float eA = (endColor   >> 24 & 255) / 255f, eR = (endColor   >> 16 & 255) / 255f;
        float eG = (endColor   >>  8 & 255) / 255f, eB = (endColor         & 255) / 255f;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);

        Tessellator t = Tessellator.getInstance();
        WorldRenderer wr = t.getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(right, top,    zLevel).color(sR, sG, sB, sA).endVertex();
        wr.pos(left,  top,    zLevel).color(sR, sG, sB, sA).endVertex();
        wr.pos(left,  bottom, zLevel).color(eR, eG, eB, eA).endVertex();
        wr.pos(right, bottom, zLevel).color(eR, eG, eB, eA).endVertex();
        t.draw();

        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static int renderStringTrimWidth(String str, boolean shadow, int x, int y, int width, int color, int maxLines) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        net.minecraft.client.gui.FontRenderer fr = mc.fontRendererObj;

        if (str == null || str.isEmpty()) return 0;

        String[] words = str.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int linesRendered = 0;
        int yOffset = 0;

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            int testWidth = fr.getStringWidth(testLine);

            if (testWidth > width && currentLine.length() > 0) {
                if (shadow) {
                    fr.drawStringWithShadow(currentLine.toString(), x, y + yOffset, color);
                } else {
                    fr.drawString(currentLine.toString(), x, y + yOffset, color);
                }
                yOffset += fr.FONT_HEIGHT;
                linesRendered++;

                if (maxLines > 0 && linesRendered >= maxLines) {
                    return yOffset;
                }

                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }

        if (currentLine.length() > 0) {
            if (shadow) {
                fr.drawStringWithShadow(currentLine.toString(), x, y + yOffset, color);
            } else {
                fr.drawString(currentLine.toString(), x, y + yOffset, color);
            }
            yOffset += fr.FONT_HEIGHT;
        }

        return yOffset;
    }
}