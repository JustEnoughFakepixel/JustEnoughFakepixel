package com.jef.justenoughfakepixel.features.scoreboard;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BankParser
 *
 * Reads the bank balance from Fakepixel's tab-list widget — exactly the same
 * approach SkyHanni uses via TabWidget.BANK:
 *   "Bank: (?:\u00A7.)*(?<amount>[^\u00A7]+)(?:(?:\u00A7.)* \/ (?:\u00A7.)*(?<personal>.*))?"
 *
 * The tab-list "Account Info" section on Fakepixel mirrors Hypixel's layout:
 *   \u00A76\u00A7lAccount Info
 *     Bank: \u00A76249,381
 *     Purse: \u00A761,234
 *
 * This class is registered as a Forge event listener in JefMod.clientInit().
 * It ticks every 20 client ticks (~1 second) and caches the last seen values.
 */
public class BankParser {

    // ── Patterns (ported from SkyHanni TabWidget.BANK) ──────────────────────
    // Matches: "Bank: \u00A76249,381"  or  "Bank: \u00A76249,381 \u00A77/ \u00A760"
    private static final Pattern BANK_PATTERN = Pattern.compile(
            "Bank: (?:\u00A7.)*(?<amount>[^\u00A7\\s]+(?:\\s*[^\u00A7\\s]+)*)(?:(?:\u00A7.)* / (?:\u00A7.)*(?<personal>.*))?");

    private static final Pattern PURSE_PATTERN = Pattern.compile(
            "Purse: (?:\u00A7.)*(?<amount>[\\d,]+(?:\\.[\\d]+)?)");

    // ── Cached values ────────────────────────────────────────────────────────
    private static String cachedBank  = null;   // null = not seen yet
    private static String cachedPurse = null;

    public static String getBank()  { return cachedBank; }
    public static String getPurse() { return cachedPurse; }
    // ── Tick interval ────────────────────────────────────────────────────────
    private static final int TICK_INTERVAL = 20;
    private int tickCounter = 0;

    // ── Tab-list ordering (same as TablistParser in your mod) ────────────────
    private static final Ordering<NetworkPlayerInfo> PLAYER_ORDERING =
            Ordering.from(new PlayerComparator());

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

    // ── Event handlers ───────────────────────────────────────────────────────

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if ((tickCounter = (tickCounter + 1) % TICK_INTERVAL) != 0) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        parseTablist(mc);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        cachedBank  = null;
        cachedPurse = null;
    }

    // ── Core parser ──────────────────────────────────────────────────────────

    private static void parseTablist(Minecraft mc) {
        GuiPlayerTabOverlay tab = mc.ingameGUI.getTabList();
        List<NetworkPlayerInfo> infos =
                PLAYER_ORDERING.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());

        boolean inAccountSection = false;

        for (NetworkPlayerInfo info : infos) {
            String raw = tab.getPlayerName(info);
            if (raw == null || raw.isEmpty()) continue;

            String clean = net.minecraft.util.StringUtils.stripControlCodes(raw).trim();

            // ── Section detection ─────────────────────────────────────────
            // Fakepixel uses the same \u00A76\u00A7lAccount Info header as Hypixel
            if (raw.contains("\u00A76\u00A7lAccount Info") || clean.equals("Account Info")) {
                inAccountSection = true;
                continue;
            }
            // Any other bold-colored section header ends Account Info
            if (inAccountSection && (raw.contains("\u00A7l") || raw.contains("\u00A7r\u00A7l")) && !clean.isEmpty()) {
                // But only if it looks like a new section (all-caps or known header)
                if (clean.equals("Player Stats") || clean.equals("Server Info")
                        || clean.equals("Quests") || clean.equals("Party")
                        || clean.equals("Dungeon") || clean.isEmpty()) {
                    inAccountSection = false;
                    continue;
                }
            }

            if (!inAccountSection) continue;
            if (clean.isEmpty()) continue;

            // ── Bank ──────────────────────────────────────────────────────
            Matcher bm = BANK_PATTERN.matcher(clean);
            if (bm.find()) {
                String amount   = bm.group("amount").trim();
                String personal = bm.group("personal");
                if (personal != null && !personal.trim().isEmpty()) {
                    // Co-op bank: show "coopAmt \u00A77/ \u00A76personalAmt"
                    cachedBank = amount + " \u00A77/ \u00A76" + personal.trim();
                } else {
                    cachedBank = amount;
                }
                continue;
            }

            // ── Purse ─────────────────────────────────────────────────────
            Matcher pm = PURSE_PATTERN.matcher(clean);
            if (pm.find()) {
                cachedPurse = pm.group("amount").trim();
            }
        }
    }
}