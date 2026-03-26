package com.jef.justenoughfakepixel.features.fishing.trophy;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.JefOverlay;
import com.jef.justenoughfakepixel.utils.ScoreboardUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@RegisterEvents
public class TrophyFishOverlay extends JefOverlay {

    private static TrophyFishOverlay instance;

    public TrophyFishOverlay() {
        super(180, 20);
        instance = this;
    }

    public static TrophyFishOverlay getInstance() { return instance; }

    @Override public Position getPosition()     { return JefConfig.feature.fishing.trophyFishPos; }
    @Override public float    getScale()        { return JefConfig.feature.fishing.trophyFishScale; }
    @Override public int      getBgColor()      { return ChromaColour.specialToChromaRGB(JefConfig.feature.fishing.trophyFishBgColor); }
    @Override public int      getCornerRadius() { return JefConfig.feature.fishing.trophyFishCornerRadius; }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature != null && JefConfig.feature.fishing.trophyOverlay;
    }

    @Override
    protected boolean extraGuard() {
        if (!JefConfig.feature.fishing.trophyOnlyCrimson) return true;
        return ScoreboardUtils.getCurrentLocation() == ScoreboardUtils.Location.CRIMSON_ISLE;
    }

    @Override
    public List<String> getLines(boolean preview) {
        List<String> out = new ArrayList<>();
        out.add("§6§lTrophy Fish");

        if (preview) {
            out.add("§9Lavahorse     §88  §75  §62  §b1");
            out.add("§5Soul Fish     §840 §720 §61  §b0");
            out.add("§6Golden Fish   §8100 §750 §625 §b5");
            return out;
        }

        TrophyFishStorage storage = TrophyFishStorage.getInstance();
        Map<String, Map<String, Integer>> fish = storage.getFish();

        if (fish.isEmpty()) {
            out.add("§cNo data — open Trophy Fishing at Odger");
            return out;
        }

        fish.entrySet().stream()
            .filter(e -> storage.getTotal(e.getKey()) > 0)
            .sorted(Comparator.comparingInt((Map.Entry<String, Map<String, Integer>> e) ->
                    storage.getTotal(e.getKey())).reversed())
            .forEach(entry -> {
                String name = entry.getKey();
                int b = storage.getCount(name, TrophyRarity.BRONZE);
                int s = storage.getCount(name, TrophyRarity.SILVER);
                int g = storage.getCount(name, TrophyRarity.GOLD);
                int d = storage.getCount(name, TrophyRarity.DIAMOND);
                String coloredName = storage.getBestRarity(name).formatCode + name;
                out.add(coloredName + "  §8" + b + " §7" + s + " §6" + g + " §b" + d);
            });

        return out;
    }
}