package com.jef.justenoughfakepixel.features.general;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.mixins.FontRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class EnchantChromaRenderer {

    private static boolean chromaActive;
    private static boolean renderingShadow;

    private EnchantChromaRenderer() {}

    public static void beginRenderString(String text, boolean shadow) {
        renderingShadow = shadow;
        chromaActive = JefConfig.feature != null
                && JefConfig.feature.general.enchantChroma
                && text != null
                && (text.indexOf('§') >= 0)
                && (text.contains("§z") || text.contains("§Z"));
    }

    public static void changeTextColor() {
        if (!chromaActive || JefConfig.feature == null) {
            return;
        }

        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        int base = ChromaColour.specialToChromaRGB(JefConfig.feature.general.enchantPerfectColor);
        FontRendererAccessor accessor = (FontRendererAccessor) fr;
        int rgb = applyMode(base, accessor.jef$getPosX(), accessor.jef$getPosY());
        if (renderingShadow) {
            int a = (rgb >>> 24) & 255;
            int r = ((rgb >>> 16) & 255) / 4;
            int g = ((rgb >>> 8) & 255) / 4;
            int b = (rgb & 255) / 4;
            rgb = (a << 24) | (r << 16) | (g << 8) | b;
        }
        float r = ((rgb >> 16) & 255) / 255F;
        float g = ((rgb >> 8) & 255) / 255F;
        float b = (rgb & 255) / 255F;
        float a = ((rgb >> 24) & 255) / 255F;
        GlStateManager.color(r, g, b, a);
    }

    public static void endRenderString() {
        chromaActive = false;
        renderingShadow = false;
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private static int applyMode(int argb, float x, float y) {
        if (JefConfig.feature.general.enchantChromaMode == 0) {
            return argb;
        }
        float size = Math.max(1F, JefConfig.feature.general.enchantChromaSize);
        float shift = ((x + y) / size) % 1F;
        int a = (argb >>> 24) & 255;
        int r = (argb >>> 16) & 255;
        int g = (argb >>> 8) & 255;
        int b = argb & 255;
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        hsb[0] = (hsb[0] + shift) % 1F;
        return (a << 24) | (Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0x00FFFFFF);
    }
}
