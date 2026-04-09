package com.jef.justenoughfakepixel.events;

import lombok.Getter;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraftforge.fml.common.eventhandler.Event;

@Getter
public class PacketReceiveTimeUpdateEvent extends Event {

    private final S03PacketTimeUpdate packet;

    public PacketReceiveTimeUpdateEvent(S03PacketTimeUpdate packet) {
        this.packet = packet;
    }

}