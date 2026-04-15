package com.jef.justenoughfakepixel.features.storage;

import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.utils.render.NineSliceUtils;
import com.jef.justenoughfakepixel.utils.render.ResolutionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedHashMap;

public class StorageOverlay extends GuiScreen {

    // Information
    public static LinkedHashMap<String,SContainer> containers = new LinkedHashMap<>();
    public static int echests,bags;

    // Drawing
    public static int boxX,boxY,boxW,boxH;
    public static int containerW,containerH;
    public static int PADDING = 5;

    // Scrolling
    public static int SINGLE_FILE = 3;
    public static float scrollOffset = 0;
    public static float scrollTarget = 0;
    public static final float SCROLL_LENGTH = 0.2f;

    // Changeable Variables
    public static float SCROLL_SPEED = 1f;
    public static boolean isHorizontal = false;

    // Containers
    private static final ResourceLocation CONTAINER_BG =
            new ResourceLocation("justenoughfakepixel",
                    "textures/gui/storage_container_bg.png");
    private static final int NINE_SLICE_CORNER = 6;
    private static final int NINE_SLICE_SIZE   = 18;

    public static boolean openGUI(ContainerChest parser){
        containers = new LinkedHashMap<>(StorageSaving.loadStorageData());
        if(containers.isEmpty()) {
            containers = StorageParser.parseOverlay(parser);
        }
        if(containers.isEmpty()) return false;
        SCROLL_SPEED = JefConfig.feature.storage.scrollSpeed;
        isHorizontal = JefConfig.feature.storage.horizontal;

        echests = 0;
        bags = 0;
        scrollOffset = 0;
        scrollTarget = 0;

        containers.values().forEach(s -> {
            if(s.type == Type.ECHEST) {
                echests++;
            }else {
                bags++;
            }
        });
        JefConfig.screenToOpen = new StorageOverlay();
        return true;
    }

    @Override
    public void onGuiClosed() {
        StorageSaving.saveStorageData(containers.values());
    }

    @Override
    public void initGui() {
        boxW = (int)ResolutionUtils.getXStatic(1080);
        boxH = (int)ResolutionUtils.getYStatic(600);
        boxX = (this.width/2) - (boxW / 2);
        boxY = (int)((this.height/2.0) - (boxH / 1.25));
        containerW = (int)ResolutionUtils.getXStatic(307);
        containerH = (int)ResolutionUtils.getXStatic(170);
    }

    private int getRowCount() {
        return (int) Math.ceil((double) containers.size() / SINGLE_FILE);
    }

    private int getColCount() {
        return (int) Math.ceil((double) containers.size() / SINGLE_FILE);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getDWheel();
        if (scroll != 0) {
            scrollTarget -= scroll > 0 ? SCROLL_SPEED : -SCROLL_SPEED;
            int maxScroll;
            if (isHorizontal) {
                int visibleCols = boxW / (containerW + PADDING);
                maxScroll = Math.max(0, getColCount() - visibleCols);
            } else {
                int visibleRows = boxH / (containerH + PADDING);
                maxScroll = Math.max(0, getRowCount() - visibleRows);
            }
            scrollTarget = Math.max(0, Math.min(scrollTarget, maxScroll));
        }
    }

    private int[] getGridPosition(int index) {
        if (isHorizontal) {
            int xGrid = index / SINGLE_FILE;
            int yGrid = index % SINGLE_FILE;
            return new int[]{xGrid, yGrid};
        } else {
            int xGrid = index % SINGLE_FILE;
            int yGrid = index / SINGLE_FILE;
            return new int[]{xGrid, yGrid};
        }
    }

