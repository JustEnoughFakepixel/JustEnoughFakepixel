package com.jef.justenoughfakepixel.features.storage.render;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.utils.TextRenderUtils;
import com.jef.justenoughfakepixel.features.misc.SearchBar;
import com.jef.justenoughfakepixel.features.storage.StorageManager;
import com.jef.justenoughfakepixel.features.storage.utils.SContainer;
import com.jef.justenoughfakepixel.features.storage.utils.Type;
import com.jef.justenoughfakepixel.utils.render.ItemRenderUtils;
import com.jef.justenoughfakepixel.utils.render.NineSliceUtils;
import com.jef.justenoughfakepixel.utils.render.ResolutionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.LinkedHashMap;

public class StorageRenderer extends Gui {

    private static final int PADDING = 5;
    private static final int ROW_SPACING = 8;
    private static final int INVENTORY_HEIGHT = 76;
    private static final float SCROLL_LENGTH = 0.2f;
    private static final int SEARCH_BAR_WIDTH = 200;
    private static final int SEARCH_BAR_HEIGHT = 20;
    private static final ResourceLocation CONTAINER_BG = new ResourceLocation("justenoughfakepixel", "textures/gui/storage_container_bg.png");
    private static final ResourceLocation SLOT_TEXTURE = new ResourceLocation("justenoughfakepixel", "textures/gui/storage_slot.png");
    private static final int NINE_SLICE_CORNER = 6;
    private static final int NINE_SLICE_SIZE = 18;
    private static final int SLOT_SIZE = 16;
    private static final int SLOTS_PER_ROW = 9;

    private final LinkedHashMap<String, SContainer> containers;
    private final java.util.HashMap<String, Boolean> searchCache = new java.util.HashMap<>();
    private final java.util.HashMap<String, Integer> containerHeightCache = new java.util.HashMap<>();
    private final java.util.HashMap<Integer, Integer> rowHeightCache = new java.util.HashMap<>();
    private int boxX, boxY, boxW, boxH;
    private int containerW, containerH;
    private int containersPerRow = 3;
    private int inventoryX, inventoryY;
    private int storageAreaH;
    private float scrollOffset;
    private float scrollTarget;
    private float scrollSpeed;
    private ItemStack hoveredItem;
    private int hoveredX = -1;
    private int hoveredY = -1;
    private GuiTextField searchField;
    private String searchText = "";
    private String lastSearchText = "";
    private int cachedVisibleCount = -1;
    private int cachedMaxScroll = -1;
    private int[] cachedGridStart = null;

    public StorageRenderer(LinkedHashMap<String, SContainer> containers) {
        this.containers = containers;
        this.scrollSpeed = JefConfig.feature.storage.scrollSpeed;
        initLayout();
        initSearchBar();
    }

    private void drawBackground() {
        int width = ResolutionUtils.getWidth();
        int height = ResolutionUtils.getHeight();
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        drawGradientRect(0, 0, width, height, -1072689136, -804253680);
        GlStateManager.disableBlend();
    }

    private void drawPanelBackground(int x, int y, int width, int height) {
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
        drawRect(x, y, x + width, y + height, 0xFF000000);
        GlStateManager.enableBlend();
        NineSliceUtils.draw(CONTAINER_BG, x, y, width, height, NINE_SLICE_CORNER, NINE_SLICE_SIZE);
    }

    private void initSearchBar() {
        int searchBarY = boxY - SEARCH_BAR_HEIGHT - 4;
        if (searchBarY < 4) searchBarY = 4;
        int searchBarX = boxX + (boxW - SEARCH_BAR_WIDTH) / 2;
        searchField = SearchBar.createStorageSearchBar(searchBarX, searchBarY, SEARCH_BAR_WIDTH);
    }

