package com.jef.justenoughfakepixel.features.storage;

import com.jef.justenoughfakepixel.features.storage.data.StorageData;
import com.jef.justenoughfakepixel.features.storage.render.StorageRenderer;
import com.jef.justenoughfakepixel.features.storage.utils.SContainer;
import com.jef.justenoughfakepixel.features.storage.utils.StorageListener;
import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.features.storage.utils.StorageParser;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;

import java.util.LinkedHashMap;


@RegisterEvents
public class StorageManager {

    @Getter
    private static String activeContainerId = null;
    @Getter
    private static StorageRenderer renderer = null;
    @Getter
    private static boolean overlayActive = false;
    private static long transitionStartTime = 0;
    @Getter
    private static boolean isTransitioning = false;
    private static boolean wasMouseLocked = false;
    private static final long TRANSITION_TIMEOUT = 5000;

    public static void setActiveContainer(String containerId) {
        activeContainerId = containerId;
        endTransition();
    }

    private static void endTransition() {
        if (isTransitioning && !wasMouseLocked) {
            setMouseLockedSilent(false);
        }
        isTransitioning = false;
    }

    /** Sets the mouse lock state without printing the "Mouse locked/unlocked" chat message. */
    private static void setMouseLockedSilent(boolean locked) {
        if (JefConfig.feature == null) return;
        JefConfig.feature.farming.lockMouse = locked;
        JefConfig.saveConfig();
    }

    public static boolean initializeOverlay(ContainerChest parser) {
        if (StorageData.containers.isEmpty()) {
            StorageData.loadContainers();
        }

        LinkedHashMap<String, SContainer> containers = StorageParser.parseOverlay(parser, StorageData.containers);

        if (containers.isEmpty()) {
            return false;
        }

        StorageData.containers = containers;

        renderer = new StorageRenderer(containers);
        overlayActive = true;


        return true;
    }

    public static void renderOverlay(int mouseX, int mouseY) {
        if (renderer == null && !StorageData.containers.isEmpty()) {
            renderer = new StorageRenderer(StorageData.containers);
            overlayActive = true;
        }

        if (isTransitioning) {
            long elapsed = System.currentTimeMillis() - transitionStartTime;
            if (elapsed > TRANSITION_TIMEOUT) {
                System.out.println("[JEF DEBUG] Transition timeout - closing overlay");
                closeOverlay();
                return;
            }
        }

        if (renderer != null && overlayActive) {
            renderer.render(mouseX, mouseY);
        }
    }


    public static boolean isMouseOverStorageArea(int mouseX, int mouseY) {
        if (renderer == null) return false;
        return renderer.isMouseOverStorageArea(mouseX, mouseY);
    }

    public static void handleMouseInput() {
        if (renderer == null) return;

        int dWheel = org.lwjgl.input.Mouse.getEventDWheel();
        if (dWheel != 0) {
            renderer.handleScroll(dWheel);
        }

        if (org.lwjgl.input.Mouse.getEventButtonState()) {
            int mouseButton = org.lwjgl.input.Mouse.getEventButton();
            if (mouseButton == 0) {
                net.minecraft.client.gui.ScaledResolution sr = new net.minecraft.client.gui.ScaledResolution(Minecraft.getMinecraft());
                int mouseX = org.lwjgl.input.Mouse.getX() * sr.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
                int mouseY = sr.getScaledHeight() - org.lwjgl.input.Mouse.getY() * sr.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;

                renderer.handleClick(mouseX, mouseY);
            }
        }
    }


    public static boolean handleKeyTyped(char typedChar, int keyCode) {
        if (renderer == null) return false;
        return renderer.handleKeyTyped(typedChar, keyCode);
    }

    public static void switchToContainer(String containerId) {
        SContainer container = StorageData.containers.get(containerId);
        if (container == null) return;

        isTransitioning = true;
        transitionStartTime = System.currentTimeMillis();
        wasMouseLocked = com.jef.justenoughfakepixel.features.farming.mouse.LockMouse.isLocked();
        if (!wasMouseLocked) {
            setMouseLockedSilent(true);
        }

        StorageListener.setSwitchingContainer(true);
        Minecraft.getMinecraft().thePlayer.closeScreen();

        String command = buildContainerCommand(container);
        executeCommandDelayed(command);
    }

    private static String buildContainerCommand(SContainer container) {
        switch (container.type) {
            case ECHEST:
                return "/echest " + container.page;
            case BAG:
                return "/storage " + container.page;
            default:
                throw new IllegalArgumentException("Unknown container type: " + container.type);
        }
    }

    private static void executeCommandDelayed(String command) {
        new Thread(() -> {
            try {
                Thread.sleep(100);
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage(command);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static boolean isClickingPlayerInventory(int mouseX, int mouseY) {
        if (renderer == null) return false;
        return renderer.isClickingPlayerInventory(mouseX, mouseY);
    }

    public static void closeOverlay() {
        StorageData.saveContainers();
        activeContainerId = null;
        renderer = null;
        overlayActive = false;
        if (isTransitioning && !wasMouseLocked) {
            setMouseLockedSilent(false);
        }
        isTransitioning = false;
    }


    public static void overrideIsMouseOverSlot(net.minecraft.inventory.Slot slotIn, int mouseX, int mouseY, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        if (!isOverlayActive() || renderer == null) return;
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChest)) return;

        boolean isPlayerSlot = slotIn.inventory == Minecraft.getMinecraft().thePlayer.inventory;

        if (isPlayerSlot) {
            cir.setReturnValue(renderer.isMouseOverPlayerInventorySlot(slotIn, mouseX, mouseY));
        } else {
            cir.setReturnValue(renderer.isMouseOverActiveContainerSlot(slotIn, mouseX, mouseY));
        }
    }
}