    private int[] getGridStart() {
        if (isHorizontal) {
            int totalGridH = (containerH * SINGLE_FILE) + (PADDING * (SINGLE_FILE - 1));
            int gridStartX = boxX + PADDING;
            int gridStartY = boxY + (boxH - totalGridH) / 2;
            return new int[]{gridStartX, gridStartY};
        } else {
            int totalGridW = (containerW * SINGLE_FILE) + (PADDING * (SINGLE_FILE - 1));
            int gridStartX = boxX + (boxW - totalGridW) / 2;
            int gridStartY = boxY + PADDING;
            return new int[]{gridStartX, gridStartY};
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(containers.isEmpty()) return;

        NineSliceUtils.draw(CONTAINER_BG,boxX,boxY,boxW,boxH,6,18);

        scrollOffset += (scrollTarget - scrollOffset) * SCROLL_LENGTH;

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int scaleFactor = sr.getScaleFactor();
        int inset = NINE_SLICE_CORNER;
        int scissorY = Minecraft.getMinecraft().displayHeight - (boxY + boxH - inset) * scaleFactor;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (boxX + inset) * scaleFactor,
                scissorY + inset * scaleFactor,
                (boxW - inset * 2) * scaleFactor,
                (boxH - inset * 2) * scaleFactor
        );
        for(SContainer container : containers.values()){
            drawContainer(mouseX,mouseY,container);
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        
        drawCenteredString(this.fontRendererObj,"Containers: " + containers.size(),
                this.width / 2,10,
                Color.white.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        int[] gridStart = getGridStart();
        int gridStartX = gridStart[0];
        int gridStartY = gridStart[1];

        int relativeX, relativeY;
        if (isHorizontal) {
            int scrollPixels = (int)(scrollOffset * (containerW + PADDING));
            relativeX = mouseX - gridStartX + scrollPixels;
            relativeY = mouseY - gridStartY;
        } else {
            int scrollPixels = (int)(scrollOffset * (containerH + PADDING));
            relativeX = mouseX - gridStartX;
            relativeY = mouseY - gridStartY + scrollPixels;
        }

        if (relativeX >= 0 && relativeY >= 0) {
            int xGrid, yGrid;
            if (isHorizontal) {
                xGrid = relativeX / (containerW + PADDING);
                yGrid = relativeY / (containerH + PADDING);
            } else {
                xGrid = relativeX / (containerW + PADDING);
                yGrid = relativeY / (containerH + PADDING);
            }

            boolean inHorizontalPadding = relativeX % (containerW + PADDING) > containerW;
            boolean inVerticalPadding = relativeY % (containerH + PADDING) > containerH;

            if (!inHorizontalPadding && !inVerticalPadding) {
                int targetIndex = isHorizontal
                        ? (xGrid * SINGLE_FILE) + yGrid
                        : (yGrid * SINGLE_FILE) + xGrid;

                SContainer clickedContainer = null;
                for (SContainer container : containers.values()) {
                    int index = container.page - 1;
                    if (container.type == Type.BAG) index += echests;
                    if (index == targetIndex) {
                        clickedContainer = container;
                        break;
                    }
                }
                if (clickedContainer != null) handleContainerClick(clickedContainer);
            }
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    public void handleContainerClick(SContainer container){
        JefMod.logger.info("Opening " + container.type.name().toLowerCase() + " of page " + container.page);
        if(container.type == Type.ECHEST){
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/echest " + container.page);
        }else {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/storage " + container.page);
        }
    }

    private void drawContainer(int mouseX, int mouseY, SContainer container) {
        int index = container.page - 1;
        if (container.type == Type.BAG) index += echests;

        int[] gridPos = getGridPosition(index);
        int xGrid = gridPos[0];
        int yGrid = gridPos[1];

        int[] gridStart = getGridStart();
        int gridStartX = gridStart[0];
        int gridStartY = gridStart[1];

        int xStart, yStart;
        if (isHorizontal) {
            int scrollPixels = (int)(scrollOffset * (containerW + PADDING));
            xStart = gridStartX + (xGrid * (containerW + PADDING)) - scrollPixels;
            yStart = gridStartY + (yGrid * (containerH + PADDING));
        } else {
            int scrollPixels = (int)(scrollOffset * (containerH + PADDING));
            xStart = gridStartX + (xGrid * (containerW + PADDING));
            yStart = gridStartY + (yGrid * (containerH + PADDING)) - scrollPixels;
        }

        int rw = container.renderW > 0 ? container.renderW : containerW;
        int rh = container.renderH > 0 ? container.renderH : containerH;
        int rx = xStart + (containerW - rw) / 2;
        int ry = yStart + (containerH - rh) / 2;

        boolean hovering = isHovering(mouseX, mouseY, xStart, yStart, containerW, containerH);
        if (hovering) GL11.glColor4f(1.3f, 1.3f, 1.3f, 1f);

        NineSliceUtils.draw(CONTAINER_BG, rx, ry, rw, rh, NINE_SLICE_CORNER, NINE_SLICE_SIZE);

        GL11.glColor4f(1f, 1f, 1f, 1f);

        int slotW = (int)ResolutionUtils.getXStatic(33);
        int slotH = (int)ResolutionUtils.getYStatic(33);

        int slotsPerRow = 9;
        int offset = 4;

        for(int i = 0;i < container.slotCount;i++){
            int col = i % slotsPerRow;
            int row = i / slotsPerRow;

            int xPos = rx + offset + (slotW * col);
            int yPos = ry + offset + (slotH * row);

            ResourceLocation tex = new ResourceLocation("justenoughfakepixel",
                    "textures/gui/storage_slot.png");
            Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();

            drawScaledCustomSizeModalRect(xPos,yPos,0,0,18,18,slotW,slotH,18,18);

            GlStateManager.enableBlend();
        }


        drawCenteredString(this.fontRendererObj,
                "Container ID: " + container.id, rx + rw/2, ry + rh/2, Color.WHITE.getRGB());
    }

    private boolean isHovering(int mouseX, int mouseY, int xStart, int yStart, int width, int height) {
        return mouseX > xStart &&
                mouseX < xStart + width &&
                mouseY > yStart &&
                mouseY < yStart + height;
    }

}
