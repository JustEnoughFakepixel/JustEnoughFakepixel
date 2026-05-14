package com.jef.justenoughfakepixel.features.mining.pristine;

import com.jef.justenoughfakepixel.core.JefGsonBuilder;
import com.jef.justenoughfakepixel.core.JefStorageManager;
import com.jef.justenoughfakepixel.features.mining.powder.PowderStats;
import lombok.Getter;

import java.io.File;

public class PristineStats implements JefStorageManager.Managed, JefStorageManager.AutoSaveable {

    private static final long INACTIVITY_TIMEOUT_MS = 120_000L;
    private static PristineStats INSTANCE;
    public final PowderStats.RateInfo rateInfo = new PowderStats.RateInfo();
    public final PowderStats.RateInfo procRateInfo = new PowderStats.RateInfo();
    private File file = null;
    @Getter
    private PristineData data = new PristineData();
    @Getter
    private volatile boolean trackingEnabled = true;

    private PristineStats() {
    }

    public static PristineStats getInstance() {
        if (INSTANCE == null) INSTANCE = new PristineStats();
        return INSTANCE;
    }

    public static long[] getGemBreakdown(PristineData data, String gem) {
        long flawed = data.gemstones.getOrDefault("Flawed_" + gem, 0L);
        long fine = data.gemstones.getOrDefault("Fine_" + gem, 0L);
        long flawless = data.gemstones.getOrDefault("Flawless_" + gem, 0L);
        long totalFlawed = flawed + fine * 80L + flawless * 6400L;
        long fl = totalFlawed / 6400L;
        long rem = totalFlawed % 6400L;
        long fi = rem / 80L;
        long fw = rem % 80L;
        return new long[]{fl, fi, fw};
    }

    public boolean toggleTracking() {
        trackingEnabled = !trackingEnabled;
        if (trackingEnabled) data.lastPristineMs = System.currentTimeMillis();
        return trackingEnabled;
    }

    public boolean shouldAutoStop() {
        return data.lastPristineMs > 0L && (System.currentTimeMillis() - data.lastPristineMs) > INACTIVITY_TIMEOUT_MS;
    }

    @Override
    public void initFile(File configDir) {
        this.file = new File(configDir, "pristine_stats.json");
    }

    @Override
    public void load() {
        PristineData loaded = JefStorageManager.loadSafe(file, PristineData.class, JefGsonBuilder.GSON);
        if (loaded != null) data = loaded;
    }

    public void save() {
        JefStorageManager.saveAtomic(file, data, JefGsonBuilder.GSON);
    }

    @Override
    public void autoSave() {
        save();
    }

    public void reset() {
        data.reset();
        rateInfo.clear();
        procRateInfo.clear();
        save();
    }

    public void tickRates() {
        PowderStats.tick(rateInfo, data.gemstones.values().stream().mapToLong(Long::longValue).sum());
        PowderStats.tick(procRateInfo, data.totalProcs);
    }

    public void onWorldChange() {
        rateInfo.clear();
        procRateInfo.clear();
    }
}
