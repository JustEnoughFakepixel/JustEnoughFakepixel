package com.jef.justenoughfakepixel.repo.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlayerTagData {

    @SerializedName("tags")
    public List<Entry> tags;

    public static class Entry {

        /** The player's IGN (case-insensitive match) */
        @SerializedName("name")
        public String name;

        /**
         * The colored text shown before the unicode, e.g. "§b[DEV]"
         * Supports § or \u00A7 for colors.
         */
        @SerializedName("text")
        public String text;

        /**
         * Unicode codepoint for the symbol shown AFTER the text, e.g. "U+2726"
         * Resolves to the actual character at runtime.
         */
        @SerializedName("unicodeSymbol")
        public String unicodeSymbol;

        /**
         * Hex color for the unicode symbol, e.g. "#FF5555"
         * Converted to nearest Minecraft §-color at runtime.
         */
        @SerializedName("unicodeColor")
        public String unicodeColor;

        /**
         * Hover tooltip text. Supports § or \u00A7 for colors.
         */
        @SerializedName("hover")
        public String hover;

        // ----------------------------------------------------------------
        // Helpers
        // ----------------------------------------------------------------

        /** Converts "U+2726" → '✦', returns 0 on failure. */
        public char resolveSymbol() {
            if (unicodeSymbol == null) return 0;
            try {
                String hex = unicodeSymbol.toUpperCase().replace("U+", "").trim();
                int cp = Integer.parseInt(hex, 16);
                // Only BMP codepoints are safely cast to char; skip surrogates
                if (cp >= 0 && cp <= 0xFFFF) return (char) cp;
            } catch (NumberFormatException ignored) {}
            return 0;
        }

        /**
         * Converts "#FF5555" → nearest Minecraft §-color code string (e.g. "§c").
         * Falls back to "§f" (white) if null or unparseable.
         */
        public String resolveUnicodeColor() {
            if (unicodeColor == null) return "§f";
            try {
                String hex = unicodeColor.trim();
                if (hex.startsWith("#")) hex = hex.substring(1);
                int r = Integer.parseInt(hex.substring(0, 2), 16);
                int g = Integer.parseInt(hex.substring(2, 4), 16);
                int b = Integer.parseInt(hex.substring(4, 6), 16);
                return nearestMinecraftColor(r, g, b);
            } catch (Exception ignored) {
                return "§f";
            }
        }

        /**
         * Builds the full tag string to inject into chat.
         * Format:  <colored text><space><unicodeColor><symbol>§r<space>
         * Example: §b[DEV] §c✦§r
         */
        public String buildTag() {
            String t = text != null ? text : "";
            char sym = resolveSymbol();
            String symPart = sym != 0 ? (resolveUnicodeColor() + sym) : "";
            return t + (symPart.isEmpty() ? "" : " " + symPart) + "§r ";
        }

        // ----------------------------------------------------------------
        // Internal: nearest Minecraft color by Euclidean RGB distance
        // ----------------------------------------------------------------
        private static final int[][] MC_COLORS = {
            {0,   0,   0  }, // §0 black
            {0,   0,   170}, // §1 dark_blue
            {0,   170, 0  }, // §2 dark_green
            {0,   170, 170}, // §3 dark_aqua
            {170, 0,   0  }, // §4 dark_red
            {170, 0,   170}, // §5 dark_purple
            {255, 170, 0  }, // §6 gold
            {170, 170, 170}, // §7 gray
            {85,  85,  85 }, // §8 dark_gray
            {85,  85,  255}, // §9 blue
            {85,  255, 85 }, // §a green
            {85,  255, 255}, // §b aqua
            {255, 85,  85 }, // §c red
            {255, 85,  255}, // §d light_purple
            {255, 255, 85 }, // §e yellow
            {255, 255, 255}, // §f white
        };

        private static String nearestMinecraftColor(int r, int g, int b) {
            int best = 0;
            long bestDist = Long.MAX_VALUE;
            for (int i = 0; i < MC_COLORS.length; i++) {
                long dr = r - MC_COLORS[i][0];
                long dg = g - MC_COLORS[i][1];
                long db = b - MC_COLORS[i][2];
                long dist = dr*dr + dg*dg + db*db;
                if (dist < bestDist) { bestDist = dist; best = i; }
            }
            return "\u00A7" + Integer.toHexString(best);
        }
    }
}
