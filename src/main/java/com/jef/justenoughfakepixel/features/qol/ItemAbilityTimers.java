package com.jef.justenoughfakepixel.features.qol;

import com.jef.justenoughfakepixel.utils.ItemStackFinder;
import com.jef.justenoughfakepixel.utils.TimerManager;
import net.minecraft.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks active durations for item abilities (e.g., Fire Veil Wand's 5s wall).
 */
public class ItemAbilityTimers {

    private static final Map<String, Long> DURATIONS = new LinkedHashMap<>();
    private static final TimerManager timerManager;

    static {
        DURATIONS.put("FIRE_VEIL_WAND", 5_000L);
        timerManager = new TimerManager(DURATIONS);
    }

    // Public API delegating to TimerManager
    public static void markActive(String itemId) {
        timerManager.markActive(itemId);
    }

    public static void markActive(String itemId, long durationMs) {
        timerManager.markActive(itemId, durationMs);
    }

    public static long getRemainingMs(String itemId) {
        return timerManager.getRemainingMs(itemId);
    }

    public static boolean isActive(String itemId) {
        return timerManager.isActive(itemId);
    }

    public static List<String> getActiveTimers() {
        return timerManager.getActiveTimers();
    }

    public static ItemStack findItemStack(String itemId) {
        return ItemStackFinder.findItemStack(itemId);
    }
}