package com.jef.justenoughfakepixel.features.dungeons.caseopening;

import com.jef.justenoughfakepixel.DebugLogger;
import com.jef.justenoughfakepixel.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class GuiInterceptChest extends GuiContainer {

    private static final int SCAN_DELAY = 3;

    private int tickCount = 0;
    private boolean doneCollectingReward = false;
    private DungeonDropData.Rule rewardToOpen = null;

    private final ContainerChest container;
    private final DungeonDropData.Floor floor;
    private final DungeonDropData.CaseMaterial material;

    public GuiInterceptChest(ContainerChest container, DungeonDropData.Floor floor,
                             DungeonDropData.CaseMaterial material) {
        super(container);
        this.container = container;
        this.floor = floor;
        this.material = material;
        DebugLogger.log("[GuiInterceptChest] Initialized — floor=" + floor + ", material=" + material);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        tickCount++;

        if (tickCount < SCAN_DELAY) return;

        if (!doneCollectingReward) {
            doneCollectingReward = true;
            scanForReward();
        }

        if (rewardToOpen != null) {
            DebugLogger.log("[GuiInterceptChest] Launching animation for: " + rewardToOpen.item.name());
            Minecraft.getMinecraft().displayGuiScreen(
                    new CustomDropAnimationGui(rewardToOpen, floor, material));
        } else {
            DebugLogger.log("[GuiInterceptChest] No matching reward found — returning to chest GUI");
            Minecraft.getMinecraft().displayGuiScreen(ChestListener.originalGui);
        }
    }

    private void scanForReward() {
        IInventory lower = container.getLowerChestInventory();
        int dropCount = DungeonDropData.getDrops(material, floor).size();
        DebugLogger.log("[GuiInterceptChest] Scanning — floor=" + floor
                + ", material=" + material + ", possible drops=" + dropCount);

        for (int i = 10; i <= 16; i++) {
            ItemStack stack = lower.getStackInSlot(i);
            if (stack == null || stack.getItem() == null) continue;

            String itemId = ItemUtils.getEffectiveItemId(stack);
            if (itemId.isEmpty()) continue;
            DebugLogger.log("[GuiInterceptChest] Slot " + i + ": " + itemId);

            DungeonDropData.Rule found = DungeonDropData.getDrops(material, floor).stream()
                    .filter(r -> r.item.name().equals(itemId))
                    .findFirst()
                    .orElse(null);

            if (found == null) continue;

            if (rewardToOpen == null
                    || found.rarity < rewardToOpen.rarity
                    || (found.rarity == rewardToOpen.rarity
                    && found.item.name().compareTo(rewardToOpen.item.name()) < 0)) {
                rewardToOpen = found;
                DebugLogger.log("[GuiInterceptChest] New best reward: "
                        + rewardToOpen.item.name() + " (rarity " + rewardToOpen.rarity + ")");
            }
        }
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {}
    @Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {}
    @Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {}
    @Override protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {}
    @Override protected void mouseReleased(int mouseX, int mouseY, int state) {}
    @Override public void handleMouseInput() {}
}