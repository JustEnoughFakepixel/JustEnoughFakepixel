package com.jef.justenoughfakepixel.features.price.bzparser.parser;

import com.google.gson.GsonBuilder;
import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.features.price.bzparser.BazaarData;
import com.jef.justenoughfakepixel.features.price.bzparser.data.BazaarCategory;
import com.jef.justenoughfakepixel.features.price.bzparser.data.BazaarItem;
import com.jef.justenoughfakepixel.features.profile.GuiWaiter;
import com.jef.justenoughfakepixel.features.profile.ProfileParser;
import com.jef.justenoughfakepixel.utils.ColorUtils;
import com.jef.justenoughfakepixel.utils.item.ItemUtils;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BazaarParser {

    public static boolean isParsing = false;
    public static int highlightSlot = -1;

    private static final Stack<MenuState> stateStack = new Stack<>();
    public static final EnumMap<BazaarCategory, List<BazaarItem>> categorizedMap = new EnumMap<>(BazaarCategory.class);

    private static class MenuState {
        String title;
        Deque<Integer> slotsToVisit = new LinkedList<>();
        boolean isPopulated = false;
        int lastPolledSlot = -1; // Memory for accidental 'Go Back' clicks
        public MenuState(String title) { this.title = title; }
    }

    public static void startParsingSequence(ContainerChest startingChest) {
        categorizedMap.clear();
        stateStack.clear();
        isParsing = true;
        highlightSlot = -1;

        String title = getChestTitle(startingChest);
        stateStack.push(new MenuState(title));

        processNextStep(startingChest);
    }

    private static int findBackSlot(ContainerChest chest) {
        int size = chest.getLowerChestInventory().getSizeInventory();
        // Check the bottom row (last 9 slots) for the exact Go Back/Close item
        for (int i = size - 9; i < size; i++) {
            ItemStack stack = chest.getSlot(i).getStack();
            if (stack != null) {
                String name = ColorUtils.stripColor(stack.getDisplayName());
                if (name.contains("Go Back") || name.contains("Close")) {
                    return i;
                }
            }
        }
        // Fallback just in case
        return size - 5;
    }

    private static void processNextStep(ContainerChest chest) {
        if (chest == null || stateStack.isEmpty()) {
            finishParsing();
            return;
        }

        String currentTitle = getChestTitle(chest);
        MenuState currentState = stateStack.peek();

        // 1. Did we enter a NEW menu? (Title changed from what we expected)
        if (!currentState.title.equals(currentTitle)) {

            // Check if we accidentally (or purposefully) went back up the chain
            int foundIndex = -1;
            for (int i = stateStack.size() - 2; i >= 0; i--) {
                if (stateStack.get(i).title.equals(currentTitle)) {
                    foundIndex = i;
                    break;
                }
            }

            if (foundIndex != -1) {
                // We went back one or more levels! Pop down to that exact level.
                while (stateStack.size() > foundIndex + 1) {
                    stateStack.pop();
                }
                currentState = stateStack.peek();

                // Re-add the abandoned category/item to the front of the queue so we resume where we left off
                if (currentState.lastPolledSlot != -1) {
                    currentState.slotsToVisit.addFirst(currentState.lastPolledSlot);
                    currentState.lastPolledSlot = -1;
                }
            }
            // Is it an Item Menu? (No arrow in title, not root Bazaar)
            else if (!currentTitle.contains("➜") && !currentTitle.equals("Bazaar")) {
                parseItemMenu(chest, currentTitle);
                // Highlight the back button to return to the sub-category
                highlightSlotForUser(chest, findBackSlot(chest));
                return;
            } else {
                // It's a new Sub-Category (e.g. "Farming ➜ Wheat & Seeds"). Push it!
                currentState = new MenuState(currentTitle);
                stateStack.push(currentState);
            }
        }

        // 2. We are now in the Category/Sub-Category menu. Continue parsing slots.
        if (!currentState.isPopulated) {
            populateSlots(chest, currentState);
            currentState.isPopulated = true;
        }

        if (!currentState.slotsToVisit.isEmpty()) {
            int nextSlot = currentState.slotsToVisit.pollFirst();
            currentState.lastPolledSlot = nextSlot; // Save to memory before highlighting
            highlightSlotForUser(chest, nextSlot);
            return;
        }

        // Handle pagination within the sub-category
        int nextSlot = findNextPageSlot(chest);
        if (nextSlot != -1) {
            currentState.isPopulated = false;
            highlightSlotForUser(chest, nextSlot);
            return;
        }

        // 3. Sub-category finished: Pop and go back up the chain
        stateStack.pop();
        if (stateStack.isEmpty()) {
            finishParsing();
        } else {
            highlightSlotForUser(chest, findBackSlot(chest));
        }
    }

    private static void highlightSlotForUser(ContainerChest chest, int slot) {
        if (slot == -1) {
            processNextStep(chest);
            return;
        }
        highlightSlot = slot;
        // Use wildcard "*" because the Bazaar title changes dynamically on every click!
        GuiWaiter.waitForUserAction("*", chest.windowId, BazaarParser::processNextStep);
    }

    private static void parseItemMenu(ContainerChest chest, String title) {
        String sbId = title;
        ItemStack centerItem = chest.getSlot(13).getStack();
        if (centerItem != null) {
            String id = ItemUtils.getInternalName(centerItem);
            if (id != null && !id.isEmpty()) sbId = id;
        }

        float instaBuy = -1, instaSell = -1;
        for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
            ItemStack stack = chest.getSlot(i).getStack();
            if (stack == null) continue;
            String name = ColorUtils.stripColor(stack.getDisplayName());
            if (name.contains("Buy Instantly")) instaBuy = extractPriceFromLore(stack);
            else if (name.contains("Sell Instantly")) instaSell = extractPriceFromLore(stack);
        }

        BazaarCategory mainCat = BazaarCategory.ODDITIES;
        if (stateStack.size() > 1) {
            String catName = stateStack.get(1).title.replaceAll(".*➜", "").trim();
            for (BazaarCategory cat : BazaarCategory.values()) {
                if (cat.name.equals(catName)) { mainCat = cat; break; }
            }
        }
        categorizedMap.computeIfAbsent(mainCat, k -> new ArrayList<>()).add(new BazaarItem(sbId, instaBuy, instaSell));
    }

    private static void populateSlots(ContainerChest chest, MenuState state) {
        int size = chest.getLowerChestInventory().getSizeInventory();

        // Scan all slots except the very bottom row (last 9 slots) which hold controls
        for (int slot = 0; slot < size - 9; slot++) {
            ItemStack stack = chest.getSlot(slot).getStack();
            if (stack == null) continue;

            // Check if the item actually has a valid internal Skyblock ID
            String sbId = ItemUtils.getInternalName(stack);
            if (sbId != null && !sbId.trim().isEmpty()) {
                state.slotsToVisit.add(slot);
            }
        }
    }

    private static int findNextPageSlot(ContainerChest chest) {
        for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
            ItemStack stack = chest.getSlot(i).getStack();
            if (stack != null && ColorUtils.stripColor(stack.getDisplayName()).contains("Next Page")) return i;
        }
        return -1;
    }

    private static float extractPriceFromLore(ItemStack stack) {
        for (String lore : ProfileParser.getLore(stack)) {
            String clean = ColorUtils.stripColor(lore).trim();
            if (clean.contains("per unit:") || clean.startsWith("Price:")) {
                try { return Float.parseFloat(clean.replaceAll("[^0-9.]", "")); } catch (Exception ignored) {}
            }
        }
        return -1;
    }

    private static void finishParsing() {
        isParsing = false;
        highlightSlot = -1;
        writeToJson(new BazaarData(categorizedMap));
    }

    public static void writeToJson(BazaarData data) {
        File file = new File(JefConfig.configDirectory, "bzData.json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(data));
        } catch (IOException e) { JefMod.logger.info("Error writing bzData.json"); }
    }

    private static String getChestTitle(ContainerChest chest) {
        return ColorUtils.stripColor(chest.getLowerChestInventory().getDisplayName().getUnformattedText()).trim();
    }
}