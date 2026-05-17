package com.jef.justenoughfakepixel.utils.data;

import com.jef.justenoughfakepixel.features.dungeons.utils.DungeonFloor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DungeonUtils {

    private static final Pattern FLOOR_PAT = Pattern.compile("\\(([EFM][\\d])\\)");

    private DungeonUtils() {}

    public static DungeonFloor getFloorFromScoreboard() {
        for (String line : SkyblockData.getScoreboardLines()) {
            Matcher m = FLOOR_PAT.matcher(line);
            if (m.find()) {
                return DungeonFloor.fromString(m.group(1));
            }
        }
        return DungeonFloor.NONE;
    }
}
