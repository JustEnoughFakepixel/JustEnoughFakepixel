package com.jef.justenoughfakepixel.features.itemlist;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.TextRenderUtils;
import com.jef.justenoughfakepixel.features.misc.SearchBar;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class ItemListOverlay {

    private static final ResourceLocation ITEM_MASK = new ResourceLocation("justenoughfakepixel", "textures/gui/item_mask.png");

    private static final Minecraft mc = Minecraft.getMinecraft();

    private int getSlotSize() {
        return (int) JefConfig.feature.misc.itemListScalePx; // e.g., 32
    }

    private static final int PAD = 4;
    private static final int MASK_PADDING = 1;
    private static final String MC_ITEM_PREFIX = "minecraft:";


    private final List<SkyblockItem> filtered = new ArrayList<>();
    private String lastQuery = null;
    private int scroll = 0;
    private int cols = 0;
    private int rows = 0;

    private static String stripColor(String s) {
        return s == null ? "" : s.replaceAll("(?i)§.", "");
    }

    private int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.misc.itemListBgColor);
    }

    @SubscribeEvent
    public void onDrawPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.gui instanceof GuiContainer)) return;
        if (!JefConfig.feature.misc.enableItemList) return;
        if (!SearchBar.isItemListMode()) return;

        GuiContainer gui = (GuiContainer) event.gui;
        ScaledResolution sr = new ScaledResolution(mc);
        int sw = sr.getScaledWidth();
        int sh = sr.getScaledHeight();

        int panelY = PAD;
        int panelH = sh - panelY - PAD;
        cols = Math.max(1, Math.round(JefConfig.feature.misc.itemListScale));
        int headerH = mc.fontRendererObj.FONT_HEIGHT + PAD * 2;
        rows = Math.max(1, (panelH - headerH) / getSlotSize());
        int usedW = cols * getSlotSize();
        int panelX = sw - usedW - PAD;

        syncFiltered();

        int bgColor = ChromaColour.specialToChromaRGB(JefConfig.feature.misc.itemListBgColor);
        Gui.drawRect(panelX, panelY, panelX + usedW, panelY + panelH, bgColor);

        mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GOLD + "Item List " + EnumChatFormatting.DARK_GRAY + filtered.size(), panelX + PAD, panelY + PAD, 0xFFFFFFFF);

        int gridTop = panelY + headerH;

        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();

        int slotSize = getSlotSize();
        int startIdx = scroll * cols;
        int endIdx = Math.min(startIdx + cols * rows, filtered.size());

        for (int i = startIdx; i < endIdx; i++) {
            int slotIndex = i - startIdx;
            int col = slotIndex % cols;
            int row = slotIndex / cols;
            int x = panelX + col * slotSize;
            int y = gridTop + row * slotSize;

            mc.getTextureManager().bindTexture(ITEM_MASK);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            int maskPadding = MASK_PADDING;
            int maskSize = slotSize - maskPadding * 2;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
            Gui.drawScaledCustomSizeModalRect(x + maskPadding, y + maskPadding, 0, 0, 16, 16, maskSize, maskSize, 16, 16);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();

            ItemStack stack = buildStack(filtered.get(i));
            if (stack != null) {
                float itemScale = maskSize / 16f;
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + maskPadding, y + maskPadding, 0);
                GlStateManager.scale(itemScale, itemScale, 1f);
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
                GlStateManager.popMatrix();
            }
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();

        int totalRows = (int) Math.ceil(filtered.size() / (double) cols);
        if (totalRows > rows) {
            int trackH = panelH - headerH;
            int thumbH = Math.max(10, trackH * rows / totalRows);
            int thumbY = gridTop + scroll * (trackH - thumbH) / Math.max(1, totalRows - rows);
            int barX = panelX + usedW - 3;
            Gui.drawRect(barX, gridTop, barX + 3, gridTop + trackH, 0xFF444444);
            Gui.drawRect(barX, thumbY, barX + 3, thumbY + thumbH, 0xFF888888);
        }

        int mx = event.mouseX, my = event.mouseY;
        List<String> pendingTooltip = null;

        if (mx >= panelX && mx < panelX + usedW && my >= gridTop) {
            int col = (mx - panelX) / getSlotSize();
            int row = (my - gridTop) / getSlotSize();
            if (row >= 0 && row < rows && col >= 0 && col < cols) {
                int idx = (scroll * cols) + row * cols + col;
                if (idx < filtered.size()) pendingTooltip = buildTooltip(filtered.get(idx));
            }
        }
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();

        if (pendingTooltip != null) TextRenderUtils.drawHoveringText(pendingTooltip, mx, my, mc.fontRendererObj);

        GlStateManager.enableDepth();
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!(event.gui instanceof GuiContainer)) return;
        if (!JefConfig.feature.misc.enableItemList) return;
        if (!SearchBar.isItemListMode()) return;

        int wheel = Mouse.getEventDWheel();
        if (wheel == 0) return;

        GuiContainer gui = (GuiContainer) event.gui;
        ScaledResolution sr = new ScaledResolution(mc);
        int panelX = gui.guiLeft + gui.xSize + PAD;

        int mx = Mouse.getX() * sr.getScaledWidth() / mc.displayWidth;
        if (mx < panelX) return;

        int totalRows = (int) Math.ceil(filtered.size() / (double) Math.max(1, cols));
        int maxScroll = Math.max(0, totalRows - rows);
        int newScroll = Math.max(0, Math.min(maxScroll, scroll - (wheel > 0 ? 1 : -1)));

        if (newScroll != scroll) scroll = newScroll;

        event.setCanceled(true);
    }

    private void syncFiltered() {
        String query = SearchBar.isItemListMode() ? SearchBar.getSearchText().toLowerCase().trim() : "";
        if (query.equals(lastQuery)) return;
        lastQuery = query;
        scroll = 0;

        filtered.clear();
        for (SkyblockItem item : ItemRegistry.skyblockItems.values()) {
            if (query.isEmpty() || stripColor(item.displayName).toLowerCase().contains(query) || item.skyblockID.toLowerCase().contains(query)) {
                filtered.add(item);
            }
        }
        filtered.sort((a, b) -> stripColor(a.displayName).compareToIgnoreCase(stripColor(b.displayName)));
    }

    private ItemStack buildStack(SkyblockItem item) {
        try {
            Item base = Item.getByNameOrId(item.itemID.toLowerCase().replace(MC_ITEM_PREFIX, ""));
            if (base == null) return null;
            ItemStack stack = new ItemStack(base, 1, item.damage);
            if (item.NBT != null && !item.NBT.isEmpty()) {
                try {
                    stack.setTagCompound(JsonToNBT.getTagFromJson(item.NBT));
                } catch (Exception ignored) {
                }
            }
            stack.setStackDisplayName(item.displayName);
            return stack;
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> buildTooltip(SkyblockItem item) {
        List<String> lines = new ArrayList<>();
        lines.add(item.displayName);
        lines.add(EnumChatFormatting.DARK_GRAY + item.skyblockID);
        if (item.lore != null) lines.addAll(item.lore);
        return lines;
    }
}