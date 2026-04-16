package com.jef.justenoughfakepixel.features.storage;

import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.utils.ColorUtils;
import com.jef.justenoughfakepixel.utils.item.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class StorageParser {


    public static SContainer parseEchest(ContainerChest chest){
        String title = chest.getLowerChestInventory().getDisplayName().getUnformattedText();
        JefMod.logger.info("Title: " + title);
        int page;
        try{
            page = Integer.parseInt(
                    String.valueOf(title.charAt(title.indexOf(")") - 1))
            );
        } catch (NumberFormatException e) {
            JefMod.logger.info("Error While Getting page from " + title);
            return null;
        }

        HashMap<Integer, ItemStack> itemList = new HashMap<>();
        JefMod.logger.info("Chest Size: " + chest.getLowerChestInventory().getSizeInventory());
        for(int i = 9; i < chest.getLowerChestInventory().getSizeInventory();i++){
            ItemStack item = chest.getSlot(i).getStack();
            if(item == null) continue;
            JefMod.logger.info(item.getDisplayName() + " | " + chest.getInventory().indexOf(item));
            itemList.put(i - 9, item);
        }
        boolean empty = false;
        ItemStack stack = chest.getSlot(0).getStack();
        if(stack != null){
            if(ColorUtils.stripColor(stack.getDisplayName()).equalsIgnoreCase("close")){
                if(itemList.isEmpty()){
                    empty = true;
                }
            }
        }
        int renderH = 200;
        if(chest.getLowerChestInventory().getSizeInventory() <= 18){
            renderH = 70;
        }
        SContainer container = new SContainer(itemList,page,Type.ECHEST,renderH,false);
        container.empty = empty;
        return container;
    }


    public static SContainer parseBackpack(ContainerChest chest) {
        String title = chest.getLowerChestInventory().getDisplayName().getUnformattedText();
        JefMod.logger.info("Title: " + title);
        int page;

        try{
            int slashIndex = title.indexOf("/");
            int spaceIndex = title.lastIndexOf(" ", slashIndex);
            String numberPart = title.substring(spaceIndex + 1, slashIndex);
            page = Integer.parseInt(numberPart);
        } catch (Exception e) {
            JefMod.logger.info("Error While Getting page from " + title);
            return null;
        }

        HashMap<Integer, ItemStack> itemList = new HashMap<>();
        for(int i = 9; i< chest.inventorySlots.size();i++) {
            Slot slot = chest.getSlot(i);
            if (slot.getStack() != null) {
                itemList.put(i, slot.getStack());
            }
        }
        boolean empty = false;
        ItemStack stack = chest.getSlot(0).getStack();
        if(stack != null){
            if(ColorUtils.stripColor(stack.getDisplayName()).equalsIgnoreCase("close")){
                if(itemList.isEmpty()){
                    empty = true;
                }
            }
        }
        int renderH;
        String name = title.split(" ")[0];
        switch (name.toLowerCase()){
            case "small": renderH = 70; break;
            case "medium": renderH = 100; break;
            case "large": renderH = 140; break;
            case "greater": renderH = 170; break;
            default: renderH = 200; break;
        }
        SContainer container = new SContainer(itemList,page,Type.ECHEST,renderH,false);
        container.empty = empty;
        return container;
    }


    public static LinkedHashMap<String,SContainer> parseOverlay(ContainerChest chest, LinkedHashMap<String, SContainer> containers){
        LinkedHashMap<String,SContainer> chests = new LinkedHashMap<>();
        for(int j = 0;j < 9;j++){
            int slot = 9 + j;
            ItemStack stack = chest.getSlot(slot).getStack();
            int i = j+1;
            if(stack == null) continue;
            String id = Type.ECHEST.prefix + "-" + i;
            String title = stack.getDisplayName().replaceAll("§[0-9a-fk-or]", "");
            if (!title.contains("Ender") || !(Block.getBlockFromItem(stack.getItem()) instanceof BlockStainedGlassPane)) continue;

            int renderH = 200;
            SContainer container = new SContainer(new HashMap<>(),i,Type.ECHEST,renderH,title.contains("Locked"));
            if(containers.containsKey(id)){
                SContainer parent = containers.get(id);
                container.renderH = parent.renderH;
                container.slots = new HashMap<>(parent.slots);
            }
            chests.put(id,container);
        }

        for(int j = 0; j < 18;j++){
            int slot = 27 + j;
            ItemStack stack = chest.getSlot(slot).getStack();
            int i = j+1;
            if(stack == null) continue;
            String id = Type.BAG.prefix + "-" + i;
            String title = stack.getDisplayName();
            if(title.contains("Skin")) continue;
            int renderH;
            String sbID = ItemUtils.getInternalName(stack);
            String name = sbID.split("_")[0];
            switch (name.toLowerCase()){
                case "small": renderH = 70; break;
                case "medium": renderH = 100; break;
                case "large": renderH = 140; break;
                case "greater": renderH = 170; break;
                default: renderH = 200; break;
            }
            JefMod.logger.info(name + " | " + renderH);
            SContainer container = new SContainer(new HashMap<>(),i,Type.ECHEST,renderH,title.startsWith("Empty"));
            if(containers.containsKey(id)){
                SContainer parent = containers.get(id);
                container.renderH = parent.renderH;
                container.slots = new HashMap<>(parent.slots);
            }
            chests.put(id,container);
        }

        return chests;
    }

}
