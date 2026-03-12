package com.jef.justenoughfakepixel.utils;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Comparator;
import java.util.List;

public class TablistParser {

    private static ScoreboardUtils.Location currentLocation = ScoreboardUtils.Location.NONE;

    public static ScoreboardUtils.Location getCurrentLocation() {
        return currentLocation;
    }

    private static final int TICK_INTERVAL = 20;
    private int tickCounter = 0;

    private static final Ordering<NetworkPlayerInfo> PLAYER_ORDERING = Ordering.from(new PlayerComparator());

    private static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
        @Override
        public int compare(NetworkPlayerInfo o1, NetworkPlayerInfo o2) {
            ScorePlayerTeam t1 = o1.getPlayerTeam();
            ScorePlayerTeam t2 = o2.getPlayerTeam();
            return ComparisonChain.start()
                    .compareTrueFirst(
                            o1.getGameType() != WorldSettings.GameType.SPECTATOR,
                            o2.getGameType() != WorldSettings.GameType.SPECTATOR)
                    .compare(t1 != null ? t1.getRegisteredName() : "",
                             t2 != null ? t2.getRegisteredName() : "")
                    .compare(o1.getGameProfile().getName(), o2.getGameProfile().getName())
                    .result();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if ((tickCounter = (tickCounter + 1) % TICK_INTERVAL) != 0) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        GuiPlayerTabOverlay tab = mc.ingameGUI.getTabList();
        List<NetworkPlayerInfo> infos = PLAYER_ORDERING.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());

        boolean inServerSection = false;

        for (NetworkPlayerInfo info : infos) {
            String raw = tab.getPlayerName(info);
            if (raw == null || raw.isEmpty()) continue;

            if (raw.contains("§3§l Server Info§r")) {
                inServerSection = true;
                continue;
            } else if (raw.contains("§6§lAccount Info§r") || raw.contains("§2§lPlayer Stats§r")) {
                inServerSection = false;
                continue;
            }

            if (!inServerSection) continue;

            String line = net.minecraft.util.StringUtils.stripControlCodes(raw).trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("Dungeon: ")) {
                currentLocation = ScoreboardUtils.Location.DUNGEON;
                return;
            }

            if (line.startsWith("Server: ")) {
                String s = line.substring(line.indexOf("Server: ") + 8).trim();
                int dashDigits = indexOfDashDigits(s);
                if (dashDigits >= 0) s = s.substring(0, dashDigits + 1);
                currentLocation = matchLocation(s);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        currentLocation = ScoreboardUtils.Location.NONE;
    }

    private static ScoreboardUtils.Location matchLocation(String s) {
        for (ScoreboardUtils.Location loc : ScoreboardUtils.Location.values()) {
            if (loc.main.isEmpty()) continue;
            if (loc.main.equals(s) || loc.sandbox.equals(s) || loc.alpha.equals(s))
                return loc;
        }
        return ScoreboardUtils.Location.NONE;
    }

    private static int indexOfDashDigits(String s) {
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '-' && Character.isDigit(s.charAt(i + 1)))
                return i;
        }
        return -1;
    }
}