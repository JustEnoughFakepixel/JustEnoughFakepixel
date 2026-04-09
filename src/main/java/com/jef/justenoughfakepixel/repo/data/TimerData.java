package com.jef.justenoughfakepixel.repo.data;

import java.util.List;

public class TimerData {
    public List<TimerEntry> cooldowns;
    public List<TimerEntry> abilities;
    public List<TimerEntry> invincibility;
    public List<TriggerEntry> chatTriggers;
    public List<TriggerEntry> actionBarTriggers;

    public static class TimerEntry {
        public String itemId;
        public long durationMs;
    }

    public static class TriggerEntry {
        public String pattern;
        public List<String> itemIds;
    }
}
