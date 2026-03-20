package com.jef.justenoughfakepixel.features.mining;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.JefOverlay;
import com.jef.justenoughfakepixel.utils.ScoreboardUtils;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class PowderOverlay extends JefOverlay {

    // ordinal, gem name, color code
    private static final String[][] GEM_ENTRIES = {
            {"Ruby",       "\u00a7c"},
            {"Sapphire",   "\u00a7b"},
            {"Amber",      "\u00a76"},
            {"Amethyst",   "\u00a75"},
            {"Jade",       "\u00a7a"},
            {"Topaz",      "\u00a7e"},
            {"Jasper",     "\u00a7c"},
            {"Opal",       "\u00a7f"},
            {"Citrine",    "\u00a76"},
            {"Aquamarine", "\u00a73"},
            {"Peridot",    "\u00a7a"},
            {"Onyx",       "\u00a78"},
    };

    private static PowderOverlay instance;

    public PowderOverlay() {
        super(200, 20);
        instance = this;
    }

    public static PowderOverlay getInstance() { return instance; }

    @Override public Position getPosition()     { return JefConfig.feature.mining.powderOverlayPos; }
    @Override public float    getScale()        { return JefConfig.feature.mining.powderOverlayScale; }
    @Override public int      getBgColor()      { return ChromaColour.specialToChromaRGB(JefConfig.feature.mining.powderBgColor); }
    @Override public int      getCornerRadius() { return JefConfig.feature.mining.powderCornerRadius; }
    @Override protected int   getBaseWidth()    { return 200; }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature.mining.powderTracker
                && PowderStats.getInstance().isTrackingEnabled()
                && ScoreboardUtils.getCurrentLocation() == ScoreboardUtils.Location.CRYSTAL_HOLLOWS;
    }

    private String lineForEntry(int ordinal, PowderData d, PowderStats stats, boolean preview) {
        switch (ordinal) {
            case 0:
                return "\u00a7b\u00a7lGemstone Powder Tracker"
                        + (!preview && !PowderStats.getInstance().isTrackingEnabled() ? " \u00a77[Paused]" : "");
            case 1: {
                String rate = preview ? "120" : PowderStats.fmtRate(stats.chestInfo.perHour);
                long   n    = preview ? 420L  : d.totalChestsPicked;
                return String.format("\u00a7d%s Chests \u00a77(%s/h)", PowderStats.fmtNum(n), rate);
            }
            case 2: {
                if (preview)
                    return "\u00a7b2x Powder: \u00a7aActive! \u00a77(5m 20s)";
                boolean dp      = PowderTracker.isDoublePowder();
                String  timeLeft = PowderTracker.getDoublePowderTimeLeft();
                String  suffix  = (dp && timeLeft != null) ? " \u00a77(" + timeLeft + ")" : "";
                return "\u00a7b2x Powder: " + (dp ? "\u00a7aActive!" + suffix : "\u00a7cInactive!");
            }
            case 3: {
                String rate = preview ? "2.5K" : PowderStats.fmtRate(stats.gemstoneInfo.perHour);
                long   n    = preview ? 1337L  : d.gemstonePowder;
                return String.format("\u00a7b%s Gemstone Powder \u00a77(%s/h)", PowderStats.fmtNum(n), rate);
            }
            case 4: {
                long n = preview ? 12L : d.diamondEssence;
                if (!preview && n == 0) return null;
                return String.format("\u00a7b%s Diamond Essence", PowderStats.fmtNum(n));
            }
            case 5: {
                long n = preview ? 66L : d.goldEssence;
                if (!preview && n == 0) return null;
                return String.format("\u00a76%s Gold Essence", PowderStats.fmtNum(n));
            }
            case 6: {
                long n = preview ? 8L : d.oilBarrels;
                if (!preview && n == 0) return null;
                return String.format("\u00a78%s Oil Barrel%s", PowderStats.fmtNum(n), n == 1 ? "" : "s");
            }
            case 7: {
                long n = preview ? 3L : d.ascensionRopes;
                if (!preview && n == 0) return null;
                return String.format("\u00a75%s Ascension Rope%s", PowderStats.fmtNum(n), n == 1 ? "" : "s");
            }
            case 8: {
                long n = preview ? 2L : d.wishingCompasses;
                if (!preview && n == 0) return null;
                return String.format("\u00a79%s Wishing Compass%s", PowderStats.fmtNum(n), n == 1 ? "" : "es");
            }
            case 9: {
                long n = preview ? 1L : d.jungleHearts;
                if (!preview && n == 0) return null;
                return String.format("\u00a72%s Jungle Heart%s", PowderStats.fmtNum(n), n == 1 ? "" : "s");
            }
            case 10: {
                long raw       = preview ? 512L   : d.hardStone;
                long compacted = preview ? 5L     : d.hardStoneCompacted;
                String rate    = preview ? "1.5K" : PowderStats.fmtRate(stats.hardStoneInfo.perHour);
                if (!preview && raw + compacted == 0) return null;
                return String.format("\u00a77%s Hard Stone \u00a78(%s compact) \u00a77(%s/h)",
                        PowderStats.fmtNum(raw), PowderStats.fmtNum(compacted), rate);
            }
            default: {
                int gemIndex = ordinal - 11;
                if (gemIndex < 0 || gemIndex >= GEM_ENTRIES.length) return null;
                return gemLine(GEM_ENTRIES[gemIndex][0], GEM_ENTRIES[gemIndex][1], d, preview);
            }
        }
    }

    private static String gemLine(String gem, String color, PowderData d, boolean preview) {
        if (preview) {
            return String.format("\u00a75%s\u00a77-\u00a79%s\u00a77-\u00a7a%s\u00a77-\u00a7f%s %s%s Gemstone",
                    1, 3, 4, 0, color, gem);
        }
        long[] bd = PowderStats.getGemBreakdown(d, gem);
        if (bd[0] + bd[1] + bd[2] + bd[3] == 0) return null;
        return String.format("\u00a75%s\u00a77-\u00a79%s\u00a77-\u00a7a%s\u00a77-\u00a7f%s %s%s Gemstone",
                bd[0], bd[1], bd[2], bd[3], color, gem);
    }

    @Override
    public List<String> getLines(boolean preview) {
        List<String> lines = new ArrayList<>();
        PowderStats  stats = PowderStats.getInstance();
        PowderData   d     = stats.getData();

        for (Object entry : JefConfig.feature.mining.powderDisplayLines) {
            int    ordinal = (entry instanceof Number) ? ((Number) entry).intValue() : -1;
            String line    = lineForEntry(ordinal, d, stats, preview);
            if (line != null) lines.add(line);
        }
        return lines;
    }
}