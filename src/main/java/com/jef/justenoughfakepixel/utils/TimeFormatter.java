package com.jef.justenoughfakepixel.utils;

public class TimeFormatter {

    public static String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long ms = millis % 1000;
        
        if (totalSeconds >= 60) {
            long mins = totalSeconds / 60;
            long secs = totalSeconds % 60;
            return secs > 0 
                ? String.format("%dm %d.%ds", mins, secs, ms / 100) 
                : mins + "m";
        }
        
        return String.format("%d.%ds", totalSeconds, ms / 100);
    }

    public static String formatDungeonTime(long millis) {
        if (millis <= 0) return "0:00.000";
        long s = millis / 1000;
        return (s / 60) + ":" + String.format("%02d", s % 60) + "." + String.format("%03d", millis % 1000);
    }

    public static String getColorForRemaining(long remainingMs, long totalMs) {
        if (totalMs <= 0) return "\u00a7c";
        
        double pct = (double) remainingMs / totalMs;
        if (pct > 0.5)  return "§c";
        if (pct > 0.25) return "§6";
        return "§e";
    }
}
