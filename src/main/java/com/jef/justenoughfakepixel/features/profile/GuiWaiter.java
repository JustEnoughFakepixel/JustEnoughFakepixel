package com.jef.justenoughfakepixel.features.profile;

import com.jef.justenoughfakepixel.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

public class GuiWaiter {

    public static final GuiWaiter INSTANCE = new GuiWaiter();

    private final Deque<PendingWait> queue = new ArrayDeque<>();

    private GuiWaiter() {}


    public static void waitFor(String expectedTitle, int tickDelay, int pressSlot,Consumer<ContainerChest> callback) {
        INSTANCE.queue.add(new PendingWait(expectedTitle, tickDelay, pressSlot,callback));
    }


    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (queue.isEmpty()) return;

        PendingWait head = queue.peek();

        if (!head.guiReceived) {
            ContainerChest chest = getOpenChest(head.expectedTitle);
            if (chest == null) return;
            head.container   = chest;
            head.guiReceived = true;
            return;
        }

        if (--head.ticksRemaining > 0) return;

        queue.poll();

        head.callback.accept(head.container);
        if(head.pressSlot > 0) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.playerController.windowClick(
                    head.container.windowId, head.pressSlot, 0, 0, mc.thePlayer
            );
        }
    }

    private static ContainerChest getOpenChest(String expectedTitle) {
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiContainer)) return null;
        Container container = ((GuiContainer) Minecraft.getMinecraft().currentScreen).inventorySlots;
        if (!(container instanceof ContainerChest)) return null;
        String title = ColorUtils.stripColor(
                ((ContainerChest) container).getLowerChestInventory()
                        .getDisplayName().getUnformattedText()
        ).trim();
        return title.equals(expectedTitle) ? (ContainerChest) container : null;
    }


    private static class PendingWait {
        final String                   expectedTitle;
        final Consumer<ContainerChest> callback;
        int                            ticksRemaining;
        ContainerChest                 container;
        boolean                        guiReceived = false;
        int pressSlot;

        PendingWait(String expectedTitle, int tickDelay, int pressSlot, Consumer<ContainerChest> callback) {
            this.expectedTitle  = expectedTitle;
            this.pressSlot = pressSlot;
            this.ticksRemaining = Math.max(tickDelay, 1);
            this.callback       = callback;
        }
    }
}