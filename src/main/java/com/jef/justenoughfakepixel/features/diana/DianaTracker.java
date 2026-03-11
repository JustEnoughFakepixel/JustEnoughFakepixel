package com.jef.justenoughfakepixel.features.diana;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DianaTracker {

    private static final Pattern BURROW_DIG =
            Pattern.compile("You dug out a Griffin Borrow! \\(([1-4])/4\\)");

    private static final Pattern MOB_SPAWN =
            Pattern.compile("Uh oh! You dug out (.+)");

    // Counter resets
    private static final Pattern RARE_STICK    = Pattern.compile("RARE DROP! Daedalus Stick");
    private static final Pattern RARE_RELIC    = Pattern.compile("RARE DROP! Minos Relic");
    private static final Pattern RARE_CHIMERA  = Pattern.compile("RARE DROP! Chimera [IVX]+");

    // Tracked mob drops
    private static final Pattern RARE_SHELMET  = Pattern.compile("RARE DROP! Dwarf Turtle Shelmet");
    private static final Pattern RARE_REMEDIES = Pattern.compile("RARE DROP! Antique Remedies");
    private static final Pattern RARE_PLUSHIE  = Pattern.compile("RARE DROP! Crochet Tiger Plushie");

    private static final Pattern DROP_FEATHER  = Pattern.compile("RARE DROP! You dug out a Griffin Feather");
    private static final Pattern DROP_SOUVENIR = Pattern.compile("RARE DROP! You dug out a Washed-up Souvenir");
    private static final Pattern DROP_CROWN    = Pattern.compile("RARE DROP! You dug out a Crown of Greed");
    private static final Pattern DROP_COINS    = Pattern.compile("RARE DROP! You dug out ([\\d,]+) Coins");
    private static final Pattern GRIFFIN_DOUBLED = Pattern.compile("Your Griffin doubled your rewards\\?!");

    // Lootshare
    private static final Pattern LOOT_SHARE =
            Pattern.compile("^LOOT SHARE You received loot for assisting");

    // Inq entity detection
    private static final double INQ_TRACK_RANGE_SQ = 30.0 * 30.0;  // 30 block radius
    private static final int    TICK_SCAN_INTERVAL  = 5;            // scan every 5 ticks

    private final Map<Integer, EntityArmorStand> trackedInqs = new HashMap<>();
    private final Set<Integer>                   defeatedInqs = new HashSet<>();
    private int tickCount = 0;

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        DianaStats stats = DianaStats.getInstance();
        stats.timerTick();

        if (++tickCount % TICK_SCAN_INTERVAL != 0) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // Add newly-visible inq armor stands to tracking map
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityArmorStand)) continue;
            int id = entity.getEntityId();
            if (trackedInqs.containsKey(id) || defeatedInqs.contains(id)) continue;
            if (mc.thePlayer.getDistanceSqToEntity(entity) > INQ_TRACK_RANGE_SQ) continue;

            String name = StringUtils.stripControlCodes(entity.getDisplayName().getUnformattedText());
            if (name.contains("Minos Inquisitor")) {
                trackedInqs.put(id, (EntityArmorStand) entity);
            }
        }

        // Check tracked inqs for death (entity dead or gone from world)
        trackedInqs.entrySet().removeIf(entry -> {
            EntityArmorStand stand = entry.getValue();
            if (stand.isDead || !mc.theWorld.loadedEntityList.contains(stand)) {
                int id = entry.getKey();
                defeatedInqs.add(id);
                stats.onInqDeath();
                return true;
            }
            return false;
        });

        // Trim defeatedInqs to avoid unbounded growth
        if (defeatedInqs.size() > 200) defeatedInqs.clear();
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        DianaStats.getInstance().pauseTimer();
        trackedInqs.clear();
        defeatedInqs.clear();
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (mc.thePlayer == null) return;
        String raw = StringUtils.stripControlCodes(event.message.getUnformattedText());
        DianaStats stats = DianaStats.getInstance();

        // Lootshare detection — must run before drop handlers
        if (LOOT_SHARE.matcher(raw).find()) {
            stats.onLootshare();
        }

        handleBurrowDrops(raw, stats);
        handleRareMobDrops(raw, stats);

        if (!stats.isTracking()) return;

        handleBurrowDig(raw, stats);
        handleMobSpawn(raw, stats);
    }

    private void handleBurrowDig(String msg, DianaStats stats) {
        if (!BURROW_DIG.matcher(msg).find()) return;
        stats.updateActivity();
        stats.getData().totalBurrows++;
        stats.save();
    }

    private void handleMobSpawn(String msg, DianaStats stats) {
        Matcher m = MOB_SPAWN.matcher(msg);
        if (!m.find()) return;

        stats.updateActivity();
        String mobName = m.group(1).trim();
        DianaData d = stats.getData();
        d.totalMobs++;

        switch (mobName) {
            case "Minos Inquisitor":
                d.mobsSinceInq = 0;
                d.inqsSinceChimera++;
                d.totalInqs++;
                break;
            case "Minotaur":
                d.mobsSinceInq++;
                d.minotaursSinceStick++;
                d.totalMinotaurs++;
                break;
            case "Minos Champion":
                d.mobsSinceInq++;
                d.champsSinceRelic++;
                d.totalChamps++;
                break;
            default:
                d.mobsSinceInq++;
                break;
        }
        stats.save();
    }

    private void handleRareMobDrops(String msg, DianaStats stats) {
        DianaData d = stats.getData();
        boolean changed = false;

        if (RARE_STICK.matcher(msg).find())    { d.minotaursSinceStick = 0;  changed = true; }
        if (RARE_RELIC.matcher(msg).find())    { d.champsSinceRelic = 0;     changed = true; }
        if (RARE_CHIMERA.matcher(msg).find())  { d.inqsSinceChimera = 0;     changed = true; }
        if (RARE_SHELMET.matcher(msg).find())  { d.dwarfTurtleShelmets++;    changed = true; }
        if (RARE_REMEDIES.matcher(msg).find()) { d.antiqueRemedies++;        changed = true; }
        if (RARE_PLUSHIE.matcher(msg).find())  { d.crochetTigerPlushies++;  changed = true; }

        if (changed) stats.save();
    }

    private void handleBurrowDrops(String msg, DianaStats stats) {
        DianaData d = stats.getData();

        if (GRIFFIN_DOUBLED.matcher(msg).find()) {
            applyDoubledReward(stats);
            return;
        }

        if (DROP_FEATHER.matcher(msg).find()) {
            d.griffinFeathers++;
            stats.lastDropType   = "feather";
            stats.lastDropAmount = 1L;
            stats.save();

        } else if (DROP_SOUVENIR.matcher(msg).find()) {
            d.souvenirs++;
            stats.lastDropType   = "souvenir";
            stats.lastDropAmount = 1L;
            stats.save();

        } else if (DROP_CROWN.matcher(msg).find()) {
            d.crownsOfGreed++;
            stats.lastDropType   = "crown";
            stats.lastDropAmount = 1L;
            stats.save();

        } else {
            Matcher coins = DROP_COINS.matcher(msg);
            if (coins.find()) {
                long amount = parseLong(coins.group(1));
                d.totalCoins += amount;
                stats.lastDropType   = "coins";
                stats.lastDropAmount = amount;
                stats.save();
            }
        }
    }

    private void applyDoubledReward(DianaStats stats) {
        if (stats.lastDropType == null) return;
        DianaData d = stats.getData();
        switch (stats.lastDropType) {
            case "feather":  d.griffinFeathers++;                  break;
            case "souvenir": d.souvenirs++;                        break;
            case "crown":    d.crownsOfGreed++;                    break;
            case "coins":    d.totalCoins += stats.lastDropAmount; break;
        }
        stats.lastDropType   = null;
        stats.lastDropAmount = 0L;
        stats.save();
    }

    // Party command reply helpers

    private static String fmtCoins(long coins) {
        if (coins >= 1_000_000) return String.format("%.1fM", coins / 1_000_000.0);
        if (coins >= 1_000)     return String.format("%.1fK", coins / 1_000.0);
        return String.valueOf(coins);
    }

    public static String getBphMessage() {
        DianaStats stats = DianaStats.getInstance();
        DianaData  d     = stats.getData();
        return String.format("BPH: %.1f  Burrows: %d", stats.getBph(), d.totalBurrows);
    }

    public static String getInqMessage() {
        DianaStats stats     = DianaStats.getInstance();
        DianaData  d         = stats.getData();
        double     inqChance = stats.getInqChance();
        if (inqChance < 0) {
            return String.format("Total inqs: %d  Mobs since last: %d  Rate: unknown",
                    d.totalInqs, d.mobsSinceInq);
        }
        return String.format("Total inqs: %d  Mobs since last: %d  Rate: %.1f%%",
                d.totalInqs, d.mobsSinceInq, inqChance);
    }

    public static String getChimMessage() {
        DianaData d = DianaStats.getInstance().getData();
        return String.format("Inqs since chimera: %d", d.inqsSinceChimera);
    }

    public static String getStickMessage() {
        DianaData d = DianaStats.getInstance().getData();
        return String.format("Minotaurs since stick: %d", d.minotaursSinceStick);
    }

    public static String getRelicMessage() {
        DianaData d = DianaStats.getInstance().getData();
        return String.format("Champs since relic: %d", d.champsSinceRelic);
    }

    public static String getBurrowStatsMessage() {
        DianaStats stats = DianaStats.getInstance();
        DianaData  d     = stats.getData();
        return String.format("Burrows: %d  BPH: %.1f  Feathers: %d  Souvenirs: %d  Crowns: %d  Coins: %s",
                d.totalBurrows, stats.getBph(),
                d.griffinFeathers, d.souvenirs, d.crownsOfGreed, fmtCoins(d.totalCoins));
    }

    public static String getMobStatsMessage() {
        DianaData d = DianaStats.getInstance().getData();
        return String.format("Total mobs: %d  Inqs: %d  Minotaurs: %d  Champs: %d",
                d.totalMobs, d.totalInqs, d.totalMinotaurs, d.totalChamps);
    }

    public static String getMobDropStatsMessage() {
        DianaData d = DianaStats.getInstance().getData();
        return String.format("Shelmets: %d  Remedies: %d  Plushies: %d",
                d.dwarfTurtleShelmets, d.antiqueRemedies, d.crochetTigerPlushies);
    }

    public static String getHelpMessage() {
        return "§6[Diana Commands]\n"
                + "§e!bph §7- burrows and BPH\n"
                + "§e!inq §7- inq rate and totals\n"
                + "§e!chim §7- inqs since chimera\n"
                + "§e!stick §7- minotaurs since stick\n"
                + "§e!relic §7- champs since relic\n"
                + "§e!stats burrow §7- burrow loot totals\n"
                + "§e!stats mob §7- mob kill totals\n"
                + "§e!stats mobdrop §7- rare mob drop totals";
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s.replace(",", "")); }
        catch (NumberFormatException e) { return 0L; }
    }
}