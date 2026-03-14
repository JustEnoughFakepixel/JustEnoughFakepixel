package com.jef.justenoughfakepixel.utils;

public class ColorUtils {

    private ColorUtils() {}

    /** Strips all Minecraft color/formatting codes (§x) from a string. */
    public static String stripColor(String s) {
        return s == null ? "" : s.replaceAll("\u00A7[0-9a-fklmnorA-FKLMNOR]", "");
    }
}