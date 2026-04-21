package com.jef.justenoughfakepixel.features.storage.utils;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.features.storage.StorageManager;
import com.jef.justenoughfakepixel.features.storage.data.StorageData;
import com.jef.justenoughfakepixel.features.storage.render.StorageRenderer;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

@RegisterEvents
public class StorageListener {

    @Setter
    private static boolean switchingContainer = false;
    private boolean shouldRenderOverlay = false;
    private boolean overlayInitialized = false;

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (!JefConfig.feature.storage.enabled) return;
        if (!shouldRenderOverlay || !overlayInitialized) return;

        String message = event.message.getUnformattedText();
        if (message.contains("Slow down!") || message.contains("executing commands too fast")) {
            shouldRenderOverlay = false;
            overlayInitialized = false;
            StorageManager.closeOverlay();
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (!JefConfig.feature.storage.enabled) return;

        if (event.gui == null) {
            handleGuiClose();
            return;
        }

        if (!(event.gui instanceof GuiChest)) {
            if (!switchingContainer) {
                resetOverlayState();
            }
            return;
        }

        GuiChest guiChest = (GuiChest) event.gui;
        if (!(guiChest.inventorySlots instanceof ContainerChest)) return;

        ContainerChest chest = (ContainerChest) guiChest.inventorySlots;
        String title = chest.getLowerChestInventory().getDisplayName().getUnformattedText();

        handleStorageGuiOpen(title);
    }

    private void handleGuiClose() {
        if (!switchingContainer) {
            resetOverlayState();
            StorageManager.closeOverlay();
        }
    }

    private void resetOverlayState() {
        shouldRenderOverlay = false;
        overlayInitialized = false;
    }

    private void handleStorageGuiOpen(String title) {
        if (title == null) return;

        switch (getStorageGuiType(title)) {
            case STORAGE_MENU:
                shouldRenderOverlay = true;
                overlayInitialized = false;
                switchingContainer = false;
                break;
            case STORAGE_CONTAINER:
                if (!StorageData.containers.isEmpty() || shouldRenderOverlay) {
                    shouldRenderOverlay = true;
                    overlayInitialized = true;
                    switchingContainer = false;
                }
                break;
            case OTHER:
                if (!switchingContainer) {
                    resetOverlayState();
                }
                break;
        }
    }

    private StorageGuiType getStorageGuiType(String title) {
        if (title.equals("Storage")) {
            return StorageGuiType.STORAGE_MENU;
        } else if (StorageParser.isStorageContainer(title)) {
            return StorageGuiType.STORAGE_CONTAINER;
        }
        return StorageGuiType.OTHER;
    }

    @SubscribeEvent
    public void onBackgroundDrawn(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!shouldRenderOverlay) return;
        if (!JefConfig.feature.storage.enabled) return;

        if (!(event.gui instanceof GuiChest)) return;

        GuiChest guiChest = (GuiChest) event.gui;
        ContainerChest chest = (ContainerChest) guiChest.inventorySlots;
        String title = chest.getLowerChestInventory().getDisplayName().getUnformattedText();

        if (title == null || !title.equals("Storage")) return;

        if (!overlayInitialized) {
            boolean success = StorageManager.initializeOverlay(chest);
            if (success) {
                overlayInitialized = true;
            }
        }
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!shouldRenderOverlay || !overlayInitialized) return;
        if (!JefConfig.feature.storage.enabled) return;
        if (!(event.gui instanceof GuiChest)) return;

        GuiChest guiChest = (GuiChest) event.gui;
        int mouseX = Mouse.getX() * guiChest.width / Minecraft.getMinecraft().displayWidth;
        int mouseY = guiChest.height - Mouse.getY() * guiChest.height / Minecraft.getMinecraft().displayHeight - 1;

        if (handleScrollInput()) {
            event.setCanceled(true);
            return;
        }

        if (handleClickInput(mouseX, mouseY, guiChest)) {
            event.setCanceled(true);
        }
    }

    private boolean handleScrollInput() {
        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0) {
            StorageManager.handleMouseInput();
            return true;
        }
        return false;
    }

    private boolean handleClickInput(int mouseX, int mouseY, GuiChest guiChest) {
        int button = Mouse.getEventButton();
        if (button != 0 && button != 1) return false;

        if (isClickingPlayerInventory(mouseX, mouseY) || isClickingActiveContainerSlots(mouseX, mouseY, guiChest)) {
            return false;
        }

        StorageManager.handleMouseInput();
        return true;
    }

    private boolean isClickingPlayerInventory(int mouseX, int mouseY) {
        return StorageManager.isClickingPlayerInventory(mouseX, mouseY);
    }

    private boolean isClickingActiveContainerSlots(int mouseX, int mouseY, GuiChest guiChest) {
        StorageRenderer r = StorageManager.getRenderer();
        if (r == null) return false;
        for (net.minecraft.inventory.Slot slot : guiChest.inventorySlots.inventorySlots) {
            if (slot == null) continue;
            if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) continue;
            if (r.isMouseOverActiveContainerSlot(slot, mouseX, mouseY)) return true;
        }
        return false;
    }

    @SubscribeEvent
    public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!shouldRenderOverlay || !overlayInitialized) return;
        if (!JefConfig.feature.storage.enabled) return;
        if (!(event.gui instanceof GuiChest)) return;

        int keyCode = org.lwjgl.input.Keyboard.getEventKey();
        if (keyCode == org.lwjgl.input.Keyboard.KEY_ESCAPE) return;
        if (!org.lwjgl.input.Keyboard.getEventKeyState()) return;

        char typedChar = org.lwjgl.input.Keyboard.getEventCharacter();

        if (StorageManager.handleKeyTyped(typedChar, keyCode)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!shouldRenderOverlay || !overlayInitialized) return;
        if (!JefConfig.feature.storage.enabled) return;
        if (!(event.gui instanceof GuiChest)) return;

        StorageManager.renderOverlay(event.mouseX, event.mouseY);
        com.jef.justenoughfakepixel.utils.render.ItemRenderUtils.renderHeldCursorItem();
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!JefConfig.feature.storage.enabled) return;
        if (!switchingContainer || !overlayInitialized || !StorageManager.isOverlayActive()) return;
        if (Minecraft.getMinecraft().currentScreen != null) return;

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int mouseX = Mouse.getX() * sr.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
        int mouseY = sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
        StorageManager.renderOverlay(mouseX, mouseY);
    }

    private enum StorageGuiType {
        STORAGE_MENU, STORAGE_CONTAINER, OTHER
    }
}
