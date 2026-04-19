package com.jef.justenoughfakepixel.features.capes.ui;

import com.jef.justenoughfakepixel.features.capes.Cape;
import com.jef.justenoughfakepixel.features.capes.CapeManager;
import com.jef.justenoughfakepixel.utils.render.NineSliceUtils;
import com.jef.justenoughfakepixel.utils.render.ResolutionUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.ScaledResolution;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CapeSelectorGUI extends GuiScreen {

    public List<CapeDisplay> capes = new ArrayList<>();

    private static final ResourceLocation CONTAINER_BG =
            new ResourceLocation("justenoughfakepixel", "textures/gui/storage_container_bg.png");

    private float scrollOffset = 0f;
    private float scrollVelocity = 0f;
    private static final float SCROLL_FRICTION = 0.85f;

    private boolean isDraggingBg = false;
    private int bgDragLastX = 0;

    private int mousePressX = 0;
    private int mousePressY = 0;
    private static final int DRAG_THRESHOLD = 4;

    @Override
    public void initGui() {
        super.initGui();
        capes.clear();
        CapeManager.capes.values().forEach(val -> capes.add(new CapeDisplay(val)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        scrollOffset += scrollVelocity;
        scrollVelocity *= SCROLL_FRICTION;
        clampScroll();

        int boxW = (int) ResolutionUtils.getXStatic(1200);
        int boxH = (int) ResolutionUtils.getYStatic(340);
        int boxX = (this.width / 2) - (boxW / 2);
        int boxY = (this.height / 2) - (boxH / 2);

        NineSliceUtils.draw(CONTAINER_BG, boxX, boxY, boxW, boxH, 6, 18);

        int PADDING = (int) ResolutionUtils.getXStatic(12);
        int cardX = boxX + PADDING + (int) scrollOffset;
        int cardY = boxY + (boxH / 2) - (capes.isEmpty() ? 0 : capes.get(0).height / 2);

        String localPlayer = mc.thePlayer != null ? mc.thePlayer.getGameProfile().getName() : "";
        Cape equipped = CapeManager.getCapeForPlayer(localPlayer);
        String equippedId = equipped != null ? equipped.id : null;
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                boxX * scaleFactor,
                (sr.getScaledHeight() - (boxY + boxH)) * scaleFactor,
                boxW * scaleFactor,
                boxH * scaleFactor
        );

        for (CapeDisplay card : capes) {
            if (cardX + card.width < boxX || cardX > boxX + boxW) {
                card.xPos = -1;
                cardX += card.width + PADDING;
                continue;
            }
            boolean hovering = card.isOverClamped(mouseX, mouseY, boxX, boxY, boxX + boxW, boxY + boxH);
            boolean selected = card.capeID.equals(equippedId);
            card.draw(cardX, cardY, hovering, selected, mc);
            cardX += card.width + PADDING;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        String title = "Select Cape";
        mc.fontRendererObj.drawString(
                title,
                (int) (this.width / 2f - mc.fontRendererObj.getStringWidth(title) / 2f),
                boxY - 14,new Color(255,255,255,255).getRGB()
        );

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            scrollVelocity += wheel > 0 ? 20f : -20f;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (button == 0) {
            mousePressX = mouseX;
            mousePressY = mouseY;

            int boxW = (int) ResolutionUtils.getXStatic(1200);
            int boxH = (int) ResolutionUtils.getYStatic(340);
            int boxX = (this.width / 2) - (boxW / 2);
            int boxY = (this.height / 2) - (boxH / 2);

            boolean hitCard = false;
            for (CapeDisplay card : capes) {
                if (card.isOverClamped(mouseX, mouseY, boxX, boxY, boxX + boxW, boxY + boxH)) {
                    hitCard = true;
                    break;
                }
            }
            if (!hitCard) {
                isDraggingBg = true;
                bgDragLastX = mouseX;
                scrollVelocity = 0;
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        isDraggingBg = false;

        int boxW = (int) ResolutionUtils.getXStatic(1200);
        int boxH = (int) ResolutionUtils.getYStatic(340);
        int boxX = (this.width / 2) - (boxW / 2);
        int boxY = (this.height / 2) - (boxH / 2);

        int dragDist = Math.abs(mouseX - mousePressX) + Math.abs(mouseY - mousePressY);
        if (dragDist < DRAG_THRESHOLD) {
            String localPlayerName = mc.thePlayer.getGameProfile().getName();
            for (CapeDisplay card : capes) {
                if (card.isOverClamped(mouseX, mouseY, boxX, boxY, boxX + boxW, boxY + boxH)) {
                    Cape selected = CapeManager.getCape(card.capeID);
                    Cape pCape = CapeManager.getCapeForPlayer(localPlayerName);
                    if (pCape != null && pCape.id.equals(card.capeID)) {
                        CapeManager.removeCape(localPlayerName);
                    } else if (selected != null) {
                        CapeManager.equipCape(localPlayerName, selected);
                    }
                    break;
                }
            }
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
        if (button == 0) {
            if (isDraggingBg) {
                int dx = mouseX - bgDragLastX;
                scrollOffset += dx;
                scrollVelocity = dx * 0.5f;
                bgDragLastX = mouseX;
            }
        }
        super.mouseClickMove(mouseX, mouseY, button, timeSinceLastClick);
    }

    private void clampScroll() {
        if (capes.isEmpty()) return;
        int PADDING = (int) ResolutionUtils.getXStatic(12);
        int boxW = (int) ResolutionUtils.getXStatic(1200);
        int totalW = capes.stream().mapToInt(c -> c.width + PADDING).sum();

        float minScroll = -(totalW - boxW + PADDING);
        float maxScroll = 0;

        if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
            scrollVelocity = 0;
        }
        if (scrollOffset < minScroll) {
            scrollOffset = minScroll;
            scrollVelocity = 0;
        }
    }
}