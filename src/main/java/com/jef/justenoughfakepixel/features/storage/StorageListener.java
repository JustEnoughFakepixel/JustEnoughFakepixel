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

    @SubscribeEvent
    public void onClose(GuiOpenEvent e){
        parsed = false;
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
            if(parsed){
                return;
            }
            if(title.equals("Storage")){
                JefMod.logger.info("Opening GUI");
                parsed = StorageOverlay.openGUI(chest);
                return;
            }
            if (title.startsWith("Ender Chest")) {
                JefMod.logger.info("Analyzing EChest");
                SContainer container = StorageParser.parseEchest(chest);
                parsed = container != null;
                if(container == null) return;
                StorageOverlay.containers.put(container.id,container);
                StorageSaving.saveStorageData(StorageOverlay.containers.values());
                return;
            }
            String[] words = title.split(" ");
            if(words.length < 2) return;
            if(words[1].equalsIgnoreCase("backpack")){
                JefMod.logger.info("Analyzing Bag");
                SContainer container = StorageParser.parseBackpack(chest);
                parsed = container != null;
                if(container == null) return;
                StorageOverlay.containers.put(container.id,container);
                StorageSaving.saveStorageData(StorageOverlay.containers.values());
            }
        }
    }

}