    private void initLayout() {
        int width = ResolutionUtils.getWidth();
        int height = ResolutionUtils.getHeight();

        containerW = 170;

        int minContainerWidth = containerW + PADDING;
        int maxContainersPerRow = Math.max(3, (width - 40) / minContainerWidth);
        containersPerRow = Math.min(maxContainersPerRow, 5);

        int maxContainerH = 120;
        for (SContainer container : containers.values()) {
            int h = getContainerDisplayHeight(container);
            if (h > maxContainerH) maxContainerH = h;
        }
        containerH = maxContainerH;

        inventoryX = (width - 162) / 2;
        inventoryY = height - INVENTORY_HEIGHT - 10;

        int searchBarReserved = SEARCH_BAR_HEIGHT + 8;
        int topMargin = 10;

        int maxStorageH = inventoryY - searchBarReserved - topMargin;
        int rows = 3;
        storageAreaH = Math.min((containerH + PADDING) * rows + PADDING * 2 + 20, maxStorageH);
        if (storageAreaH < 40) storageAreaH = 40;

        boxY = inventoryY - storageAreaH;
        if (boxY < topMargin + searchBarReserved) {
            boxY = topMargin + searchBarReserved;
            storageAreaH = inventoryY - boxY;
        }

        boxH = storageAreaH;

        boxW = (containerW + PADDING) * containersPerRow + PADDING * 2;
        int maxBoxW = width - 20;
        if (boxW > maxBoxW) {
            boxW = maxBoxW;
            containersPerRow = Math.max(1, (boxW - PADDING * 2) / (containerW + PADDING));
        }

        boxX = (width - boxW) / 2;
        if (boxX < 10) boxX = 10;
        if (boxX + boxW > width - 10) boxX = width - boxW - 10;
    }

    private boolean containerMatchesSearch(SContainer container) {
        if (searchText == null || searchText.isEmpty()) return true;

        String cacheKey = container.id + ":" + searchText;
        if (searchCache.containsKey(cacheKey)) {
            return searchCache.get(cacheKey);
        }

        for (int i = 0; i < container.slotCount; i++) {
            String displayName = container.getDisplayName(i);
            if (displayName != null && !displayName.isEmpty() && displayName.toLowerCase().contains(searchText)) {
                searchCache.put(cacheKey, true);
                return true;
            }
        }
        searchCache.put(cacheKey, false);
        return false;
    }

    private boolean itemMatchesSearch(ItemStack stack) {
        if (searchText == null || searchText.isEmpty()) return false;
        if (stack == null) return false;
        return stack.getDisplayName().toLowerCase().contains(searchText);
    }

    private int getContainerDisplayHeight(SContainer container) {
        String cacheKey = container.id + ":" + container.slotCount;
        if (containerHeightCache.containsKey(cacheKey)) {
            return containerHeightCache.get(cacheKey);
        }

        int rows = (int) Math.ceil(container.slotCount / 9.0);
        int titleHeight = 18;
        int bottomPadding = 4;
        int height = titleHeight + (rows * 16) + bottomPadding;

        containerHeightCache.put(cacheKey, height);
        return height;
    }

