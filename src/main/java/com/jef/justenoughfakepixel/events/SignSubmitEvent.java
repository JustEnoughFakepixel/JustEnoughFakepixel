package com.jef.justenoughfakepixel.events;

import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SignSubmitEvent extends Event {
    public final GuiEditSign sign;
    public final String[] lines;

    public SignSubmitEvent(GuiEditSign sign, String[] lines) {
        this.sign = sign;
        this.lines = lines;
    }
}
