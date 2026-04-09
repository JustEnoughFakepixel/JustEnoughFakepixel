package com.jef.justenoughfakepixel.features.storage;

import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.utils.ColorUtils;
import com.jef.justenoughfakepixel.utils.render.ResolutionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StorageOverlay extends GuiScreen {

    private static final int BASE_BOX_WIDTH = 1536;
    private static final int BASE_BOX_HEIGHT = BASE_BOX_WIDTH * 9 / 16;
    private static final int BASE_CONTAINER_WIDTH = 450;
    private static final int BASE_CONTAINER_HEIGHT = BASE_CONTAINER_WIDTH * 9 / 16;
    private static final int BASE_PADDING = 30;
    public static boolean isHorizontal = true;
    public double scrollOffset = 0;
    public double offset = 0;
    public String activeContainer = "";
    private int boxX, boxY, boxWidth, boxHeight;
    private int cWidth, cHeight, pX, pY;
    private int startX, startY;
    private static double SCROLL_SPEED = 100;
    private double maxScroll = 0;
    private double totalElements = 0;
    private double visibleElements = 0;
    private boolean isDraggingScreen = false;
    private boolean isDraggingScrollbar = false;
    private int lastMouseX, lastMouseY;
    private int draggedDistance = 0;
    private int scrollbarDragOffset = 0;

    private int trackX, trackY, trackW, trackH;
    private int thumbX, thumbY, thumbW, thumbH;

    public static HashMap<String, StorageContainer> containers = new HashMap<>();
    public static List<ItemStack> searchedItems = new ArrayList<>();
    public static void openOverlay(ContainerChest chest) {
        containers.clear();
        searchedItems.clear();

        JefMod.logger.info("Searching Items");
        isHorizontal = JefConfig.feature.storage.horizontalScroll;
        SCROLL_SPEED = 100 * (JefConfig.feature.storage.scrollSpeed);

        int rows = isHorizontal ? 3 : 6;
        int cols = isHorizontal ? 8 : 3;

        int validItemIndex = 0;

        int echestCount = 0;
        int backpackCount = 0;

        for (int i = 9; i < 18; i++) {
            ItemStack stack = chest.getSlot(i).getStack();
            if (stack == null) continue;

            String name = ColorUtils.stripColor(stack.getDisplayName());
            if (name.startsWith("Locked")) continue;

            searchedItems.add(stack);
            echestCount++;

            int xGrid = isHorizontal ? (validItemIndex / rows) : (validItemIndex % cols);
            int yGrid = isHorizontal ? (validItemIndex % rows) : (validItemIndex / cols);

            containers.put("echest-" + echestCount,
                    new StorageContainer(new HashMap<>(), ContainerType.ECHEST, echestCount, xGrid, yGrid));

            validItemIndex++;
        }

        for (int i = 27; i < 45; i++) {
            ItemStack stack = chest.getSlot(i).getStack();
            if (stack == null) continue;

            String name = ColorUtils.stripColor(stack.getDisplayName());
            if (name.startsWith("Empty")) continue;

            searchedItems.add(stack);
            backpackCount++;

            int xGrid = isHorizontal ? (validItemIndex / rows) : (validItemIndex % cols);
            int yGrid = isHorizontal ? (validItemIndex % rows) : (validItemIndex / cols);

            containers.put("bag-" + backpackCount,
                    new StorageContainer(new HashMap<>(), ContainerType.BACKPACK, backpackCount, xGrid, yGrid));

            validItemIndex++;
        }

        if (searchedItems.isEmpty()) return;

        JefMod.logger.info("Parsed " + searchedItems.size() + " items.");
        JefConfig.screenToOpen = new StorageOverlay();
    }

    public static void updateContainer(ContainerChest container,boolean echest) {
        String name = container.getLowerChestInventory().getDisplayName().getUnformattedText();

        if(echest) {
            char page = name.charAt((name.indexOf(')') - 1));
            int pageID;
            try {
                pageID = Integer.parseInt(String.valueOf(page));
            } catch (NumberFormatException e) {
                JefMod.logger.info("Error While Trying to Parse " + page);
                return;
            }
            if (pageID < 0) return;
            String id = "echest-"+ pageID;
            StorageContainer storage = containers.get(id);
            if(storage == null) return;
            storage.items = parseItems(container);

            ItemStack stack = container.getSlot(0).getStack();
            if(stack == null || stack.getItem() != Item.getItemFromBlock(Blocks.barrier)){
                StorageListener.scanned.put(id,false);
            }else {
                StorageListener.scanned.put(id,true);
            }

            JefMod.logger.info("Put " + storage.items.size() + " in container id " + id);
        }else {
            String[] words = name.split(" ");
            String temp = words[words.length - 1];
            String pageStr = temp.split("/")[0];
            int pageID;
            try{
                pageID = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                JefMod.logger.info("Error While Trying to Parse " + pageStr);
                return;
            }
            if (pageID < 0) return;
            String id = "bag-"+(pageID - 1);
            StorageContainer storage = containers.get(id);
            if(storage == null) return;
            storage.items = parseItems(container);

            ItemStack stack = container.getSlot(0).getStack();
            if(stack == null || stack.getItem() != Item.getItemFromBlock(Blocks.barrier)){
                StorageListener.scanned.put(id,false);
            }else {
                StorageListener.scanned.put(id,true);
            }

            JefMod.logger.info("Put " + storage.items.size() + " in container id " + id);
        }
    }

    public static HashMap<Integer,String> parseItems(ContainerChest chest){
        HashMap<Integer,String> data = new HashMap<>();
        for(int i = 9;i < chest.getLowerChestInventory().getSizeInventory();i++){
            ItemStack stack = chest.getSlot(i).getStack();
            if(stack == null){
                continue;
            }
            NBTTagCompound compound = new NBTTagCompound();
            stack.writeToNBT(compound);
            String item = compound.toString();
            data.put(i,item);
            JefMod.logger.info(i + ": " + item.substring(0,100));
        }
        return data;
    }

    public static ItemStack getItemFromNBT(String itemString) {
        if (itemString == null || itemString.isEmpty()) {
            return null;
        }
        try {
            NBTTagCompound nbt = JsonToNBT.getTagFromJson(itemString);

            return ItemStack.loadItemStackFromNBT(nbt);

        } catch (NBTException e) {
            System.err.println("Failed to parse ItemStack from string: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        boxWidth = (int) ResolutionUtils.getXStatic(BASE_BOX_WIDTH);
        boxHeight = (int) ResolutionUtils.getYStatic(BASE_BOX_HEIGHT);
        boxX = (ResolutionUtils.getWidth() - boxWidth) / 2;
        boxY = (ResolutionUtils.getHeight() - boxHeight) / 2;

        cWidth = (int) ResolutionUtils.getXStatic(BASE_CONTAINER_WIDTH);
        cHeight = (int) ResolutionUtils.getYStatic(BASE_CONTAINER_HEIGHT);
        pX = (int) ResolutionUtils.getXStatic(BASE_PADDING);
        pY = (int) ResolutionUtils.getYStatic(BASE_PADDING);


        int maxXGrid = 0;
        int maxYGrid = 0;
        for (StorageContainer container : containers.values()) {
            if (container.xGrid > maxXGrid) maxXGrid = container.xGrid;
            if (container.yGrid > maxYGrid) maxYGrid = container.yGrid;
        }
        int columns = maxXGrid + 1;
        int rows = maxYGrid + 1;

        int totalGridWidth = (columns * cWidth) + ((columns - 1) * pX);
        int totalGridHeight = (rows * cHeight) + ((rows - 1) * pY);

        startX = isHorizontal ? boxX + pX : boxX + ((boxWidth - totalGridWidth) / 2);
        startY = isHorizontal ? boxY + ((boxHeight - totalGridHeight) / 2) : boxY + pY;

        double visibleCols = (double) boxWidth / (cWidth + pX);
        double visibleRows = (double) boxHeight / (cHeight + pY);

        visibleElements = isHorizontal ? visibleCols : visibleRows;
        totalElements = isHorizontal ? columns : rows;
        maxScroll = Math.max(0, (isHorizontal ? columns : rows) - visibleElements);
        clampScroll();

        offset = this.scrollOffset;
        if (!JefConfig.feature.storage.smoothScroll) {
            offset = Math.round(this.scrollOffset);
        }

        this.drawCenteredString(mc.fontRendererObj, "Offset: " + String.format("%.2f", offset), boxX + (boxWidth / 2), boxY - 15, new Color(255, 255, 255).getRGB());

        drawRect(boxX - 2, boxY - 2, boxX + boxWidth + 4, boxY + boxHeight + 4, new Color(0, 0, 0, 150).getRGB());

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        doGlScissor(boxX, boxY, boxWidth, boxHeight);
        for (StorageContainer container : containers.values()) {
            if (canDraw(isHorizontal ? container.xGrid : container.yGrid)) {
                drawContainer(container);
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (maxScroll > 0 && JefConfig.feature.storage.barScroll) {
            int SCROLLBAR_SIZE = 10;
            int SCROLLBAR_MARGIN = 5;

            if (isHorizontal) {
                trackX = boxX;
                trackY = boxY + boxHeight + SCROLLBAR_MARGIN;
                trackW = boxWidth;
                trackH = SCROLLBAR_SIZE;
            } else {
                trackX = boxX + boxWidth + SCROLLBAR_MARGIN;
                trackY = boxY;
                trackW = SCROLLBAR_SIZE;
                trackH = boxHeight;
            }

            double trackLen = isHorizontal ? trackW : trackH;
            double thumbLen = Math.max(20, trackLen * (visibleElements / totalElements));
            double progress = scrollOffset / maxScroll;
            double thumbStart = (isHorizontal ? trackX : trackY) + progress * (trackLen - thumbLen);

            if (isHorizontal) {
                thumbX = (int) thumbStart;
                thumbY = trackY;
                thumbW = (int) thumbLen;
                thumbH = trackH;
            } else {
                thumbX = trackX;
                thumbY = (int) thumbStart;
                thumbW = trackW;
                thumbH = (int) thumbLen;
            }

            drawRect(trackX, trackY, trackX + trackW, trackY + trackH, new Color(30, 30, 30, 200).getRGB());

            drawRect(thumbX, thumbY, thumbX + thumbW, thumbY + thumbH, new Color(150, 150, 150, 255).getRGB());
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void doGlScissor(int x, int y, int width, int height) {
        int scaleFactor = ResolutionUtils.getFactor();
        int displayHeight = mc.displayHeight;

        GL11.glScissor(x * scaleFactor, displayHeight - ((y + height) * scaleFactor), width * scaleFactor, height * scaleFactor);
    }

    public boolean canDraw(int gridPos) {
        return gridPos >= Math.floor(offset) - 1 && gridPos <= Math.ceil(offset + visibleElements) + 1;
    }

    public boolean isHovering(int mouseX, int mouseY, StorageContainer container) {
        if (container == null) return false;
        if (mouseX < boxX || mouseX > boxX + boxWidth || mouseY < boxY || mouseY > boxY + boxHeight) {
            return false;
        }

        double renderX = startX + ((container.xGrid - (isHorizontal ? offset : 0)) * (cWidth + pX));
        double renderY = startY + ((container.yGrid - (!isHorizontal ? offset : 0)) * (cHeight + pY));

        return mouseX >= renderX && mouseX <= renderX + cWidth && mouseY >= renderY && mouseY <= renderY + cHeight;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        draggedDistance = 0;
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        isDraggingScreen = false;
        isDraggingScrollbar = false;
        if (maxScroll > 0) {
            if (mouseX >= thumbX && mouseX <= thumbX + thumbW && mouseY >= thumbY && mouseY <= thumbY + thumbH) {
                if (JefConfig.feature.storage.barScroll) {
                    isDraggingScrollbar = true;
                    scrollbarDragOffset = isHorizontal ? mouseX - thumbX : mouseY - thumbY;
                    return;
                }
            }
            if (mouseX >= trackX && mouseX <= trackX + trackW && mouseY >= trackY && mouseY <= trackY + trackH) {
                double trackLen = isHorizontal ? trackW : trackH;
                double thumbLen = Math.max(20, trackLen * (visibleElements / totalElements));
                double clickPos = isHorizontal ? mouseX - trackX : mouseY - trackY;

                double newThumbStart = clickPos - (thumbLen / 2);
                double progress = newThumbStart / (trackLen - thumbLen);

                scrollOffset = progress * maxScroll;
                clampScroll();
                if (JefConfig.feature.storage.barScroll) {
                    isDraggingScrollbar = true;
                }
                scrollbarDragOffset = (int) (thumbLen / 2);
                return;
            }


            if (!JefConfig.feature.storage.dragScroll) {
                if (activeContainer != null && !activeContainer.isEmpty() && containers.containsKey(activeContainer)) {
                    if (isHovering(mouseX, mouseY, containers.get(activeContainer))) {
                        containers.get(activeContainer).mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
                for (StorageContainer container : containers.values()) {
                    if (isHovering(mouseX, mouseY, container)) {
                        this.activeContainer = container.id;
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("/" + container.type.command + " " + container.page);
                    }
                }
            }

            if (mouseX >= boxX && mouseX <= boxX + boxWidth && mouseY >= boxY && mouseY <= boxY + boxHeight) {
                if (JefConfig.feature.storage.dragScroll) {
                    isDraggingScreen = true;
                }
            }

        }

        super.mouseClicked(mouseX, mouseY, mouseButton);

    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (isDraggingScrollbar) {
            double trackLen = isHorizontal ? trackW : trackH;
            double thumbLen = Math.max(20, trackLen * (visibleElements / totalElements));
            double mousePos = isHorizontal ? mouseX : mouseY;
            double tStart = isHorizontal ? trackX : trackY;

            double newThumbStart = mousePos - scrollbarDragOffset;
            double progress = (newThumbStart - tStart) / (trackLen - thumbLen);

            scrollOffset = progress * maxScroll;
            clampScroll();

        } else if (isDraggingScreen) {
            int deltaX = mouseX - lastMouseX;
            int deltaY = mouseY - lastMouseY;

            draggedDistance += Math.abs(deltaX) + Math.abs(deltaY);

            if (isHorizontal) {
                scrollOffset -= (double) deltaX / (cWidth + pX);
            } else {
                scrollOffset -= (double) deltaY / (cHeight + pY);
            }

            clampScroll();
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (isDraggingScreen && draggedDistance < 5) {
            if (activeContainer != null && !activeContainer.isEmpty() && containers.containsKey(activeContainer)) {
                if (isHovering(mouseX, mouseY, containers.get(activeContainer))) {
                    containers.get(activeContainer).mouseClicked(mouseX, mouseY, state);
                }
            }
            for (StorageContainer container : containers.values()) {
                if (isHovering(mouseX, mouseY, container)) {
                    this.activeContainer = container.id;
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/" + container.type.command + " " + container.page);
                }
            }
        }

        isDraggingScreen = false;
        isDraggingScrollbar = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    public void drawContainer(StorageContainer container) {
        double renderX = startX + ((container.xGrid - (isHorizontal ? offset : 0)) * (cWidth + pX));
        double renderY = startY + ((container.yGrid - (!isHorizontal ? offset : 0)) * (cHeight + pY));

        drawRect((int) renderX, (int) renderY, (int) renderX + cWidth, (int) renderY + cHeight, new Color(150, 150, 150, 200).getRGB());

        this.drawCenteredString(mc.fontRendererObj, container.type.name().toLowerCase() + "-" + container.page, (int) renderX + (cWidth / 2), (int) renderY + (cHeight / 2) - 4, new Color(255, 255, 255).getRGB());
    }

    private void clampScroll() {
        if (this.scrollOffset > maxScroll) this.scrollOffset = maxScroll;
        if (this.scrollOffset < 0) this.scrollOffset = 0;
    }

    @Override
    public void handleMouseInput() throws IOException {
        int scroll = Mouse.getDWheel();
        if (scroll != 0) {
            int direction = Integer.signum(scroll);
            this.scrollOffset -= direction * (SCROLL_SPEED / 100.0);
            clampScroll();
        }
        super.handleMouseInput();
    }
}