    public void render(int mouseX, int mouseY) {
        if (containers.isEmpty()) return;

        drawBackground();

        hoveredItem = null;
        hoveredX = -1;
        hoveredY = -1;

        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

        if (searchField != null) {
            searchField.updateCursorCounter();
        }

        SearchBar.drawStorageSearchBar(searchField);

        drawPanelBackground(boxX, boxY, boxW, boxH);

        scrollOffset += (scrollTarget - scrollOffset) * SCROLL_LENGTH;

        int scaleFactor = ResolutionUtils.getFactor();
        int inset = NINE_SLICE_CORNER;

        int scissorScreenTop = boxY + inset;
        int scissorScreenBottom = boxY + storageAreaH - inset;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((boxX + inset) * scaleFactor, (Minecraft.getMinecraft().displayHeight - scissorScreenBottom * scaleFactor), (boxW - inset * 2) * scaleFactor, (scissorScreenBottom - scissorScreenTop) * scaleFactor);

        String activeId = StorageManager.getActiveContainerId();

        for (SContainer container : containers.values()) {
            if (!containerMatchesSearch(container)) continue;
            boolean isActive = container.id.equals(activeId);
            drawContainer(mouseX, mouseY, container, fr, isActive);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        renderPlayerInventory(mouseX, mouseY);

        if (hoveredItem != null) {
            TextRenderUtils.drawItemTooltip(hoveredItem, hoveredX, hoveredY, fr);
        }
    }

    private void renderSlot(int x, int y, ItemStack stack, boolean matchesSearch, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(SLOT_TEXTURE);
        GlStateManager.color(matchesSearch && !searchText.isEmpty() ? 0.5f : 1f, 1.0f, 1f, 1f);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE);
        GlStateManager.color(1f, 1f, 1f, 1f);

        ItemRenderUtils.drawItemStackOverlay(stack, x, y);

        if (isHovering(mouseX, mouseY, x, y, SLOT_SIZE, SLOT_SIZE) && isSlotVisible(x, y)) {
            if (stack != null) {
                hoveredItem = stack;
                hoveredX = mouseX;
                hoveredY = mouseY;
            }
            drawSlotHighlight(x, y);
        }
    }

    private void drawSlotHighlight(int x, int y) {
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.colorMask(true, true, true, false);
        drawRect(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0x80FFFFFF);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.disableBlend();
    }

    private void renderPlayerInventory(int mouseX, int mouseY) {
        ItemStack[] playerItems = Minecraft.getMinecraft().thePlayer.inventory.mainInventory;

        drawInventoryBackground();
        renderInventorySlots(playerItems, mouseX, mouseY);

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.disableBlend();
    }

    private void drawInventoryBackground() {
        int invPanelX = inventoryX - 5;
        int invPanelY = inventoryY - 4;
        int invPanelW = 172;
        int invPanelH = INVENTORY_HEIGHT + 4 + 5;

        drawPanelBackground(invPanelX, invPanelY, invPanelW, invPanelH);
    }

    private void renderInventorySlots(ItemStack[] playerItems, int mouseX, int mouseY) {
        for (int i = 0; i < 27; i++) {
            renderSlot(inventoryX + (i % 9) * 18, inventoryY + (i / 9) * 18, playerItems[i + 9], false, mouseX, mouseY);
        }

        for (int i = 0; i < 9; i++) {
            renderSlot(inventoryX + i * 18, inventoryY + 58, playerItems[i], false, mouseX, mouseY);
        }
    }

    public void handleScroll(int dWheel) {
        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) return;

