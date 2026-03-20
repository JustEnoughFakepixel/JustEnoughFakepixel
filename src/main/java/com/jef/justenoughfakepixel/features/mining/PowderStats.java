package com.jef.justenoughfakepixel.features.mining;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PowderStats {

    private static PowderStats INSTANCE;

    public static PowderStats getInstance() {
        if (INSTANCE == null) INSTANCE = new PowderStats();
        return INSTANCE;
    }

    private PowderStats() {}

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private File       file     = null;
    private PowderData data     = new PowderData();
    private boolean    tracking = true;

    public boolean isTrackingEnabled() { return tracking; }

    public boolean toggleTracking() {
        tracking = !tracking;
        return tracking;
    }

    public void initFile(File configDir) {
        this.file = new File(configDir, "powder_stats.json");
    }

    public void load() {
        if (file == null || !file.exists()) return;
        try (Reader r = new FileReader(file)) {
            PowderData loaded = GSON.fromJson(r, PowderData.class);
            if (loaded != null) data = loaded;
        } catch (Exception e) {
            System.err.println("[JEF/Powder] Failed to load powder_stats.json: " + e.getMessage());
        }
    }

    public void save() {
        if (file == null) return;
        try (Writer w = new FileWriter(file)) {
            GSON.toJson(data, w);
        } catch (Exception e) {
            System.err.println("[JEF/Powder] Failed to save powder_stats.json: " + e.getMessage());
        }
    }

    public PowderData getData() { return data; }

    public void reset() {
        data.reset();
        gemstoneInfo.clear();
        chestInfo.clear();
        hardStoneInfo.clear();
        save();
    }

    public static class RateInfo {
        public long   estimated     = 0L;
        public long   lastEstimated = 0L;
        public int    stoppedChecks = 0;
        public double perHour       = 0.0;
        public final List<Long> perMin = new ArrayList<>();

        public void clear() {
            estimated = 0L; lastEstimated = 0L;
            stoppedChecks = 0; perHour = 0.0;
            perMin.clear();
        }
    }

    public final RateInfo gemstoneInfo  = new RateInfo();
    public final RateInfo chestInfo     = new RateInfo();
    public final RateInfo hardStoneInfo = new RateInfo();

    public void tickRates() {
        tick(gemstoneInfo,  data.gemstonePowder);
        tick(chestInfo,     data.totalChestsPicked);
        tick(hardStoneInfo, data.hardStone + data.hardStoneCompacted * 9L);
    }

    private static void tick(RateInfo info, long current) {
        info.estimated = current;
        long difference = info.estimated - info.lastEstimated;
        info.lastEstimated = info.estimated;

        if (difference == info.estimated) return;

        if (!info.perMin.isEmpty())
            info.perHour = info.perMin.stream().mapToLong(Long::longValue).average().orElse(0) * 3600;
        info.perMin.add(difference);

        if (difference == 0L) {
            info.stoppedChecks++;
            if (info.stoppedChecks >= 60) {
                info.stoppedChecks = 0;
                info.perMin.clear();
                info.perHour = 0.0;
            }
        } else {
            info.stoppedChecks = 0;
        }
    }

    public void onWorldChange() {
        gemstoneInfo.clear();
        chestInfo.clear();
        hardStoneInfo.clear();
    }

    public static String gemKey(String quality, String gem) {
        return quality + "_" + gem;
    }

    public static long[] getGemBreakdown(PowderData data, String gem) {
        long rough    = data.gemstones.getOrDefault(gemKey("Rough",    gem), 0L);
        long flawed   = data.gemstones.getOrDefault(gemKey("Flawed",   gem), 0L);
        long fine     = data.gemstones.getOrDefault(gemKey("Fine",     gem), 0L);
        long flawless = data.gemstones.getOrDefault(gemKey("Flawless", gem), 0L);

        long totalRough = rough + flawed * 80L + fine * 6400L + flawless * 512000L;

        long fl  = totalRough / 512000L;
        long rem = totalRough % 512000L;
        long fi  = rem / 6400L;
        rem      = rem % 6400L;
        long fw  = rem / 80L;
        long rgh = rem % 80L;
        return new long[]{fl, fi, fw, rgh};
    }

    public static String fmtRate(double perHour) {
        if (perHour <= 0) return "0";
        long v = (long) perHour;
        if (v >= 1_000_000) return String.format("%.1fM", v / 1_000_000.0);
        if (v >= 1_000)     return String.format("%.1fK", v / 1_000.0);
        return String.valueOf(v);
    }

    public static String fmtNum(long n) {
        if (n >= 1_000_000) return String.format("%.2fM", n / 1_000_000.0);
        if (n >= 1_000)     return String.format("%,d", n);
        return String.valueOf(n);
    }
}