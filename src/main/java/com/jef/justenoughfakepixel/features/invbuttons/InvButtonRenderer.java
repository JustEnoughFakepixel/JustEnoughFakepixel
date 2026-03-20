package com.jef.justenoughfakepixel.features.invbuttons;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.Utils;
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

    private static final ResourceLocation EDITOR_TEX =
            new ResourceLocation("justenoughfakepixel", "invbuttons/editor.png");

    private static Method drawHoveringTextMethod = null;
    static {
        try {
            drawHoveringTextMethod = GuiScreen.class.getDeclaredMethod(
                    "drawHoveringText", List.class, int.class, int.class, FontRenderer.class);
            drawHoveringTextMethod.setAccessible(true);
        } catch (Exception e) {
            System.err.println("[JEF] drawHoveringText reflect failed: " + e.getMessage());
        }
    }

    private InventoryButton hovered = null;
    private long hoveredSince = 0L;

    @SubscribeEvent
    public void onDrawPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!isEnabled()) return;
        if (!(event.gui instanceof GuiContainer)) return;
        if (event.gui instanceof GuiInvButtonEditor) return;

        GuiContainer gui = (GuiContainer) event.gui;
        int gl = gui.guiLeft, gt = gui.guiTop, gw = gui.xSize, gh = gui.ySize;

        List<InventoryButton> buttons = InventoryButtonStorage.getInstance().getButtons();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 50);
        for (InventoryButton btn : buttons) {
            if (!btn.isActive()) continue;
            if (btn.playerInvOnly && !(event.gui instanceof GuiInventory)) continue;
            int bx = gl + btn.x + (btn.anchorRight  ? gw : 0);
            int by = gt + btn.y + (btn.anchorBottom ? gh : 0);

            GlStateManager.color(1, 1, 1, 1f);
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            Minecraft.getMinecraft().getTextureManager().bindTexture(EDITOR_TEX);
            Utils.drawTexturedRect(bx, by, 18, 18,
                    btn.backgroundIndex * 18 / 256f,
                    (btn.backgroundIndex * 18 + 18) / 256f,
                    18 / 256f, 36 / 256f, GL11.GL_NEAREST);
            if (btn.icon != null && !btn.icon.trim().isEmpty()) {
                GlStateManager.enableDepth();
                InvButtonIconRenderer.renderIcon(btn.icon, bx + 1, by + 1);
            }
        }
        GlStateManager.popMatrix();

        // tooltip
        int mx = event.mouseX, my = event.mouseY;
        long now = System.currentTimeMillis();
        int delay = JefConfig.feature != null ? JefConfig.feature.misc.invButtonTooltipDelay : 600;

        InventoryButton newHovered = null;
        for (InventoryButton btn : buttons) {
            if (!btn.isActive()) continue;
            if (btn.playerInvOnly && !(event.gui instanceof GuiInventory)) continue;
            int bx = gl + btn.x + (btn.anchorRight  ? gw : 0);
            int by = gt + btn.y + (btn.anchorBottom ? gh : 0);
            if (mx >= bx && mx <= bx + 18 && my >= by && my <= by + 18) { newHovered = btn; break; }
        }
        if (newHovered != hovered) { hovered = newHovered; hoveredSince = now; }
        if (hovered != null && now - hoveredSince >= delay && drawHoveringTextMethod != null) {
            String cmd = hovered.command.trim();
            if (!cmd.startsWith("/")) cmd = "/" + cmd;
            try {
                drawHoveringTextMethod.invoke(gui,
                        Collections.singletonList("\u00a77" + cmd),
                        mx, my, Minecraft.getMinecraft().fontRendererObj);
            } catch (Exception ignored) {}
        }
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!isEnabled()) return;
        if (Mouse.getEventButton() < 0) return;
        if (!(event.gui instanceof GuiContainer)) return;
        if (event.gui instanceof GuiInvButtonEditor) return;

        GuiContainer gui = (GuiContainer) event.gui;
        int gl = gui.guiLeft, gt = gui.guiTop, gw = gui.xSize, gh = gui.ySize;
        int mx = Mouse.getEventX() * event.gui.width  / Minecraft.getMinecraft().displayWidth;
        int my = event.gui.height - Mouse.getEventY() * event.gui.height / Minecraft.getMinecraft().displayHeight - 1;

        int clickType = JefConfig.feature != null ? JefConfig.feature.misc.invButtonClickType : 0;
        boolean fire  = clickType == 0 ? Mouse.getEventButtonState() : !Mouse.getEventButtonState();

        for (InventoryButton btn : InventoryButtonStorage.getInstance().getButtons()) {
            if (!btn.isActive()) continue;
            if (btn.playerInvOnly && !(event.gui instanceof GuiInventory)) continue;
            int bx = gl + btn.x + (btn.anchorRight  ? gw : 0);
            int by = gt + btn.y + (btn.anchorBottom ? gh : 0);
            if (mx >= bx && mx <= bx + 18 && my >= by && my <= by + 18) {
                if (Minecraft.getMinecraft().thePlayer.inventory.getItemStack() == null) {
                    if (fire) {
                        String cmd = btn.command.trim();
                        if (!cmd.startsWith("/")) cmd = "/" + cmd;
                        if (ClientCommandHandler.instance.executeCommand(
                                Minecraft.getMinecraft().thePlayer, cmd) == 0)
                            Minecraft.getMinecraft().thePlayer.sendChatMessage(cmd);
                    }
                } else {
                    event.setCanceled(true);
                }
                return;
            }
        }
    }

    private static boolean isEnabled() {
        return JefConfig.feature != null && JefConfig.feature.misc.enableInvButtons;
    }
}
