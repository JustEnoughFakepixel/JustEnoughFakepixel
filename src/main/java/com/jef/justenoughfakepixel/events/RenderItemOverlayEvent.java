package com.jef.justenoughfakepixel.events;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderItemOverlayEvent extends Event {
    public final ItemStack stack;
    public final int x;
    public final int y;

    public RenderItemOverlayEvent(ItemStack stack, int x, int y) {
        this.stack = stack;
        this.x = x;
        this.y = y;
    }
}