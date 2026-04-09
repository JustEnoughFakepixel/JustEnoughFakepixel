package com.jef.justenoughfakepixel.features.storage;

import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;


@RegisterEvents
public class StorageListener {

    public static HashMap<String, Boolean> scanned = new HashMap<>();
    public static boolean scannedStorage = false;
    public static String[] types = new String[]{"Greater","Jumbo","Small","Medium","Large"};
    @SubscribeEvent
    public void onClose(GuiOpenEvent event){
        if(!JefConfig.feature.storage.enabled) return;
        if(event.gui == null){
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if(screen == null) return;
            if(screen instanceof GuiChest ){
                GuiChest chest = (GuiChest) screen;
                ContainerChest container = (ContainerChest) chest.inventorySlots;
                IInventory inv = container.getLowerChestInventory();
                String title = inv.getDisplayName().getUnformattedText();

                if(title.equals("Storage")){
                    scannedStorage = false;
                }

                if(title.startsWith("Ender") && title.endsWith(")") && title.contains("Chest") && title.contains("Page")){
                    char page = title.charAt((title.indexOf(')') - 1));
                    int pageID;
                    try {
                        pageID = Integer.parseInt(String.valueOf(page));
                    } catch (NumberFormatException e) {
                        JefMod.logger.info("Error While Trying to Parse " + page);
                        return;
                    }
                    scanned.put("echest-" + pageID,false);
                }
                if(title.contains("Backpack")){
                    boolean forward = false;
                    for(String s : types){
                        if (title.contains(s)) {
                            forward = true;
                            break;
                        }
                    }
                    if(forward){
                        String[] words = title.split(" ");
                        String temp = words[words.length - 1];
                        String pageStr = temp.split("/")[0];
                        int pageID;
                        try{
                            pageID = Integer.parseInt(pageStr);
                        } catch (NumberFormatException e) {
                            JefMod.logger.info("Error While Trying to Parse " + pageStr);
                            return;
                        }
                        scanned.put("bag-" + pageID,false);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void onOpen(GuiScreenEvent.BackgroundDrawnEvent e){
        if(!JefConfig.feature.storage.enabled) return;
        GuiScreen gui = e.gui;
        if(!(gui instanceof GuiChest)) return;
        GuiChest gC = (GuiChest)gui;
        ContainerChest container = (ContainerChest) gC.inventorySlots;
        String title = container.getLowerChestInventory().getDisplayName().getUnformattedText();
        if(title.equals("Storage")){
            scannedStorage = true;
            StorageOverlay.openOverlay(container);
        }
        if(title.startsWith("Ender") && title.endsWith(")") && title.contains("Chest") && title.contains("Page")){
            char page = title.charAt((title.indexOf(')') - 1));
            int pageID;
            try {
                pageID = Integer.parseInt(String.valueOf(page));
            } catch (NumberFormatException exception) {
                JefMod.logger.info("Error While Trying to Parse " + page);
                return;
            }
            String id = "echest-" + pageID;
            if(!scanned.containsKey(id) || !scanned.get(id)) {
                scanned.put("echest-" + pageID, true);
                JefMod.logger.info("Parsing EnderChest of title " + title);
                StorageOverlay.updateContainer(container, true);
            }
        }
        if(title.contains("Backpack")){
            boolean forward = false;
            for(String s : types){
                if (title.contains(s)) {
                    forward = true;
                    break;
                }
            }
            if(forward){
                String[] words = title.split(" ");
                String temp = words[words.length - 1];
                String pageStr = temp.split("/")[0];
                int pageID;
                try{
                    pageID = Integer.parseInt(pageStr);
                } catch (NumberFormatException exception) {
                    JefMod.logger.info("Error While Trying to Parse " + pageStr);
                    return;
                }
                String id = "bag-" + pageID;
                if(!scanned.containsKey(id) || !scanned.get(id)) {
                    scanned.put(id, true);
                    JefMod.logger.info("Parsing Bag of title " + title);
                    StorageOverlay.updateContainer(container, false);
                }
            }
        }
    }


}
