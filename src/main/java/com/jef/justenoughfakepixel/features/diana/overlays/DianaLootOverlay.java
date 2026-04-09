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
public class DianaLootOverlay extends Overlay {

    @Getter
    private static DianaLootOverlay instance;

    public DianaLootOverlay() {
        super(180, LINE_HEIGHT * 9 + PADDING * 2);
        instance = this;
    }

    @Override
    protected int getBaseWidth() {
        return 180;
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.diana.lootOverlayPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.diana.lootScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.diana.lootBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.diana.lootCornerRadius;
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature.diana.enabled && JefConfig.feature.diana.showLootOverlay;
    }

    @Override
    public List<String> getLines(boolean preview) {
        List<String> lines = new ArrayList<>();

        if (preview) {
            lines.add("§6§lDiana Loot");
            lines.add("§7Inqs since Chimera: §f4  §7[§bLS §f3§7]");
            lines.add("§dChimeras: §f1");
            lines.add("§1Feathers: §f5");
            lines.add("§2Shelmets: §f2  §5Remedies: §f1  §5Plushies: §f0");
            lines.add("§6Daedalus Sticks: §f2  §7(since last: §f12§7)");
            lines.add("§5Minos Relics: §f1  §7(since last: §f30§7)");
            lines.add("§5Souvenirs: §f2  §6Crowns: §f1");
            lines.add("§6Coins: §f1.2M");
            return lines;
        }

        DianaStats stats = DianaStats.getInstance();
        if (!stats.isTracking()) return lines;

        DianaData d = stats.getData();

        lines.add("§6§lDiana Loot");
        lines.add(String.format("§7Inqs since Chimera: §f%d%s", d.inqsSinceChimera, d.getLootsharedSuffix()));
        lines.add(String.format("§dChimeras: §f%d", d.totalChimeras));
        lines.add(String.format("§1Feathers: §f%d", d.griffinFeathers));
        lines.add(String.format("§2Shelmets: §f%d  §5Remedies: §f%d  §5Plushies: §f%d", d.dwarfTurtleShelmets, d.antiqueRemedies, d.crochetTigerPlushies));
        lines.add(String.format("§6Daedalus Sticks: §f%d  §7(since last: §f%d§7)", d.totalSticks, d.minotaursSinceStick));
        lines.add(String.format("§5Minos Relics: §f%d  §7(since last: §f%d§7)", d.totalRelics, d.champsSinceRelic));
        lines.add(String.format("§5Souvenirs: §f%d  §6Crowns: §f%d", d.souvenirs, d.crownsOfGreed));
        lines.add(String.format("§6Coins: §f%s", DianaStats.fmtCoins(d.totalCoins)));

        return lines;
    }
}