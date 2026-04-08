package com.jef.justenoughfakepixel.features.qol;

import com.jef.justenoughfakepixel.events.ActionBarUpdateEvent;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.ItemStackFinder;
import com.jef.justenoughfakepixel.utils.TimerManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Tracks item ability cooldowns triggered by chat messages and action bar updates.
 */
@RegisterEvents
public class ItemCooldowns {

    private static final Map<String, Long> DURATIONS = new LinkedHashMap<>();
    private static final TimerManager timerManager;
    private static final List<ChatTrigger> CHAT_TRIGGERS = new ArrayList<>();
    private static final List<ChatTrigger> ACTION_BAR_TRIGGERS = new ArrayList<>();

    static {
        DURATIONS.put("GYROKINETIC_WAND", 30_000L);
        DURATIONS.put("ICE_SPRAY_WAND", 5_000L);
        DURATIONS.put("FIRE_VEIL_WAND", 1_000L);
        DURATIONS.put("ATOMSPLIT_KATANA", 5_000L);
        DURATIONS.put("MIDAS_STAFF", 3_000L);
        DURATIONS.put("STARRED_MIDAS_STAFF", 3_000L);
        DURATIONS.put("RAGNAROK_AXE", 120_000L);
        DURATIONS.put("BONZO_MASK", 360_000L);
        DURATIONS.put("STARRED_BONZO_MASK", 360_000L);
        DURATIONS.put("SPIRIT_MASK", 30_000L);
        DURATIONS.put("STARRED_SPIRIT_MASK", 30_000L);

        timerManager = new TimerManager(DURATIONS);
    }

    static {
        CHAT_TRIGGERS.add(new ChatTrigger(Pattern.compile("§r§r§a§aYour §r§9§9Bonzo's Mask §r§a§asaved your life!§r§r §r"), "BONZO_MASK", "STARRED_BONZO_MASK"));
        CHAT_TRIGGERS.add(new ChatTrigger( Pattern.compile("§r§aYour §r§5Spirit Mask §r§asaved you from death!§r§r §r"), "SPIRIT_MASK", "STARRED_SPIRIT_MASK"));

        ACTION_BAR_TRIGGERS.add(new ChatTrigger(Pattern.compile("Gravity Storm"), "GYROKINETIC_WAND"));
        ACTION_BAR_TRIGGERS.add(new ChatTrigger(Pattern.compile("BLIZZARD!|Ice Spray"), "ICE_SPRAY_WAND"));
        ACTION_BAR_TRIGGERS.add(new ChatTrigger(Pattern.compile("Fire Veil"), "FIRE_VEIL_WAND"));
        ACTION_BAR_TRIGGERS.add(new ChatTrigger(Pattern.compile("Atomsplit"), "ATOMSPLIT_KATANA"));
        ACTION_BAR_TRIGGERS.add(new ChatTrigger(Pattern.compile("Molten Wave"), "MIDAS_STAFF", "STARRED_MIDAS_STAFF"));
    }

    // Public API delegating to TimerManager
    public static long getRemainingMs(String itemId) {
        return timerManager.getRemainingMs(itemId);
    }

    public static boolean isOnCooldown(String itemId) {
        return timerManager.isActive(itemId);
    }

    public static void markUsed(String itemId) {
        timerManager.markActive(itemId);
    }

    public static void markUsed(String itemId, long durationMs) {
        timerManager.markActive(itemId, durationMs);
    }

    public static List<String> getActiveCooldowns() {
        return timerManager.getActiveTimers();
    }

    public static ItemStack findItemStack(String itemId) {
        return ItemStackFinder.findItemStack(itemId);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (event.type == 2) return;
        String msg = event.message.getFormattedText();
        for (ChatTrigger t : CHAT_TRIGGERS) {
            if (t.pattern.matcher(msg).find()) {
                for (String id : t.itemIds) {
                    markUsed(id);
                    ItemInvincibilityTimers.markActive(id);
                }
            }
        }
    }

    @SubscribeEvent
    public void onActionBar(ActionBarUpdateEvent event) {
        String msg = event.getText();
        for (ChatTrigger t : ACTION_BAR_TRIGGERS) {
            if (t.pattern.matcher(msg).find()) {
                for (String id : t.itemIds) {
                    markUsed(id);
                    ItemAbilityTimers.markActive(id);
                }
            }
        }
    }

    private static class ChatTrigger {
        final Pattern pattern;
        final String[] itemIds;

        ChatTrigger(Pattern pattern, String... itemIds) {
            this.pattern = pattern;
            this.itemIds = itemIds;
        }
    }
}