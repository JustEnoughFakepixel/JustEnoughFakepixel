package com.jef.justenoughfakepixel.features.misc.invbuttons;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.events.GuiContainerRenderButtonsEvent;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.Utils;
import com.jef.justenoughfakepixel.utils.chat.ChatUtils;
import com.jef.justenoughfakepixel.utils.render.HighlightUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@RegisterEvents
public class InvButtonRenderer {

    private static final ResourceLocation EDITOR_TEX = new ResourceLocation("justenoughfakepixel", "invbuttons/editor.png");

    private static Method drawHoveringTextMethod = null;

    static {
        try {
            drawHoveringTextMethod = GuiScreen.class.getDeclaredMethod("drawHoveringText", List.class, int.class, int.class, FontRenderer.class);
            drawHoveringTextMethod.setAccessible(true);
        } catch (Exception e) {
            System.err.println("[JEF] drawHoveringText reflect failed: " + e.getMessage());
        }
    }

    private InventoryButton hovered = null;
    private long hoveredSince = 0L;

    private static boolean isEnabled() {
        return JefConfig.feature != null && JefConfig.feature.misc.enableInvButtons;
    }

    private static boolean isGuiEditor() {
        return Minecraft.getMinecraft().currentScreen instanceof GuiInvButtonEditor;
    }

    private static int btnX(InventoryButton btn, int gl, int gw) {
        return gl + btn.x + (btn.anchorRight ? gw : 0);
    }

    private static int btnY(InventoryButton btn, int gt, int gh) {
        return gt + btn.y + (btn.anchorBottom ? gh : 0);
    }

    private static boolean isVisible(InventoryButton btn, GuiContainer gui) {
        return btn.isActive() && (!btn.playerInvOnly || gui instanceof GuiInventory);
    }

    private static InventoryButton hitTest(int mx, int my, int gl, int gt, int gw, int gh, GuiContainer gui) {
        for (InventoryButton btn : InventoryButtonStorage.getInstance().getButtons()) {
            if (!isVisible(btn, gui)) continue;
            int bx = btnX(btn, gl, gw);
            int by = btnY(btn, gt, gh);
            if (mx >= bx && mx <= bx + 18 && my >= by && my <= by + 18) return btn;
        }
        return null;
    }

    @SubscribeEvent
    public void onRenderButtons(GuiContainerRenderButtonsEvent event) {
        if (!isEnabled() || isGuiEditor()) return;

        GuiContainer gui = event.gui;
        int gl = gui.guiLeft, gt = gui.guiTop, gw = gui.xSize, gh = gui.ySize;
        int mx = event.mouseX, my = event.mouseY;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-gl, -gt, 50);
        for (InventoryButton btn : InventoryButtonStorage.getInstance().getButtons()) {
            if (!isVisible(btn, gui)) continue;
            int bx = btnX(btn, gl, gw);
            int by = btnY(btn, gt, gh);

            GlStateManager.color(1, 1, 1, 1f);
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            Minecraft.getMinecraft().getTextureManager().bindTexture(EDITOR_TEX);
            Utils.drawTexturedRect(bx, by, 18, 18, btn.backgroundIndex * 18 / 256f, (btn.backgroundIndex * 18 + 18) / 256f, 18 / 256f, 36 / 256f, GL11.GL_NEAREST);
            if (btn.icon != null && !btn.icon.trim().isEmpty()) {
                GlStateManager.enableDepth();
                InvButtonIconRenderer.renderIcon(btn.icon, bx + 1, by + 1);
            }
        }
        GlStateManager.popMatrix();

        InventoryButton newHovered = hitTest(mx, my, gl, gt, gw, gh, gui);
        long now = System.currentTimeMillis();
        if (newHovered != hovered) {
            hovered = newHovered;
            hoveredSince = now;
        }

        if (hovered == null) return;

        int bx = btnX(hovered, gl, gw);
        int by = btnY(hovered, gt, gh);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-gl, -gt, 60);
        HighlightUtils.renderButtonHighlight(bx, by);
        GlStateManager.popMatrix();

        int delay = JefConfig.feature != null ? JefConfig.feature.misc.invButtonTooltipDelay : 600;
        if (now - hoveredSince >= delay && drawHoveringTextMethod != null) {
            String cmd = hovered.command.trim();
            if (!cmd.startsWith("/")) cmd = "/" + cmd;
            GlStateManager.pushMatrix();
            GlStateManager.translate(-gl, -gt, 400);
            try {
                drawHoveringTextMethod.invoke(gui, Collections.singletonList("§7" + cmd), mx, my, Minecraft.getMinecraft().fontRendererObj);
            } catch (Exception ignored) {
            }
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!isEnabled() || isGuiEditor()) return;
        if (Mouse.getEventButton() < 0) return;
        if (!(event.gui instanceof GuiContainer)) return;

        GuiContainer gui = (GuiContainer) event.gui;
        int gl = gui.guiLeft, gt = gui.guiTop, gw = gui.xSize, gh = gui.ySize;
        int mx = Mouse.getEventX() * event.gui.width / Minecraft.getMinecraft().displayWidth;
        int my = event.gui.height - Mouse.getEventY() * event.gui.height / Minecraft.getMinecraft().displayHeight - 1;

        InventoryButton btn = hitTest(mx, my, gl, gt, gw, gh, gui);
        if (btn == null) return;

        if (Minecraft.getMinecraft().thePlayer.inventory.getItemStack() != null) {
            event.setCanceled(true);
            return;
        }

        int clickType = JefConfig.feature != null ? JefConfig.feature.misc.invButtonClickType : 0;
        boolean fire = (clickType == 0) == Mouse.getEventButtonState();
        if (fire) {
            String cmd = btn.command.trim();
            if (!cmd.startsWith("/")) cmd = "/" + cmd;
            if (ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().thePlayer, cmd) == 0)
                ChatUtils.sendChatCommand(cmd);
        }
    }
}