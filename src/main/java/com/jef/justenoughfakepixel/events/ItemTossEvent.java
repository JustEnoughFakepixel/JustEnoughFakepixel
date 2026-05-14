package com.jef.justenoughfakepixel.events;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ItemTossEvent extends Event {
    public final ItemStack item;
    public final boolean dropAll;

    public ItemTossEvent(ItemStack item, boolean dropAll) {
        this.item = item;
        this.dropAll = dropAll;
    }
}