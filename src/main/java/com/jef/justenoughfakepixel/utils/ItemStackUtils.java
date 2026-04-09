// Credit: Skytils (https://github.com/Skytils/SkytilsMod) (AGPLv3)

package com.jef.justenoughfakepixel.utils;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.events.RenderItemOverlayEvent;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class ItemStackUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static void drawTip(String tip, int x, int y) {
        FontRenderer fr = mc.fontRendererObj;
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        fr.drawStringWithShadow(tip, x + 17 - fr.getStringWidth(tip), y + 9, 0xFFFFFF);
        GlStateManager.enableDepth();
    }

    @SubscribeEvent
    public void onItemOverlay(RenderItemOverlayEvent event) {
        if (JefConfig.feature == null) return;
        String tip = getTip(event.stack);
        if (tip == null || tip.isEmpty()) return;
        drawTip(tip, event.x, event.y);
    }

    private String getTip(ItemStack stack) {
        if (JefConfig.feature == null) return null;

        if (JefConfig.feature.misc.partyFinderFloorTip && isInContainer("Party Finder")) {
            if (stack.getItem() == Items.skull) {
                return getPartyFinderFloor(stack);
            }
            return null;
        }

        if (!JefConfig.feature.misc.itemStackTips) return null;

        String id = ItemUtils.getInternalName(stack);
        if (id.isEmpty()) return null;

        if (id.equals("ENCHANTED_BOOK")) {
            return getEnchantLevel(stack);
        }

        if (isInContainer("Catacombs Gate")) {
            return getDungeonFloor(id);
        }

        return null;
    }

    /**
     * Extracts the last word of the display name and converts it from roman numeral to integer.
     */
    private String getEnchantLevel(ItemStack stack) {
        String name = ColorUtils.stripColor(stack.getDisplayName());
        if (name.isEmpty()) return null;
        String[] parts = name.split(" ");
        String last = parts[parts.length - 1];
        // roman numerals are all uppercase letters only
        if (last.isEmpty() || !last.chars().allMatch(c -> "IVXLCDM".indexOf(c) >= 0)) return null;
        if (!RomanNumeralParser.isValid(last)) return null;
        int level = RomanNumeralParser.parse(last);
        return level > 0 ? String.valueOf(level) : null;
    }

    private String getDungeonFloor(String id) {
        String suffix = null;
        if (id.startsWith("MASTER_CATACOMBS_PASS_")) {
            suffix = id.substring("MASTER_CATACOMBS_PASS_".length());
        } else if (id.startsWith("CATACOMBS_PASS_")) {
            suffix = id.substring("CATACOMBS_PASS_".length());
        }
        if (suffix == null) return null;
        try {
            int floor = Integer.parseInt(suffix) - 3;
            return floor > 0 ? String.valueOf(floor) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getPartyFinderFloor(ItemStack stack) {
        java.util.List<String> lore = ItemUtils.getLoreLines(stack);
        if (lore.isEmpty()) return null;

        boolean master = false;
        String floorLabel = null;

        for (String line : lore) {
            String stripped = ColorUtils.stripColor(line).trim();

            if (stripped.startsWith("Dungeon: ")) {
                String dungeon = stripped.substring("Dungeon: ".length()).trim();
                master = dungeon.equals("Master Catacombs");

            } else if (stripped.startsWith("Floor: ")) {
                String value = stripped.substring("Floor: ".length()).trim();
                if (value.equals("Entrance")) {
                    floorLabel = "ENT";
                } else if (value.startsWith("Floor ")) {
                    String numeral = value.substring("Floor ".length()).trim();
                    int n = RomanNumeralParser.parse(numeral);
                    if (n >= 1 && n <= 7) floorLabel = String.valueOf(n);
                }
            }
        }

        if (floorLabel == null) return null;
        if (floorLabel.equals("ENT")) return "ENT";
        return (master ? "M" : "F") + floorLabel;
    }

    private boolean isInContainer(String name) {
        if (!(mc.currentScreen instanceof GuiChest)) return false;
        ContainerChest cc = (ContainerChest) ((GuiChest) mc.currentScreen).inventorySlots;
        return name.equals(ColorUtils.stripColor(cc.getLowerChestInventory().getDisplayName().getUnformattedText()));
    }

}