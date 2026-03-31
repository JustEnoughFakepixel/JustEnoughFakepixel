package com.jef.justenoughfakepixel.features.mining.hotm;

import java.util.LinkedHashMap;
import java.util.Map;

// this is here for when fakepixel updates COTM
@Deprecated
public class CoreOfTheMountainData {

    public static final String GUI_NAME = "Core of the Mountain";

    public static final int MAX_LEVEL = 7;

    private static final Map<HotmPerkData.PowderType, Long>[] LEVEL_COSTS;

    static {
        LEVEL_COSTS = new Map[11];

        for (int i = 0; i < LEVEL_COSTS.length; i++) LEVEL_COSTS[i] = new LinkedHashMap<>();


        // Level 2
        LEVEL_COSTS[2].put(HotmPerkData.PowderType.MITHRIL,   50_000L);
        // Level 3
        LEVEL_COSTS[3].put(HotmPerkData.PowderType.MITHRIL,  100_000L);
        // Level 4
        LEVEL_COSTS[4].put(HotmPerkData.PowderType.GEMSTONE, 200_000L);
        // Level 5
        LEVEL_COSTS[5].put(HotmPerkData.PowderType.GEMSTONE, 300_000L);
        // Level 6
        LEVEL_COSTS[6].put(HotmPerkData.PowderType.GEMSTONE, 400_000L);
        // Level 7
        LEVEL_COSTS[7].put(HotmPerkData.PowderType.GEMSTONE, 500_000L);

        // Uncomment when extending to level 10
        // LEVEL_COSTS[8].put(HotmPerkData.PowderType.GLACITE,  750_000L);
        // LEVEL_COSTS[9].put(HotmPerkData.PowderType.GLACITE, 1_000_000L);
        // LEVEL_COSTS[10].put(HotmPerkData.PowderType.GLACITE, 1_250_000L);
    }
    public static Map<HotmPerkData.PowderType, Long> cumulativeCost(int level) {
        Map<HotmPerkData.PowderType, Long> totals = new LinkedHashMap<>();
        for (int l = 2; l <= Math.min(level, LEVEL_COSTS.length - 1); l++) {
            LEVEL_COSTS[l].forEach((type, cost) ->
                    totals.merge(type, cost, Long::sum));
        }
        return totals;
    }

    public static Map<HotmPerkData.PowderType, Long> rangeCost(int fromLevel, int toLevel) {
        Map<HotmPerkData.PowderType, Long> totals = new LinkedHashMap<>();
        for (int l = fromLevel + 1; l <= Math.min(toLevel, LEVEL_COSTS.length - 1); l++) {
            LEVEL_COSTS[l].forEach((type, cost) ->
                    totals.merge(type, cost, Long::sum));
        }
        return totals;
    }
}