package com.jef.justenoughfakepixel.features.mining.powder;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.data.SkyblockData;
import com.jef.justenoughfakepixel.utils.overlay.Overlay;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class PowderOverlay extends Overlay {

    // ordinal, gem name, color code
    private static final String[][] GEM_ENTRIES = {{"Ruby", "§c"}, {"Sapphire", "§b"}, {"Amber", "§6"}, {"Amethyst", "§5"}, {"Jade", "§a"}, {"Topaz", "§e"}, {"Jasper", "§c"}, {"Opal", "§f"}, {"Citrine", "§6"}, {"Aquamarine", "§3"}, {"Peridot", "§a"}, {"Onyx", "§8"},};

    @Getter
    private static PowderOverlay instance;

    public PowderOverlay() {
        super(200, 20);
        instance = this;
    }

    private static String gemLine(String gem, String color, PowderData d, boolean preview) {
        if (preview) {
            return String.format("§5%s§7-§9%s§7-§a%s§7-§f%s %s%s Gemstone", 1, 3, 4, 0, color, gem);
        }
        long[] bd = PowderStats.getGemBreakdown(d, gem);
        if (bd[0] + bd[1] + bd[2] + bd[3] == 0) return null;
        return String.format("§5%s§7-§9%s§7-§a%s§7-§f%s %s%s Gemstone", bd[0], bd[1], bd[2], bd[3], color, gem);
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.mining.powderOverlayPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.mining.powderOverlayScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.mining.powderBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.mining.powderCornerRadius;
    }

    @Override
    protected int getBaseWidth() {
        return 200;
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature.mining.powderTracker && PowderStats.getInstance().isTrackingEnabled() && SkyblockData.getCurrentLocation() == SkyblockData.Location.CRYSTAL_HOLLOWS;
    }

    private String lineForEntry(int ordinal, PowderData d, PowderStats stats, boolean preview) {
        switch (ordinal) {
            case 0:
                return "§b§lPowder Tracker" + (!preview && !PowderStats.getInstance().isTrackingEnabled() ? " §7[Paused]" : "");
            case 1: {
                String rate = preview ? "120" : PowderStats.fmtRate(stats.chestInfo.perHour);
                long n = preview ? 420L : d.totalChestsPicked;
                return String.format("§7%s Chests §7(%s/h)", PowderStats.fmtNum(n), rate);
            }
            case 2: {
                if (preview) return "§b2x Powder: §aActive! §7(5m 20s)";
                boolean dp = PowderTracker.isDoublePowder();
                String timeLeft = PowderTracker.getDoublePowderTimeLeft();
                String suffix = (dp && timeLeft != null) ? " §7(" + timeLeft + ")" : "";
                return "§b2x Powder: " + (dp ? "§aActive!" + suffix : "§cInactive!");
            }
            case 3: {
                String rate = preview ? "2.5K" : PowderStats.fmtRate(stats.gemstoneInfo.perHour);
                long n = preview ? 1337L : d.gemstonePowder;
                return String.format("§d%s Gemstone Powder §7(%s/h)", PowderStats.fmtNum(n), rate);
            }
            case 4: {
                long n = preview ? 12L : d.diamondEssence;
                if (!preview && n == 0) return null;
                return String.format("§b%s Diamond Essence", PowderStats.fmtNum(n));
            }
            case 5: {
                long n = preview ? 66L : d.goldEssence;
                if (!preview && n == 0) return null;
                return String.format("§6%s Gold Essence", PowderStats.fmtNum(n));
            }
            case 6: {
                long n = preview ? 8L : d.oilBarrels;
                if (!preview && n == 0) return null;
                return String.format("§8%s Oil Barrel%s", PowderStats.fmtNum(n), n == 1 ? "" : "s");
            }
            case 7: {
                long n = preview ? 3L : d.ascensionRopes;
                if (!preview && n == 0) return null;
                return String.format("§5%s Ascension Rope%s", PowderStats.fmtNum(n), n == 1 ? "" : "s");
            }
            case 8: {
                long n = preview ? 2L : d.wishingCompasses;
                if (!preview && n == 0) return null;
                return String.format("§9%s Wishing Compass%s", PowderStats.fmtNum(n), n == 1 ? "" : "es");
            }
            case 9: {
                long n = preview ? 1L : d.jungleHearts;
                if (!preview && n == 0) return null;
                return String.format("§6%s Jungle Heart%s", PowderStats.fmtNum(n), n == 1 ? "" : "s");
            }
            case 10: {
                long raw = preview ? 512L : d.hardStone;
                long compacted = preview ? 5L : d.hardStoneCompacted;
                String rate = preview ? "1.5K" : PowderStats.fmtRate(stats.hardStoneInfo.perHour);
                if (!preview && raw + compacted == 0) return null;
                return String.format("§a%s Hard Stone §8(%s compact) §7(%s/h)", PowderStats.fmtNum(raw), PowderStats.fmtNum(compacted), rate);
            }
            default: {
                int gemIndex = ordinal - 11;
                if (gemIndex < 0 || gemIndex >= GEM_ENTRIES.length) return null;
                return gemLine(GEM_ENTRIES[gemIndex][0], GEM_ENTRIES[gemIndex][1], d, preview);
            }
        }
    }

    @Override
    public List<String> getLines(boolean preview) {
        List<String> lines = new ArrayList<>();
        PowderStats stats = PowderStats.getInstance();
        PowderData d = stats.getData();

        for (Object entry : JefConfig.feature.mining.powderDisplayLines) {
            int ordinal = (entry instanceof Number) ? ((Number) entry).intValue() : -1;
            String line = lineForEntry(ordinal, d, stats, preview);
            if (line != null) lines.add(line);
        }
        return lines;
    }
}