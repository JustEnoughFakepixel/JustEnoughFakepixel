package com.jef.justenoughfakepixel.features.diana.overlays;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.features.diana.DianaData;
import com.jef.justenoughfakepixel.features.diana.DianaStats;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.overlay.Overlay;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class DianaEventOverlay extends Overlay {

    @Getter
    private static DianaEventOverlay instance;

    public DianaEventOverlay() {
        super(180, LINE_HEIGHT * 7 + PADDING * 2);
        instance = this;
    }

    @Override
    protected int getBaseWidth() {
        return 180;
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.diana.eventOverlayPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.diana.eventScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.diana.eventBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.diana.eventCornerRadius;
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature.diana.enabled && JefConfig.feature.diana.showEventOverlay;
    }

    @Override
    public List<String> getLines(boolean preview) {
        List<String> lines = new ArrayList<>();

        if (preview) {
            lines.add("§e§lDiana Event");
            lines.add("§9Total Mobs: §f165");
            lines.add("§1Playtime: §f2h 30m  §1Session: §f45m");
            lines.add("§eBurrows: §f42  §7(§a120.0§7/hr)");
            lines.add("§dInquisitor §d4.20% §f(7) §7[§bLS §f3§7]");
            lines.add("§6Minotaur §d12.30% §f(45)");
            lines.add("§5Minos Champion §d8.10% §f(30)");
            lines.add("§fGaia Construct §d5.00% §f(8)");
            lines.add("§aMinos Hunter §d20.00% §f(33)");
            lines.add("§eSiamese Lynx §d10.00% §f(17)");
            return lines;
        }

        DianaStats stats = DianaStats.getInstance();
        if (!stats.isTracking()) return lines;

        DianaData d = stats.getData();
        double bph = stats.getBph();

        lines.add("§e§lDiana Event");
        lines.add(String.format("§9Total Mobs: §f%d", d.totalMobs));
        lines.add(String.format("§1Playtime: §f%s  §1Session: §f%s", DianaStats.formatTime(d.activeTimeMs), DianaStats.formatTime(stats.getSessionTimeMs())));
        lines.add(String.format("§eBurrows: §f%d  §7(§a%.1f§7/hr)", d.totalBorrows, bph));

        lines.add(String.format("§dInquisitor §d%s §f(%d)%s", stats.formatMobPct(d.totalInqs), d.totalInqs, d.getLootsharedSuffix()));
        lines.add(String.format("§6Minotaur §d%s §f(%d)", stats.formatMobPct(d.totalMinotaurs), d.totalMinotaurs));
        lines.add(String.format("§5Minos Champion §d%s §f(%d)", stats.formatMobPct(d.totalChamps), d.totalChamps));
        lines.add(String.format("§fGaia Construct §d%s §f(%d)", stats.formatMobPct(d.totalGaiaConstructs), d.totalGaiaConstructs));
        lines.add(String.format("§aMinos Hunter §d%s §f(%d)", stats.formatMobPct(d.totalMinosHunters), d.totalMinosHunters));
        lines.add(String.format("§eSiamese Lynx §d%s §f(%d)", stats.formatMobPct(d.totalSiameseLynxes), d.totalSiameseLynxes));

        return lines;
    }
}