package com.jef.justenoughfakepixel.features.profile.data.dungeon;

import lombok.AllArgsConstructor;

import java.util.EnumMap;

@AllArgsConstructor
public class DungeonData {

    public int cataLevel,healerLevel,mageLevel,archerLevel,bersLevel,tankLevel;
    public long curProgress,reqProgress;
    public EnumMap<Floor,FloorData> floorData;

}
