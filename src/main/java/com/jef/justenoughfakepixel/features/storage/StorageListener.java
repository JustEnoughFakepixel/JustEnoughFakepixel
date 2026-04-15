package com.jef.justenoughfakepixel.features.storage;

import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class StorageListener {


    public boolean parsed = false;
    public long lastParse = 0;
    public long PARSE_INTERVAL = 10000;

    @SubscribeEvent
    public void onClose(GuiOpenEvent e){
        if(e.gui == null){
            lastParse = 0;
        }
    }

    @SubscribeEvent
    public void onOpen(GuiScreenEvent.BackgroundDrawnEvent event){
        if(!JefConfig.feature.storage.enabled) return;
        GuiScreen screen = event.gui;
        if(screen instanceof GuiChest){
            ContainerChest chest = (ContainerChest) ((GuiChest)screen).inventorySlots;
            String title = chest.getLowerChestInventory().getDisplayName().getUnformattedText();
            if(title == null) return;
            if(title.isEmpty()) return;
            if(title.equals("Storage")){
                if(parsed){
                    if(System.currentTimeMillis() - lastParse > PARSE_INTERVAL){
                        parsed = false;
                    }
                }
                if(parsed){
                    return;
                }
                JefMod.logger.info("Continuing from Parsed");

                    lastParse = System.currentTimeMillis();
                    JefMod.logger.info("Parse Time: " + lastParse);
                    parsed = StorageOverlay.openGUI(chest);
            }
            if (title.startsWith("Enderchest")) {
                SContainer container = StorageParser.parseEchest(chest);
                if(container == null) return;
                StorageOverlay.containers.put(container.id,container);
                StorageSaving.saveStorageData(StorageOverlay.containers.values());
            }
            if(title.split(" ")[1].equalsIgnoreCase("backpack")){
                SContainer container = StorageParser.parseBackpack(chest);
                if(container == null) return;
                StorageOverlay.containers.put(container.id,container);
                StorageSaving.saveStorageData(StorageOverlay.containers.values());
            }
        }
    }

}
