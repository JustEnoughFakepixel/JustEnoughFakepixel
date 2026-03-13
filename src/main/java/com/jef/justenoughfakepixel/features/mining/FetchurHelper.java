package com.jef.justenoughfakepixel.features.mining;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class FetchurHelper {

    private FetchurHelper() {}

    // fetchur item rotation
    private static final String[][] ITEMS = {
            { "Yellow Stained Glass", "x20" },
            { "Compass",              "x1"  },
            { "Mithril",              "x20" },
            { "Firework Rocket",      "x1"  },
            { "Coffee",               "x1 (Cheap/Decent/Black)" },
            { "Wooden Door",          "x1 (any type)"           },
            { "Rabbit's Foot",        "x3"  },
            { "Superboom TNT",        "x1"  },
            { "Pumpkin",              "x1"  },
            { "Flint and Steel",      "x1"  },
            { "Nether Quartz Ore",    "x50" },
            { "Red Wool",             "x50" },
    };

    private static final Calendar CALENDAR =
            new GregorianCalendar(TimeZone.getTimeZone("America/New_York"));

    public static String getTodaysItem() {
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        int idx = (CALENDAR.get(Calendar.DAY_OF_MONTH) - 1) % ITEMS.length;
        return ITEMS[idx][0] + " " + ITEMS[idx][1];
    }
}