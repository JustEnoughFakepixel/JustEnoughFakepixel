package com.jef.justenoughfakepixel.features.mining.fetchur;

import java.util.concurrent.TimeUnit;

public class FetchurData {

    private static final String[][] ITEMS = {
            {"Yellow Stained Glass", "x20"},
            {"Compass", "x1"},
            {"Mithril", "x20"},
            {"Firework Rocket", "x1"},
            {"Wooden Door", "x1 (any type)"},
            {"Rabbit's Foot", "x3"},
            {"Superboom TNT", "x1"},
            {"Pumpkin", "x1"},
            {"Flint and Steel", "x1"},
            {"Nether Quartz Ore", "x50"},
            {"Red Wool", "x50"},
    };

    private FetchurData() {}

    public static String getTodaysItem() {
        int idx = (int) (TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) % ITEMS.length);
        return ITEMS[idx][0] + " " + ITEMS[idx][1];
    }
}