package com.jef.justenoughfakepixel.features.diana;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;

import java.io.*;

public class DianaStats {

    private static DianaStats INSTANCE;

    public static DianaStats getInstance() {
        if (INSTANCE == null) INSTANCE = new DianaStats();
        return INSTANCE;
    }

    private DianaStats() {}

    private static final long   INACTIVITY_LIMIT_MS = 90_000L;
    private static final Gson   GSON                = new GsonBuilder().setPrettyPrinting().create();
    private static final Minecraft mc               = Minecraft.getMinecraft();

    private File      file = null;
    private DianaData data = new DianaData();

    public volatile String lastDropType   = null;
    public volatile long   lastDropAmount = 0L;

    // Lootshare
    private volatile long    lastLootShareMs  = 0L;
    private volatile boolean hasTrackedInqLs  = false;

    // Timer state
    private boolean timerRunning      = false;
    private boolean timerStartedOnce  = false;
    private boolean inactivityFlagged = false;
    private long    timerStartTime    = 0L;
    private long    lastActivityTime  = 0L;

    // File I/O

    public void initFile(File configDir) {
        this.file = new File(configDir, "diana_stats.json");
    }

    public void load() {
        if (file == null || !file.exists()) return;
        try (Reader r = new FileReader(file)) {
            DianaData loaded = GSON.fromJson(r, DianaData.class);
            if (loaded != null) data = loaded;
        } catch (Exception e) {
            System.err.println("[JEF/Diana] Failed to load diana_stats.json: " + e.getMessage());
        }
    }

    public void save() {
        if (file == null) return;
        try (Writer w = new FileWriter(file)) {
            GSON.toJson(data, w);
        } catch (Exception e) {
            System.err.println("[JEF/Diana] Failed to save diana_stats.json: " + e.getMessage());
        }
    }

    // Reset

    public void reset() {
        data              = new DianaData();
        lastDropType      = null;
        lastDropAmount    = 0L;
        lastLootShareMs   = 0L;
        hasTrackedInqLs   = false;
        timerRunning      = false;
        timerStartedOnce  = false;
        inactivityFlagged = false;
        timerStartTime    = 0L;
        lastActivityTime  = 0L;
    }

    // Accessors

    public DianaData getData() { return data; }

    public boolean isTracking() {
        return hasSpadeInHotbar();
    }

    public static boolean hasSpadeInHotbar() {
        if (mc.thePlayer == null) return false;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.hasDisplayName()
                    && StringUtils.stripControlCodes(stack.getDisplayName()).contains("Ancestral Spade")) {
                return true;
            }
        }
        return false;
    }

    // Lootshare

    public void onLootshare() {
        lastLootShareMs = System.currentTimeMillis();
    }

    /** Returns true if a lootshare message arrived within the last {@code seconds} seconds. */
    public boolean gotLootShareRecently(long seconds) {
        return (System.currentTimeMillis() - lastLootShareMs) / 1000L <= seconds;
    }

    /**
     * Called when a Minos Inquisitor armor stand disappears from the world nearby.
     * If a lootshare arrived within the last 3 seconds, counts it as a lootshared inq.
     * Per-call cooldown prevents double-counting if the event fires more than once.
     */
    public void onInqDeath() {
        if (hasTrackedInqLs) return;
        if (!gotLootShareRecently(3)) return;

        hasTrackedInqLs = true;
        data.totalInqsLootshared++;
        save();

        // Reset cooldown after 2 seconds
        new Thread(() -> {
            try { Thread.sleep(2_000L); } catch (InterruptedException ignored) {}
            hasTrackedInqLs = false;
        }).start();
    }

    // Timer

    public void updateActivity() {
        if (!timerStartedOnce) {
            timerStartTime   = System.currentTimeMillis();
            timerRunning     = true;
            timerStartedOnce = true;
        } else if (!timerRunning) {
            if (inactivityFlagged) {
                data.activeTimeMs -= INACTIVITY_LIMIT_MS;
                inactivityFlagged  = false;
            }
            timerStartTime = System.currentTimeMillis();
            timerRunning   = true;
        }
        lastActivityTime = System.currentTimeMillis();
    }

    public void timerTick() {
        if (!timerRunning) return;
        long now = System.currentTimeMillis();
        data.activeTimeMs += now - timerStartTime;
        timerStartTime     = now;
        if (now - lastActivityTime > INACTIVITY_LIMIT_MS) {
            timerRunning      = false;
            inactivityFlagged = true;
        }
    }

    public void pauseTimer() {
        if (!timerRunning) return;
        long now = System.currentTimeMillis();
        data.activeTimeMs += now - timerStartTime;
        timerRunning       = false;
        save();
    }

    // Computed stats

    public double getBph() {
        if (data.activeTimeMs < 1_000L || data.totalBurrows == 0) return 0.0;
        return data.totalBurrows / (data.activeTimeMs / 3_600_000.0);
    }

    public double getInqChance() {
        if (data.totalInqs == 0 || data.totalMobs == 0) return -1.0;
        return (double) data.totalInqs / data.totalMobs * 100.0;
    }
}