        float step = (containerH + PADDING) * scrollSpeed;
        scrollTarget -= dWheel > 0 ? step : -step;
        scrollTarget = Math.max(0, Math.min(scrollTarget, maxScroll));
    }

    private int getMaxScroll() {
        String newSearchText = SearchBar.getStorageSearchText().toLowerCase();
        if (!newSearchText.equals(lastSearchText)) {
            searchCache.clear();
            rowHeightCache.clear();
            cachedVisibleCount = -1;
            cachedMaxScroll = -1;
            lastSearchText = newSearchText;
        }
        searchText = newSearchText;

        if (cachedMaxScroll != -1) {
            return cachedMaxScroll;
        }

        int visibleCount = getVisibleContainerCount();
        int rowCount = (int) Math.ceil((double) visibleCount / containersPerRow);

        int totalHeight = 0;
        for (int i = 0; i < rowCount; i++) {
            totalHeight += getRowHeight(i);
            if (i < rowCount - 1) {
                totalHeight += ROW_SPACING;
            }
        }

        int visibleHeight = storageAreaH - 20;
        cachedMaxScroll = Math.max(0, totalHeight - visibleHeight);
        return cachedMaxScroll;
    }

    public boolean handleClick(int mouseX, int mouseY) {
        if (SearchBar.handleStorageMouseClick(searchField, mouseX, mouseY)) {
            return true;
        }

        int[] gridStart = getGridStart();
        int gridStartX = gridStart[0];
        int gridStartY = gridStart[1];

        int scrollPixels = (int) scrollOffset;

        int index = 0;
        for (SContainer container : containers.values()) {
            if (!containerMatchesSearch(container)) continue;

            int[] gridPos = getGridPosition(index);
            int xGrid = gridPos[0];
            int yGrid = gridPos[1];

            int yOffset = getRowYOffset(yGrid);

            int xStart = gridStartX + (xGrid * (containerW + PADDING));
            int yStart = gridStartY + yOffset - scrollPixels;

            int rw = containerW;
            int rh = getContainerDisplayHeight(container);

            if (isHovering(mouseX, mouseY, xStart, yStart, rw, rh)) {
                handleContainerClick(container);
                return true;
            }

            index++;
        }

        return false;
    }

    public boolean handleKeyTyped(char typedChar, int keyCode) {
        return SearchBar.handleStorageKeyTyped(searchField, typedChar, keyCode);
    }

    private void handleContainerClick(SContainer container) {
        if (container.id.equals(StorageManager.getActiveContainerId())) {
            return;
        }

        StorageManager.switchToContainer(container.id);
    }

    private int getRowHeight(int rowIndex) {
        if (rowHeightCache.containsKey(rowIndex)) {
            return rowHeightCache.get(rowIndex);
        }

        int maxHeight = 0;
        int startIndex = rowIndex * containersPerRow;
        int endIndex = Math.min(startIndex + containersPerRow, getVisibleContainerCount());

        int currentIndex = 0;
        for (SContainer container : containers.values()) {
            if (!containerMatchesSearch(container)) continue;

            if (currentIndex >= startIndex && currentIndex < endIndex) {
                int height = getContainerDisplayHeight(container);
                if (height > maxHeight) {
                    maxHeight = height;
                }
            }
            currentIndex++;
        }

        rowHeightCache.put(rowIndex, maxHeight);
        return maxHeight;
    }

    private int getVisibleContainerCount() {
        if (cachedVisibleCount != -1) {
            return cachedVisibleCount;
        }

        int count = 0;
        for (SContainer container : containers.values()) {
            if (containerMatchesSearch(container)) {
                count++;
            }
        }

        cachedVisibleCount = count;
        return count;
    }

    private int getRowYOffset(int rowIndex) {
        int offset = 0;
        for (int i = 0; i < rowIndex; i++) {
            offset += getRowHeight(i) + ROW_SPACING;
        }
        return offset;
    }

    private int[] getGridStart() {
        if (cachedGridStart != null) {
            return cachedGridStart;
        }

        int totalGridW = (containerW * containersPerRow) + (PADDING * (containersPerRow - 1));
        int gridStartX = boxX + (boxW - totalGridW) / 2;
        int gridStartY = boxY + 6 + PADDING;
        cachedGridStart = new int[]{gridStartX, gridStartY};
        return cachedGridStart;
    }

    private int[] getGridPosition(int index) {
        int xGrid = index % containersPerRow;
        int yGrid = index / containersPerRow;
        return new int[]{xGrid, yGrid};
    }

    private int getVisibleIndex(SContainer container) {
        int visibleIndex = 0;
        for (SContainer c : containers.values()) {
            if (c.id.equals(container.id)) {
                return visibleIndex;
            }
            if (containerMatchesSearch(c)) {
                visibleIndex++;
            }
        }
        return visibleIndex;
    }

    private void drawContainer(int mouseX, int mouseY, SContainer container, FontRenderer fr, boolean isActive) {
        ContainerRenderInfo renderInfo = calculateContainerRenderInfo(container);
        if (!renderInfo.isVisible) return;

        drawContainerBackground(renderInfo, isActive, mouseX, mouseY);
        drawContainerTitle(container, renderInfo, fr, isActive);
        drawContainerSlots(container, renderInfo, mouseX, mouseY);
    }

    private ContainerRenderInfo calculateContainerRenderInfo(SContainer container) {
        int index = getVisibleIndex(container);
        int[] gridPos = getGridPosition(index);
        int[] gridStart = getGridStart();

        int scrollPixels = (int) scrollOffset;
        int yOffset = getRowYOffset(gridPos[1]);

        int x = gridStart[0] + (gridPos[0] * (containerW + PADDING));
        int y = gridStart[1] + yOffset - scrollPixels;
        int width = containerW;
        int height = getContainerDisplayHeight(container);

        boolean isVisible = y + height > boxY + 10 && y < boxY + storageAreaH - 10;

        return new ContainerRenderInfo(x, y, width, height, isVisible);
    }

    private void drawContainerBackground(ContainerRenderInfo info, boolean isActive, int mouseX, int mouseY) {
        boolean hovering = isHovering(mouseX, mouseY, info.x, info.y, info.width, info.height);

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableLighting();

        if (isActive) {
            GlStateManager.color(1.2f, 1.2f, 0.8f, 1f);
        } else if (hovering) {
            GlStateManager.color(1.3f, 1.3f, 1.3f, 1f);
        }

        NineSliceUtils.draw(CONTAINER_BG, info.x, info.y, info.width, info.height, NINE_SLICE_CORNER, NINE_SLICE_SIZE);
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    private void drawContainerTitle(SContainer container, ContainerRenderInfo info, FontRenderer fr, boolean isActive) {
        String title = buildContainerTitle(container, isActive);
        drawCenteredString(fr, title, info.x + info.width / 2, info.y + 4, Color.WHITE.getRGB());
    }

    private String buildContainerTitle(SContainer container, boolean isActive) {
        String baseTitle = container.type == Type.ECHEST ? "§6Ender Chest " + container.page : "§aBackpack " + container.page;

        if (isActive) {
            baseTitle = "§e§l» §r" + baseTitle + " §e§l«";
        }

        if (container.locked) {
            baseTitle += " §c(Locked)";
        }

        if (container.empty) {
            baseTitle += " §7(Empty)";
        }

        return baseTitle;
    }

    private void drawContainerSlots(SContainer container, ContainerRenderInfo info, int mouseX, int mouseY) {
        int gridWidth = SLOT_SIZE * SLOTS_PER_ROW;
        int startX = info.x + (info.width - gridWidth) / 2;
        int startY = info.y + 16;

        GlStateManager.enableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.disableLighting();

        for (int i = 0; i < container.slotCount; i++) {
            int col = i % SLOTS_PER_ROW;
            int row = i / SLOTS_PER_ROW;
            int xPos = startX + (col * SLOT_SIZE);
            int yPos = startY + (row * SLOT_SIZE);
            ItemStack stack = container.getStack(i);
            renderSlot(xPos, yPos, stack, itemMatchesSearch(stack), mouseX, mouseY);
        }

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
    }

    private boolean isHovering(int mouseX, int mouseY, int xStart, int yStart, int width, int height) {
        return mouseX > xStart && mouseX < xStart + width && mouseY > yStart && mouseY < yStart + height;
    }

    private boolean isSlotVisible(int slotX, int slotY) {
        int slotEndX = slotX + SLOT_SIZE;
        int slotEndY = slotY + SLOT_SIZE;

        int inset = NINE_SLICE_CORNER;
        int storageLeft = boxX + inset;
        int storageRight = boxX + boxW - inset;
        int storageTop = boxY + inset;
        int storageBottom = boxY + storageAreaH - inset;

        int invLeft = inventoryX;
        int invRight = inventoryX + 162;
        int invTop = inventoryY;
        int invBottom = inventoryY + INVENTORY_HEIGHT;

        boolean inStorageArea = slotX < storageRight && slotEndX > storageLeft &&
                slotY < storageBottom && slotEndY > storageTop;

        boolean inInventoryArea = slotX < invRight && slotEndX > invLeft &&
                slotY < invBottom && slotEndY > invTop;

        return inStorageArea || inInventoryArea;
    }

    public boolean isClickingPlayerInventory(int mouseX, int mouseY) {
        return mouseX >= inventoryX && mouseX < inventoryX + 162 && mouseY >= inventoryY && mouseY < inventoryY + INVENTORY_HEIGHT;
    }

    public boolean isMouseOverPlayerInventorySlot(net.minecraft.inventory.Slot slot, int mouseX, int mouseY) {
        int slotIndex = slot.getSlotIndex();

        if (slotIndex >= 0 && slotIndex < 9) {
            return checkSlotHover(mouseX, mouseY, inventoryX + slotIndex * 18, inventoryY + 58);
        }

        if (slotIndex >= 9 && slotIndex < 36) {
            int adjustedIndex = slotIndex - 9;
            return checkSlotHover(mouseX, mouseY, inventoryX + (adjustedIndex % 9) * 18, inventoryY + (adjustedIndex / 9) * 18);
        }

        return false;
    }

    public boolean isMouseOverActiveContainerSlot(net.minecraft.inventory.Slot slot, int mouseX, int mouseY) {
        String activeId = StorageManager.getActiveContainerId();
        if (activeId == null) return false;

        SContainer activeContainer = containers.get(activeId);
        if (activeContainer == null) return false;

        ContainerPosition pos = calculateContainerPosition(activeContainer);
        if (!pos.isVisible) return false;

        return checkActiveContainerSlotHover(slot, mouseX, mouseY, pos);
    }

    private boolean checkSlotHover(int mouseX, int mouseY, int slotX, int slotY) {
        return isHovering(mouseX, mouseY, slotX, slotY, SLOT_SIZE, SLOT_SIZE);
    }

    private ContainerPosition calculateContainerPosition(SContainer container) {
        int index = getVisibleIndex(container);
        int[] gridPos = getGridPosition(index);
        int[] gridStart = getGridStart();

        int scrollPixels = (int) scrollOffset;
        int yOffset = getRowYOffset(gridPos[1]);

        int xStart = gridStart[0] + (gridPos[0] * (containerW + PADDING));
        int yStart = gridStart[1] + yOffset - scrollPixels;

        boolean isVisible = yStart + getContainerDisplayHeight(container) > boxY + 10 && yStart < boxY + storageAreaH - 10;

        return new ContainerPosition(xStart, yStart, isVisible);
    }

    private boolean checkActiveContainerSlotHover(net.minecraft.inventory.Slot slot, int mouseX, int mouseY, ContainerPosition pos) {
        int gridWidth = SLOT_SIZE * SLOTS_PER_ROW;
        int startX = pos.x + (containerW - gridWidth) / 2;
        int startY = pos.y + 16;

        int slotIndex = slot.getSlotIndex();
        int storageSlotIndex = slotIndex - 9;

        SContainer activeContainer = containers.get(StorageManager.getActiveContainerId());
        if (storageSlotIndex < 0 || storageSlotIndex >= activeContainer.slotCount) {
            return false;
        }

        int col = storageSlotIndex % SLOTS_PER_ROW;
        int row = storageSlotIndex / SLOTS_PER_ROW;
        int xPos = startX + (col * SLOT_SIZE);
        int yPos = startY + (row * SLOT_SIZE);

        return isHovering(mouseX, mouseY, xPos, yPos, SLOT_SIZE, SLOT_SIZE);
    }

    private static class ContainerRenderInfo {
        final int x, y, width, height;
        final boolean isVisible;

        ContainerRenderInfo(int x, int y, int width, int height, boolean isVisible) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.isVisible = isVisible;
        }
    }

    private static class ContainerPosition {
        final int x, y;
        final boolean isVisible;

        ContainerPosition(int x, int y, boolean isVisible) {
            this.x = x;
            this.y = y;
            this.isVisible = isVisible;
        }
    }
}
