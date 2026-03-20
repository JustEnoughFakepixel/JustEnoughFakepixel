package com.jef.justenoughfakepixel.features.invbuttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

public class InvButtonIconRenderer {

    private static final HashMap<String, ItemStack> skullMap = new HashMap<>();

    private InvButtonIconRenderer() {}

    public static void renderIcon(String icon, int x, int y) {
        if (icon == null || icon.isEmpty()) return;

        if (icon.startsWith("extra:")) {
            String name = icon.substring("extra:".length());
            ResourceLocation loc = new ResourceLocation("justenoughfakepixel",
                    "invbuttons/extraicons/" + name + ".png");
            Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
            GlStateManager.color(1, 1, 1, 1);
            drawTexturedRect(x, y, 16, 16);
        } else {
            ItemStack stack = getStack(icon);
            if (stack == null) return;

            float scale = icon.startsWith("skull:") ? 1.2f : 1f;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 8, y + 8, 0);
            GlStateManager.scale(scale, scale, 1);
            GlStateManager.translate(-8, -8, 0);
            drawItemStack(stack, 0, 0);
            GlStateManager.popMatrix();
        }
    }

    public static ItemStack getStack(String icon) {
        if (icon == null || icon.isEmpty()) return null;
        if (icon.startsWith("extra:")) return null;

        if (icon.startsWith("skull:")) {
            String link = icon.substring("skull:".length());
            if (skullMap.containsKey(link)) return skullMap.get(link);

            ItemStack render     = new ItemStack(Items.skull, 1, 3);
            NBTTagCompound nbt   = new NBTTagCompound();
            NBTTagCompound owner = new NBTTagCompound();
            NBTTagCompound props = new NBTTagCompound();
            NBTTagList     texs  = new NBTTagList();
            NBTTagCompound tex0  = new NBTTagCompound();

            String uuid = UUID.nameUUIDFromBytes(link.getBytes()).toString();
            owner.setString("Id",   uuid);
            owner.setString("Name", uuid);

            String json = "{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/" + link + "\"}}}";
            tex0.setString("Value", Base64.getEncoder().encodeToString(json.getBytes()));
            texs.appendTag(tex0);
            props.setTag("textures", texs);
            owner.setTag("Properties", props);
            nbt.setTag("SkullOwner", owner);
            render.setTagCompound(nbt);

            skullMap.put(link, render);
            return render;
        }

        try {
            net.minecraft.item.Item item = net.minecraft.item.Item.getByNameOrId(icon);
            if (item != null) return new ItemStack(item);
            item = net.minecraft.item.Item.getByNameOrId("minecraft:" + icon.toLowerCase());
            if (item != null) return new ItemStack(item);
        } catch (Exception ignored) {}
        return null;
    }

    public static void drawItemStack(ItemStack stack, int x, int y) {
        if (stack == null) return;
        net.minecraft.client.renderer.entity.RenderItem ri = Minecraft.getMinecraft().getRenderItem();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        ri.renderItemAndEffectIntoGUI(stack, x, y);
        ri.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, stack, x, y, null);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
    }

    private static void drawTexturedRect(float x, float y, float w, float h) {
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.getInstance();
        net.minecraft.client.renderer.WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(7, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);
        wr.pos(x,     y + h, 0).tex(0, 1).endVertex();
        wr.pos(x + w, y + h, 0).tex(1, 1).endVertex();
        wr.pos(x + w, y,     0).tex(1, 0).endVertex();
        wr.pos(x,     y,     0).tex(0, 0).endVertex();
        tess.draw();
        GlStateManager.disableBlend();
    }
}