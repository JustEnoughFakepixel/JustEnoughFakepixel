package com.jef.justenoughfakepixel.features.storage.utils;

import com.jef.justenoughfakepixel.DebugLogger;
import com.jef.justenoughfakepixel.features.storage.data.ItemData;
import lombok.AllArgsConstructor;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

@AllArgsConstructor
public class SContainer {

    public HashMap<Integer, ItemData> slots;
    public String id;
    public int page;
    public Type type;
    public boolean locked;
    public int renderW;
    public int renderH;
    public int slotCount;
    public boolean empty = false;

    public SContainer(HashMap<Integer, ItemStack> slots, int page, Type type, int renderH, boolean locked) {
        this.id = type.prefix + "-" + page;
        this.page = page;
        this.type = type;

        DebugLogger.log("Creating container " + this.id + " with " + slots.size() + " items");
        this.slots = convertSlots(slots);
        DebugLogger.log("After serialization: " + this.slots.size() + " items");

        this.renderH = renderH;
        this.renderW = 307;
        this.slotCount = calculateSlotCount(renderH);
        DebugLogger.log(renderW + " | " + slotCount);
        this.locked = locked;
    }

    private static int calculateSlotCount(int renderH) {
        return StorageUtils.getSlotCountFromRenderHeight(renderH);
    }

    private HashMap<Integer, ItemData> convertSlots(HashMap<Integer, ItemStack> slots) {
        HashMap<Integer, ItemData> items = new HashMap<>();
        slots.keySet().forEach(key -> {
            ItemStack stack = slots.get(key);
            ItemData data = ItemData.fromItemStack(stack);
            if (data != null) {
                items.put(key, data);
            }
        });
        return items;
    }

    public ItemStack getStack(Integer key) {
        ItemData data = slots.get(key);
        if (data == null) return null;
        return data.toItemStack();
    }

    public String getDisplayName(Integer key) {
        ItemData data = slots.get(key);
        if (data == null) return "";
        return data.displayName;
    }

    public void setStack(Integer key, ItemStack stack) {
        if (stack == null) {
            slots.remove(key);
        } else {
            ItemData data = ItemData.fromItemStack(stack);
            if (data != null) {
                slots.put(key, data);
            }
        }
    }

}
