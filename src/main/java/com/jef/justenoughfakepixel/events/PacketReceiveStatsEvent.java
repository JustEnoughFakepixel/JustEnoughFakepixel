package com.jef.justenoughfakepixel.events;

import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PacketReceiveStatsEvent extends Event {
    public final S37PacketStatistics packet;
    public PacketReceiveStatsEvent(S37PacketStatistics packet) { this.packet = packet; }
}