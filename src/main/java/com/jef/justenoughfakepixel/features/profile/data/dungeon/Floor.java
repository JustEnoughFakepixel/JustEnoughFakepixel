package com.jef.justenoughfakepixel.features.profile.data.dungeon;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Floor {

    ENTRANCE("Entrance","The Watcher"),
    FLOOR_ONE("Floor I","Bonzo"),
    FLOOR_TWO("Floor II","Scarf"),
    FLOOR_THREE("Floor III","The Professor"),
    FLOOR_FOUR("Floor IV","Thorn"),
    FLOOR_FIVE("Floor V","Livid"),
    FLOOR_SIX("Floor VI","Sadan"),
    FLOOR_SEVEN("Floor VII","Maxor,Storm,Goldor and Necron"),
    ;
    public final String floorName,bossName;

    public static Floor getFloor(String s) {
        for (Floor f : values()){
            if(f.floorName.equalsIgnoreCase(s))return f;
        }
        return null;
    }
}
