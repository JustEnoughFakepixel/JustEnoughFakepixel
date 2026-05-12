package com.jef.justenoughfakepixel.features.price.ahparser.parser;

import com.google.gson.GsonBuilder;
import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.features.price.ahparser.AuctionData;
import com.jef.justenoughfakepixel.features.price.ahparser.data.AuctionItem;
import com.jef.justenoughfakepixel.features.profile.GuiWaiter;
import com.jef.justenoughfakepixel.features.profile.ProfileParser;
import com.jef.justenoughfakepixel.utils.ColorUtils;
import com.jef.justenoughfakepixel.utils.item.ItemUtils;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class AuctionParser {

    public static boolean canClick = false;
    public static int highlightSlot = -1;

    // --- State Machine ---
    public enum AhCategory {
        // Format: (Button Slot ID, Glass Pane Color Metadata)
        WEAPONS(0, 1),       // 1 = Orange
        ARMOR(9, 11),         // 3 = Light Blue
        ACCESSORIES(18, 13), // 13 = Dark Green
        CONSUMABLES(27, 14), // 14 = Red
        BLOCKS(36, 12),      // 12 = Brown (Change this to 7 or 4 if it infinitely loops on blocks!)
        TOOLS_MISC(45, 10);  // 10 = Purple (As seen in your screenshot)

        public final int buttonSlot;
        public final int paneMeta;

        AhCategory(int buttonSlot, int paneMeta) {
            this.buttonSlot = buttonSlot;
            this.paneMeta = paneMeta;
        }
    }

    public static AhCategory[] CATEGORIES = AhCategory.values();
    public static int currentCategoryIndex = 0;

    // Master map to hold ALL items across all categories
    public static HashMap<String, AuctionItem> masterItemMap = new HashMap<>();

    // Filters
    private static final int RARITY_FILTER_SLOT = 51;
    private static final int BIN_FILTER_SLOT = 52;

    public static void startParsingSequence(ContainerChest startingChest) {
        currentCategoryIndex = 0;
        masterItemMap.clear();
        checkAndEnforceFilters(startingChest);
    }

    // Called by the "Next Category" button in the GUI
    public static void forceNextCategory(ContainerChest chest) {
        GuiWaiter.clearQueue(); // Cancel any current waits
        highlightSlot = -1;
        currentCategoryIndex++;

        if (currentCategoryIndex < CATEGORIES.length) {
            JefMod.logger.info("[AH Parser] Manually skipped to next category: " + CATEGORIES[currentCategoryIndex].name());
            checkAndEnforceFilters(chest);
        } else {
            JefMod.logger.info("[AH Parser] No more categories left! Ending parse.");
            // We ran out of categories, so finish and save!
            writeToJson(new AuctionData(masterItemMap.values()));
        }
    }

    private static void checkAndEnforceFilters(ContainerChest chest) {
        if (chest == null || currentCategoryIndex >= CATEGORIES.length) return;

        AhCategory currentCat = CATEGORIES[currentCategoryIndex];

        // 1. Check Category (Always checking slot 1 to see if the color matches the current category)
        ItemStack paneStack = chest.getSlot(1).getStack();
        boolean isCategoryActive = paneStack != null && paneStack.getItemDamage() == currentCat.paneMeta;

        if (!isCategoryActive) {
            JefMod.logger.info("[AH Parser] Not in " + currentCat.name() + " (Expected Color: " + currentCat.paneMeta + "). Forcing user to click it.");
            highlightAndYield(chest, currentCat.buttonSlot);
            return;
        }

        // 2. Check BIN Filter
        ItemStack binFilter = chest.getSlot(BIN_FILTER_SLOT).getStack();
        boolean isBinOnly = isFilterSelected(binFilter, "BIN Only");

        if (!isBinOnly) {
            JefMod.logger.info("[AH Parser] BIN Filter not set to 'BIN Only'. Forcing user to click.");
            highlightAndYield(chest, BIN_FILTER_SLOT);
            return;
        }

        // 3. Check Rarity Filter
        ItemStack rarityFilter = chest.getSlot(RARITY_FILTER_SLOT).getStack();
        boolean isAllRarity = isFilterSelected(rarityFilter, "No filter");

        if (!isAllRarity) {
            JefMod.logger.info("[AH Parser] Rarity Filter not set to 'No filter'. Forcing user to click.");
            highlightAndYield(chest, RARITY_FILTER_SLOT);
            return;
        }

        // 4. All checks passed!
        JefMod.logger.info("[AH Parser] All filters correct for " + currentCat.name() + "! Starting parse.");
        highlightSlot = -1;
        processAH(chest);
    }

    private static void highlightAndYield(ContainerChest chest, int slotIndex) {
        Slot slotObj = chest.inventorySlots.get(slotIndex);
        if (slotObj != null) {
            highlightSlot = slotObj.slotNumber;
        }
        GuiWaiter.waitForUserAction("Auction Browser", chest.windowId, AuctionParser::checkAndEnforceFilters);
    }

    private static boolean isFilterSelected(ItemStack stack, String target) {
        if (stack == null) return false;
        for (String lore : ProfileParser.getLore(stack)) {
            String unformatted = ColorUtils.stripColor(lore).trim();
            if (unformatted.endsWith(target) && !unformatted.equals(target)) {
                return true;
            }
        }
        return false;
    }

    public static void processAH(ContainerChest chest) {
        // Calculate which slot to highlight when we run out of pages.
        // We set the "backSlot" to be the button for the NEXT category.
        int targetSlotOnFinish = 49; // Default to 'Close' button slot if we are on the very last category
        if (currentCategoryIndex + 1 < CATEGORIES.length) {
            targetSlotOnFinish = CATEGORIES[currentCategoryIndex + 1].buttonSlot;
        }

        GuiWaiter.waitForManualPaged(
                "Auction Browser", 2,
                53, "Next Page",
                targetSlotOnFinish, "Auction Browser",
                page -> {
                    // Extract items from this page and merge them into the master map
                    for (AuctionItem item : parseItems(page)) {
                        if (masterItemMap.containsKey(item.SKYBLOCK_ID)) {
                            masterItemMap.get(item.SKYBLOCK_ID).itemPrices.addAll(item.itemPrices);
                        } else {
                            masterItemMap.put(item.SKYBLOCK_ID, item);
                        }
                    }
                },
                returnChest -> {
                    // This is triggered when the user clicks the next category button at the end of the pages
                    currentCategoryIndex++;
                    if (currentCategoryIndex < CATEGORIES.length) {
                        JefMod.logger.info("[AH Parser] Proceeding to next category: " + CATEGORIES[currentCategoryIndex].name());
                        checkAndEnforceFilters(returnChest);
                    } else {
                        JefMod.logger.info("[AH Parser] Finished parsing ALL categories! Total unique items: " + masterItemMap.size());
                        writeToJson(new AuctionData(masterItemMap.values()));
                        highlightSlot = -1; // Clear any leftover highlights
                    }
                }
        );
    }

    public static void writeToJson(AuctionData data) {
        File file1 = new File(JefConfig.configDirectory, "ahData.json");
        if (!file1.exists()) {
            try { file1.createNewFile(); }
            catch (IOException e) { JefMod.logger.info("Error creating ahData.json"); return; }
        }
        try(FileWriter writer = new FileWriter(file1)){
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(data));
        }catch (IOException e) { JefMod.logger.info("Error writing to ahData.json");
        }
    }

    private static Collection<AuctionItem> parseItems(ContainerChest chest) {
        int[] SLOTS = new int[]{11,12,13,14,15,16,20,21,22,23,24,25,29,30,31,32,33,34,38,39,40,41,42,43};
        HashMap<String,AuctionItem> itemMap = new HashMap<>();
        for(int slot : SLOTS){
            ItemStack stack  = chest.getSlot(slot).getStack();
            if(stack == null) continue;

            String SB_ID = ItemUtils.getInternalName(stack);
            if(SB_ID == null || SB_ID.isEmpty()) continue;

            int price = -1;
            for(String lore :  ProfileParser.getLore(stack)){
                if(lore.startsWith("Buy it now")){
                    String line = lore.replaceAll("[^0-9]", "");
                    try{
                        price = Integer.parseInt(line);
                    } catch (NumberFormatException e) {
                        JefMod.logger.info("Error parsing price from: '" + lore + "'");
                    }
                }
            }
            if(price < 0) continue;

            List<Integer> prices = new ArrayList<>();
            prices.add(price);
            if(itemMap.containsKey(SB_ID)){
                prices.addAll(itemMap.get(SB_ID).itemPrices);
            }
            itemMap.put(SB_ID,new AuctionItem(SB_ID,prices));
        }
        return itemMap.values();
    }
}