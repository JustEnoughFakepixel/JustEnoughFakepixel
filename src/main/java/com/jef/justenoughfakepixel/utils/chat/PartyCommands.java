package com.jef.justenoughfakepixel.utils.chat;

import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.features.diana.DianaTracker;
import com.jef.justenoughfakepixel.features.dungeons.DungeonStats;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class PartyCommands {

    private static final long HELP_COOLDOWN_MS = 10_000L;
    private final Minecraft mc = Minecraft.getMinecraft();
    private long lastHelpMs = 0L;

    private static String getJefVersion() {
        return "JustEnoughFakepixel v" + JefMod.VERSION;
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!ChatUtils.isFromServer(event)) return;
        String msg = ChatUtils.clean(event);
        if (!ChatUtils.isPartyMessage(msg)) return;

        String body = ChatUtils.getPartyBody(msg);
        if (body == null) return;
        body = body.toLowerCase();

        if (body.startsWith("!pb")) {
            String[] parts = body.split("\\s+");
            String arg1 = parts.length >= 2 ? parts[1] : null;
            String arg2 = parts.length >= 3 ? parts[2] : null;
            if (arg1 == null) {
                respond("Usage: !pb <floor> | !pb <floor> br | !pb p1-p5");
                return;
            }
            respond(DungeonStats.getFormattedPb(arg1, arg2));
            return;
        }

        switch (body) {
            case "!jef":
                respond(getJefVersion());
                break;
            case "!burrows":
                respond(DianaTracker.getBorrowsMessage());
                break;
            case "!inq":
                respond(DianaTracker.getInqMessage());
                break;
            case "!mobs":
                respond(DianaTracker.getMobsMessage());
                break;
            case "!time":
                respond(DianaTracker.getTimeMessage());
                break;
            case "!chim":
                respond(DianaTracker.getChimMessage());
                break;
            case "!stick":
                respond(DianaTracker.getStickMessage());
                break;
            case "!relic":
                respond(DianaTracker.getRelicMessage());
                break;
            case "!loot":
                respond(DianaTracker.getLootMessage());
                break;
            case "!help": {
                long now = System.currentTimeMillis();
                if (now - lastHelpMs < HELP_COOLDOWN_MS) break;
                lastHelpMs = now;
                ChatUtils.sendMultilineMessage(DianaTracker.getHelpMessage());
                break;
            }
        }
    }

    private void respond(String msg) {
        ChatUtils.sendPartyMessage(msg);
    }
}