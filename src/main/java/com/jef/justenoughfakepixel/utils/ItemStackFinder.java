package com.jef.justenoughfakepixel.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;


public class ItemStackFinder {

    public static ItemStack findItemStack(String itemId) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return null;
        
        for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
            if (stack != null && itemId.equals(ItemUtils.getInternalName(stack))) {
                return stack;
            }
        }
        
        for (ItemStack stack : mc.thePlayer.inventory.armorInventory) {
            if (stack != null && itemId.equals(ItemUtils.getInternalName(stack))) {
                return stack;
            }
        }
        
        return null;
    }
}
