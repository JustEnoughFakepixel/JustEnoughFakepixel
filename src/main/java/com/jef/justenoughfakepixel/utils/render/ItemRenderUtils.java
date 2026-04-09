package com.jef.justenoughfakepixel.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;


public class ItemRenderUtils {

    public static void renderItemIcon(Minecraft mc, ItemStack stack, int x, int y, int size) {
        if (stack == null) return;
        
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(size / 16f, size / 16f, 1f);
        mc.getRenderItem().renderItemIntoGUI(stack, 0, 0);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
    }

    public static void renderItemIcon(Minecraft mc, ItemStack stack, int x, int y) {
        renderItemIcon(mc, stack, x, y, 16);
    }

    public static void drawItemStack(ItemStack stack, int x, int y) {
        if (stack == null) return;
        
        Minecraft mc = Minecraft.getMinecraft();
        net.minecraft.client.renderer.entity.RenderItem ri = mc.getRenderItem();
        FontRenderer fr = mc.fontRendererObj;
        
        RenderHelper.enableGUIStandardItemLighting();
        ri.zLevel = -145;
        ri.renderItemAndEffectIntoGUI(stack, x, y);
        ri.renderItemOverlayIntoGUI(fr, stack, x, y, null);
        ri.zLevel = 0;
        RenderHelper.disableStandardItemLighting();
    }

    public static void renderItemWithEffects(Minecraft mc, ItemStack stack, int x, int y) {
        if (stack == null) return;
        
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
    }
}
