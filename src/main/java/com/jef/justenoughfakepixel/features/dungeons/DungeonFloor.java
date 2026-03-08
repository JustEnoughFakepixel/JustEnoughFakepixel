package com.jef.justenoughfakepixel.features.dungeons;

import java.util.Arrays;

public enum DungeonFloor {
    NONE(-1),
    E0(20), F1(30), F2(40), F3(50), F4(60), F5(70), F6(85), F7(100),
    M1(30),  M2(40),  M3(50),  M4(60),  M5(70),  M6(85),  M7(100);

    public final int secretPercentage;

    DungeonFloor(int secretPercentage) {
        this.secretPercentage = secretPercentage;
    }

    public boolean isMasterMode() {
        return name().startsWith("M");
    }

    public boolean isF7orM7() {
        return this == F7 || this == M7;
    }

    public boolean isF6orM6() {
        return this == F6 || this == M6;
    }

    public static DungeonFloor fromString(String s) {
        return Arrays.stream(values())
                .filter(f -> f.name().equalsIgnoreCase(s))
                .findFirst()
                .orElse(NONE);
    }
}