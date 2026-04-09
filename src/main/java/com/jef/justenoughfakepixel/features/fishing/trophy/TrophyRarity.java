package com.jef.justenoughfakepixel.features.fishing.trophy;

public enum TrophyRarity {

    BRONZE("§8", "Bronze"), SILVER("§7", "Silver"), GOLD("§6", "Gold"), DIAMOND("§b", "Diamond");

    public final String formatCode;
    public final String displayName;

    TrophyRarity(String formatCode, String displayName) {
        this.formatCode = formatCode;
        this.displayName = displayName;
    }

    public static TrophyRarity fromDisplayName(String name) {
        if (name == null) return null;
        for (TrophyRarity r : values()) {
            if (r.displayName.equalsIgnoreCase(name.trim())) return r;
        }
        return null;
    }

    public static TrophyRarity getByName(String rawName) {
        if (rawName == null) return null;
        String upper = rawName.toUpperCase();
        for (TrophyRarity r : values()) {
            if (upper.endsWith(r.name())) return r;
        }
        return null;
    }

    public String getFormatted() {
        return formatCode + displayName;
    }
}