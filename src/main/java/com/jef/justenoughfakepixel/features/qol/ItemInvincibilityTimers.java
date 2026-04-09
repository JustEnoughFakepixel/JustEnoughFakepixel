package com.jef.justenoughfakepixel.features.qol;

import com.jef.justenoughfakepixel.repo.JefRepo;
import com.jef.justenoughfakepixel.repo.RepoHandler;
import com.jef.justenoughfakepixel.repo.TimerRepo;
import com.jef.justenoughfakepixel.utils.ItemStackFinder;
import com.jef.justenoughfakepixel.utils.TimerManager;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemInvincibilityTimers {

    private static final TimerManager timerManager = new TimerManager(TimerRepo.getInvincibilityDurations());

    static {
        RepoHandler.addListener(JefRepo.KEY_TIMERS, () ->
                timerManager.updateDurations(TimerRepo.getInvincibilityDurations())
        );
    }

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