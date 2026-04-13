package com.jef.justenoughfakepixel.features.qol.timers;

import com.jef.justenoughfakepixel.events.ActionBarUpdateEvent;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.repo.JefRepo;
import com.jef.justenoughfakepixel.repo.RepoHandler;
import com.jef.justenoughfakepixel.repo.TimerRepo;
import com.jef.justenoughfakepixel.repo.data.TimerData;
import com.jef.justenoughfakepixel.utils.item.ItemStackFinder;
import com.jef.justenoughfakepixel.utils.time.TimerManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RegisterEvents
public class ItemCooldowns {

    private static final TimerManager timerManager = new TimerManager(TimerRepo.getCooldownDurations());
    private static List<ChatTrigger> chatTriggers = new ArrayList<>();
    private static List<ChatTrigger> actionBarTriggers = new ArrayList<>();

    static {
        reloadFromRepo();
        RepoHandler.addListener(JefRepo.KEY_TIMERS, ItemCooldowns::reloadFromRepo);
    }

    private static void reloadFromRepo() {
        timerManager.updateDurations(TimerRepo.getCooldownDurations());

        TimerData data = TimerRepo.getTimerData();

        List<ChatTrigger> newChat = new ArrayList<>();
        if (data.chatTriggers != null) {
            for (TimerData.TriggerEntry entry : data.chatTriggers) {
                if (entry.pattern != null && entry.itemIds != null && !entry.itemIds.isEmpty()) {
                    newChat.add(new ChatTrigger(Pattern.compile(entry.pattern), entry.itemIds.toArray(new String[0])));
                }
            }
        }
        chatTriggers = newChat;

        List<ChatTrigger> newActionBar = new ArrayList<>();
        if (data.actionBarTriggers != null) {
            for (TimerData.TriggerEntry entry : data.actionBarTriggers) {
                if (entry.pattern != null && entry.itemIds != null && !entry.itemIds.isEmpty()) {
                    newActionBar.add(new ChatTrigger(Pattern.compile(entry.pattern), entry.itemIds.toArray(new String[0])));
                }
            }
        }
        actionBarTriggers = newActionBar;
    }

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
        for (ChatTrigger t : chatTriggers) {
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
        for (ChatTrigger t : actionBarTriggers) {
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