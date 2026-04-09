package com.jef.justenoughfakepixel.events;

import lombok.Getter;
import net.minecraftforge.fml.common.eventhandler.Event;

@Getter
public class ActionBarXpGainEvent extends Event {

    private final String formattedText;

    public ActionBarXpGainEvent(String formattedText) {
        this.formattedText = formattedText;
    }

}