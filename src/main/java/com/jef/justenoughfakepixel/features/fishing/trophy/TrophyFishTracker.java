package com.jef.justenoughfakepixel.features.fishing.trophy;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.ChatUtils;
import com.jef.justenoughfakepixel.utils.ColorUtils;
import com.jef.justenoughfakepixel.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RegisterEvents
public class TrophyFishTracker {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final String ODGER_TITLE = "Trophy Fishing";
    private static final Pattern TROPHY_CHAT = Pattern.compile(
        "§6♔ §r§6§lTROPHY FISH! §r§fYou caught an? §r" +
        "(?<displayName>§[0-9a-fA-F](?:§k)?[\\w -]+?) §r" +
        "(?<displayRarity>§[0-9a-fA-F]§l\\w+)§r§f!"
    );

    private static final Pattern RANK_CAUGHT = Pattern.compile(
        "^(?:§5§o)?§.([A-Za-z]+) §a✔§7 \\((\\d+)\\)$"
    );

    private static final Pattern RANK_EMPTY = Pattern.compile(
        "^(?:§5§o)?§.([A-Za-z]+) §c✖$"
    );
    private static final Pattern DISCOVERED = Pattern.compile("§aDiscovered");
    private static final Pattern BRONZE_LINE = Pattern.compile("^(?:§5§o)?§8Bronze.*");

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!ChatUtils.isFromServer(event)) return;
        if (JefConfig.feature == null) return;

        Matcher m = TROPHY_CHAT.matcher(event.message.getFormattedText());
        if (!m.find()) return;

        String coloredName   = m.group("displayName");    // e.g. "§9Lavahorse"
        String coloredRarity = m.group("displayRarity");  // e.g. "§6§lGOLD"

        String fishName  = ColorUtils.stripColor(coloredName).trim();
        String rarityStr = ColorUtils.stripColor(coloredRarity).trim();

        TrophyRarity rarity = TrophyRarity.fromDisplayName(rarityStr);
        if (rarity == null) return;

        TrophyFishStorage storage = TrophyFishStorage.getInstance();
        int newCount = storage.incrementCount(fishName, rarity);
        storage.save();

        boolean hideBronze = JefConfig.feature.fishing.trophyBronzeHider
                && rarity == TrophyRarity.BRONZE && newCount > 1;
        boolean hideSilver = JefConfig.feature.fishing.trophySilverHider
                && rarity == TrophyRarity.SILVER && newCount > 1;
        if (hideBronze || hideSilver) {
            event.setCanceled(true);
            return;
        }

        if (!JefConfig.feature.fishing.trophyChatModify) return;

        int total = storage.getTotal(fishName);
        String countPart = newCount == 1
                ? "§c§lFIRST! §r"
                : "§7" + newCount + ordinal(newCount) + " §r";

        String newMsg = "§6♔ §6§lTROPHY FISH! " + countPart
                + coloredRarity + " " + coloredName
                + " §7(§e" + String.format("%,d", total) + " total§7)";

        event.setCanceled(true);
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(newMsg));
        }
    }

    @SubscribeEvent
    public void onGuiDraw(GuiScreenEvent.BackgroundDrawnEvent event) {
        ContainerChest container = getChestContainer(event.gui);
        if (container == null) return;
        if (!ODGER_TITLE.equals(containerName(container))) return;

        scanOdger(container);
    }

    private void scanOdger(ContainerChest container) {
        if (mc.thePlayer == null) return;
        TrophyFishStorage storage = TrophyFishStorage.getInstance();
        boolean changed = false;

        for (Slot slot : container.inventorySlots) {
            if (slot.inventory == mc.thePlayer.inventory) continue;
            ItemStack item = slot.getStack();
            if (item == null) continue;

            String fishName = ColorUtils.stripColor(item.getDisplayName().replace("§k", "")).trim();
            if (fishName.isEmpty()) continue;

            boolean hasRarityLine = false;

            for (String line : ItemUtils.getLoreLines(item)) {
                Matcher caught = RANK_CAUGHT.matcher(line);
                if (caught.find()) {
                    hasRarityLine = true;
                    TrophyRarity rarity = TrophyRarity.fromDisplayName(caught.group(1));
                    if (rarity == null) continue;
                    int amount = Integer.parseInt(caught.group(2));
                    if (storage.getCount(fishName, rarity) != amount) {
                        storage.setCount(fishName, rarity, amount);
                        changed = true;
                    }
                    continue;
                }

                Matcher empty = RANK_EMPTY.matcher(line);
                if (empty.find()) {
                    hasRarityLine = true;
                    TrophyRarity rarity = TrophyRarity.fromDisplayName(empty.group(1));
                    if (rarity == null) continue;
                    if (!storage.getFish().containsKey(fishName)) {
                        storage.setCount(fishName, rarity, 0);
                        changed = true;
                    }
                }
            }

            if (hasRarityLine && !storage.getFish().containsKey(fishName)) {
                storage.getFish().put(fishName, new java.util.LinkedHashMap<>());
                changed = true;
            }
        }

        if (changed) storage.save();
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (JefConfig.feature == null || !JefConfig.feature.fishing.trophyOdgerTotal) return;
        if (!ODGER_TITLE.equals(getOpenContainerName())) return;
        if (event.toolTip == null || event.itemStack == null) return;

        // Only enrich tooltips for discovered trophy fish
        List<String> lore = ItemUtils.getLoreLines(event.itemStack);
        boolean discovered = lore.stream().anyMatch(l -> DISCOVERED.matcher(l).find());
        if (!discovered) return;

        String fishName = ColorUtils.stripColor(event.itemStack.getDisplayName().replace("§k", "")).trim();
        TrophyFishStorage storage = TrophyFishStorage.getInstance();

        int total = storage.getTotal(fishName);
        if (total == 0) return;

        List<String> tip = event.toolTip;
        int bronzeIdx = -1;
        for (int i = 0; i < tip.size(); i++) {
            if (BRONZE_LINE.matcher(tip.get(i)).find()) { bronzeIdx = i; break; }
        }

        if (bronzeIdx >= 0) {
            TrophyRarity best = storage.getBestRarity(fishName);
            tip.add(bronzeIdx + 1, "");
            tip.add(bronzeIdx + 2, "§7Total: " + best.formatCode + String.format("%,d", total));
        }
    }


    private static ContainerChest getChestContainer(GuiScreen gui) {
        if (!(gui instanceof GuiChest)) return null;
        GuiChest chest = (GuiChest) gui;
        if (!(chest.inventorySlots instanceof ContainerChest)) return null;
        return (ContainerChest) chest.inventorySlots;
    }

    private static String containerName(ContainerChest cc) {
        return ColorUtils.stripColor(
                cc.getLowerChestInventory().getDisplayName().getUnformattedText());
    }

    private static String getOpenContainerName() {
        ContainerChest cc = getChestContainer(mc.currentScreen);
        return cc == null ? null : containerName(cc);
    }

    private static String ordinal(int n) {
        if (n % 100 >= 11 && n % 100 <= 13) return "th";
        switch (n % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
}