package com.jef.justenoughfakepixel.features.mining.pristine;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.features.mining.powder.PowderStats;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.data.SkyblockData;
import com.jef.justenoughfakepixel.utils.overlay.Overlay;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class PristineOverlay extends Overlay {

    private static final String[][] GEM_ENTRIES = {{"Ruby", "§c"}, {"Sapphire", "§b"}, {"Amber", "§6"}, {"Amethyst", "§5"}, {"Jade", "§a"}, {"Topaz", "§e"}, {"Jasper", "§c"}, {"Opal", "§f"}, {"Citrine", "§6"}, {"Aquamarine", "§3"}, {"Peridot", "§a"}, {"Onyx", "§8"}};

    @Getter
    private static PristineOverlay instance;

    public PristineOverlay() {
        super(200, 20);
        instance = this;
    }

    private static String gemLine(String gem, String color, PristineData d, boolean preview, boolean compacted) {
        long flawed = d.gemstones.getOrDefault("Flawed_" + gem, 0L);
        long fine = d.gemstones.getOrDefault("Fine_" + gem, 0L);
        long flawless = d.gemstones.getOrDefault("Flawless_" + gem, 0L);
        long total = flawed + fine + flawless;
        if (!preview && total == 0) return null;

        if (preview) {
            return compacted ? String.format("§5%s§7-§9%s§7-§a%s %s%s Gemstone", 2, 10, 40, color, gem) : String.format("§a%s %s%s Gemstone", 200, color, gem);
        }

        if (compacted) {
            long[] bd = PristineStats.getGemBreakdown(d, gem);
            return String.format("§5%s§7-§9%s§7-§a%s %s%s Gemstone", bd[0], bd[1], bd[2], color, gem);
        }
        return String.format("§a%s %s%s Gemstone", PowderStats.fmtNum(total), color, gem);
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.mining.pristineTrackerConfig.pristineOverlayPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.mining.pristineTrackerConfig.pristineOverlayScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.mining.pristineTrackerConfig.pristineBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.mining.pristineTrackerConfig.pristineCornerRadius;
    }

    @Override
    protected int getBaseWidth() {
        return 200;
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature.mining.pristineTrackerConfig.pristineTracker && PristineStats.getInstance().isTrackingEnabled() && SkyblockData.getCurrentLocation() == SkyblockData.Location.CRYSTAL_HOLLOWS;
    }

    private String lineForEntry(int ordinal, PristineData d, PristineStats stats, boolean preview) {
        if (ordinal == 0) {
            return "§d§lPristine Tracker" + (!preview && !stats.isTrackingEnabled() ? " §7[Paused]" : "");
        }
        if (ordinal == 1) {
            long total = preview ? 1500L : d.gemstones.values().stream().mapToLong(Long::longValue).sum();
            String rate = preview ? "150" : PowderStats.fmtRate(stats.rateInfo.perHour);
            return String.format("§7Total Gems: §a%s §7(%s/h)", PowderStats.fmtNum(total), rate);
        }
        if (ordinal == 2) {
            int procs = preview ? 42 : d.totalProcs;
            String rate = preview ? "5" : PowderStats.fmtRate(stats.procRateInfo.perHour);
            return String.format("§7Procs: §d%s §7(%s/h)", procs, rate);
        }
        int gemIndex = ordinal - 3;
        if (gemIndex < 0 || gemIndex >= GEM_ENTRIES.length) return null;
        return gemLine(GEM_ENTRIES[gemIndex][0], GEM_ENTRIES[gemIndex][1], d, preview, JefConfig.feature.mining.pristineTrackerConfig.showCompacted);
    }

    @Override
    public List<String> getLines(boolean preview) {
        List<String> lines = new ArrayList<>();
        PristineStats stats = PristineStats.getInstance();
        PristineData d = stats.getData();

        for (Object entry : JefConfig.feature.mining.pristineTrackerConfig.pristineDisplayLines) {
            int ordinal = (entry instanceof Number) ? ((Number) entry).intValue() : -1;
            String line = lineForEntry(ordinal, d, stats, preview);
            if (line != null) lines.add(line);
        }
        return lines;
    }
}
