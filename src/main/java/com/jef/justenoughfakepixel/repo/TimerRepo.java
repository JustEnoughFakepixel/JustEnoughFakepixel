package com.jef.justenoughfakepixel.repo;

import com.jef.justenoughfakepixel.repo.data.TimerData;

import java.util.LinkedHashMap;
import java.util.Map;

public class TimerRepo {

    private static final TimerData FALLBACK = new TimerData();

    private TimerRepo() {
    }

    public static TimerData getTimerData() {
        TimerData data = RepoHandler.get(JefRepo.KEY_TIMERS, TimerData.class, null);
        return data != null ? data : getFallbackData();
    }

    public static Map<String, Long> getCooldownDurations() {
        Map<String, Long> map = new LinkedHashMap<>();
        TimerData data = getTimerData();
        if (data.cooldowns != null) {
            for (TimerData.TimerEntry entry : data.cooldowns) {
                if (entry.itemId != null) {
                    map.put(entry.itemId, entry.durationMs);
                }
            }
        }
        return map;
    }

    public static Map<String, Long> getAbilityDurations() {
        Map<String, Long> map = new LinkedHashMap<>();
        TimerData data = getTimerData();
        if (data.abilities != null) {
            for (TimerData.TimerEntry entry : data.abilities) {
                if (entry.itemId != null) {
                    map.put(entry.itemId, entry.durationMs);
                }
            }
        }
        return map;
    }

    public static Map<String, Long> getInvincibilityDurations() {
        Map<String, Long> map = new LinkedHashMap<>();
        TimerData data = getTimerData();
        if (data.invincibility != null) {
            for (TimerData.TimerEntry entry : data.invincibility) {
                if (entry.itemId != null) {
                    map.put(entry.itemId, entry.durationMs);
                }
            }
        }
        return map;
    }

    private static TimerData getFallbackData() {
        TimerData data = new TimerData();
        data.cooldowns = new java.util.ArrayList<>();
        data.abilities = new java.util.ArrayList<>();
        data.invincibility = new java.util.ArrayList<>();
        data.chatTriggers = new java.util.ArrayList<>();
        data.actionBarTriggers = new java.util.ArrayList<>();

        // Cooldowns
        addTimer(data.cooldowns, "GYROKINETIC_WAND", 30_000L);
        addTimer(data.cooldowns, "ICE_SPRAY_WAND", 5_000L);
        addTimer(data.cooldowns, "FIRE_VEIL_WAND", 1_000L);
        addTimer(data.cooldowns, "ATOMSPLIT_KATANA", 5_000L);
        addTimer(data.cooldowns, "MIDAS_STAFF", 3_000L);
        addTimer(data.cooldowns, "STARRED_MIDAS_STAFF", 3_000L);
        addTimer(data.cooldowns, "RAGNAROK_AXE", 120_000L);
        addTimer(data.cooldowns, "BONZO_MASK", 360_000L);
        addTimer(data.cooldowns, "STARRED_BONZO_MASK", 360_000L);
        addTimer(data.cooldowns, "SPIRIT_MASK", 30_000L);
        addTimer(data.cooldowns, "STARRED_SPIRIT_MASK", 30_000L);

        // Abilities
        addTimer(data.abilities, "FIRE_VEIL_WAND", 5_000L);

        // Invincibility
        addTimer(data.invincibility, "BONZO_MASK", 5_000L);
        addTimer(data.invincibility, "STARRED_BONZO_MASK", 5_000L);
        addTimer(data.invincibility, "SPIRIT_MASK", 5_000L);
        addTimer(data.invincibility, "STARRED_SPIRIT_MASK", 5_000L);

        // Chat triggers
        addTrigger(data.chatTriggers, "§r§r§a§aYour §r§9§9Bonzo's Mask §r§a§asaved your life!§r§r §r", "BONZO_MASK", "STARRED_BONZO_MASK");
        addTrigger(data.chatTriggers, "§r§aYour §r§5Spirit Mask §r§asaved you from death!§r§r §r", "SPIRIT_MASK", "STARRED_SPIRIT_MASK");

        // Action bar triggers
        addTrigger(data.actionBarTriggers, "Gravity Storm", "GYROKINETIC_WAND");
        addTrigger(data.actionBarTriggers, "BLIZZARD!|Ice Spray", "ICE_SPRAY_WAND");
        addTrigger(data.actionBarTriggers, "Fire Veil", "FIRE_VEIL_WAND");
        addTrigger(data.actionBarTriggers, "Atomsplit", "ATOMSPLIT_KATANA");
        addTrigger(data.actionBarTriggers, "Molten Wave", "MIDAS_STAFF", "STARRED_MIDAS_STAFF");

        return data;
    }

    private static void addTimer(java.util.List<TimerData.TimerEntry> list, String itemId, long durationMs) {
        TimerData.TimerEntry entry = new TimerData.TimerEntry();
        entry.itemId = itemId;
        entry.durationMs = durationMs;
        list.add(entry);
    }

    private static void addTrigger(java.util.List<TimerData.TriggerEntry> list, String pattern, String... itemIds) {
        TimerData.TriggerEntry entry = new TimerData.TriggerEntry();
        entry.pattern = pattern;
        entry.itemIds = java.util.Arrays.asList(itemIds);
        list.add(entry);
    }
}
