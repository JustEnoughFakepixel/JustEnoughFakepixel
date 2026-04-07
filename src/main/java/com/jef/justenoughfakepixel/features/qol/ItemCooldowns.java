package com.jef.justenoughfakepixel.features.qol;

import com.jef.justenoughfakepixel.events.ActionBarUpdateEvent;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.chat.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.regex.Pattern;

@RegisterEvents
public class ItemCooldowns {


    private static final Map<String, Long> DURATIONS = new LinkedHashMap<>();
    private static final List<ChatTrigger> CHAT_TRIGGERS = new ArrayList<>();
    private static final List<ChatTrigger> ACTION_BAR_TRIGGERS = new ArrayList<>();
    private static final Map<String, Long> endTimes = new HashMap<>();

    static {
        // Wands
        DURATIONS.put("GYROKINETIC_WAND", 30_000L);
        DURATIONS.put("ICE_SPRAY_WAND", 5_000L);
        DURATIONS.put("FIRE_VEIL_WAND", 5_000L);

        // Swords & Weapons
        DURATIONS.put("ATOMSPLIT_KATANA", 5_000L);
        DURATIONS.put("MIDAS_STAFF", 3_000L);
        DURATIONS.put("STARRED_MIDAS_STAFF", 3_000L);
        DURATIONS.put("RAGNAROK_AXE", 120_000L);

        // Armor
        DURATIONS.put("BONZO_MASK", 360_000L);
        DURATIONS.put("STARRED_BONZO_MASK", 360_000L);
        DURATIONS.put("SPIRIT_MASK", 30_000L);
        DURATIONS.put("STARRED_SPIRIT_MASK", 30_000L);
    }

    static {
        CHAT_TRIGGERS.add(new ChatTrigger(Pattern.compile("§r§r§a§aYour §r§9§9Bonzo's Mask §r§a§asaved your life!§r§r"), "BONZO_MASK", "STARRED_BONZO_MASK"));
        CHAT_TRIGGERS.add(new ChatTrigger(Pattern.compile("Spirit Mask.*saved your life"), "SPIRIT_MASK", "STARRED_SPIRIT_MASK"));

        ACTION_BAR_TRIGGERS.add(new ChatTrigger(Pattern.compile("Gravity Storm"), "GYROKINETIC_WAND"));
        ACTION_BAR_TRIGGERS.add(new ChatTrigger(Pattern.compile("BLIZZARD!|Ice Spray"), "ICE_SPRAY_WAND"));
        ACTION_BAR_TRIGGERS.add(new ChatTrigger(Pattern.compile("Fire Veil"), "FIRE_VEIL_WAND"));
        ACTION_BAR_TRIGGERS.add(new ChatTrigger(Pattern.compile("Atomsplit"), "ATOMSPLIT_KATANA"));
        ACTION_BAR_TRIGGERS.add(new ChatTrigger(Pattern.compile("Molten Wave"), "MIDAS_STAFF", "STARRED_MIDAS_STAFF"));
    }

    public static long getRemainingMs(String itemId) {
        Long end = endTimes.get(itemId);
        if (end == null) return 0;
        return Math.max(0, end - System.currentTimeMillis());
    }

    public static boolean isOnCooldown(String itemId) {
        return getRemainingMs(itemId) > 0;
    }

    public static void markUsed(String itemId) {
        Long dur = DURATIONS.get(itemId);
        if (dur == null) return;
        endTimes.put(itemId, System.currentTimeMillis() + dur);
    }

    public static void markUsed(String itemId, long durationMs) {
        endTimes.put(itemId, System.currentTimeMillis() + durationMs);
    }

    public static List<String> getActiveCooldowns() {
        long now = System.currentTimeMillis();
        List<String> active = new ArrayList<>();
        for (Map.Entry<String, Long> e : endTimes.entrySet()) {
            if (e.getValue() > now) active.add(e.getKey());
        }
        active.sort((a, b) -> Long.compare(endTimes.get(b), endTimes.get(a)));
        return active;
    }

    public static ItemStack findItemStack(String itemId) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return null;

        for (ItemStack s : mc.thePlayer.inventory.mainInventory) {
            if (s != null && itemId.equals(com.jef.justenoughfakepixel.utils.ItemUtils.getInternalName(s))) return s;
        }
        for (ItemStack s : mc.thePlayer.inventory.armorInventory) {
            if (s != null && itemId.equals(com.jef.justenoughfakepixel.utils.ItemUtils.getInternalName(s))) return s;
        }
        return null;
    }

    public static String getColorForRemaining(long remainingMs, long totalMs) {
        if (totalMs <= 0) return "§c";
        double pct = (double) remainingMs / totalMs;
        if (pct > 0.5) return "§c";
        if (pct > 0.25) return "§6";
        return "§e";
    }


    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (event.type == 2) return;
        String msg = ChatUtils.clean(event);
        for (ChatTrigger t : CHAT_TRIGGERS) {
            if (t.pattern.matcher(msg).find()) {
                for (String id : t.itemIds) markUsed(id);
            }
        }
    }

    @SubscribeEvent
    public void onActionBar(ActionBarUpdateEvent event) {
        String msg = event.getText();
        for (ChatTrigger t : ACTION_BAR_TRIGGERS) {
            if (t.pattern.matcher(msg).find()) {
                for (String id : t.itemIds) markUsed(id);
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