package com.jef.justenoughfakepixel.features.profile.data.dungeon;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FloorData {

    public Floor floor;
    public int bossKills;
    public long fastestTime,fastestSTime,fastestSPlusTime;
    public int bestScore;
    public long mostHealerDmg,mostMageDamage,mostArcherDamage,mostBersDamage,mostTankDamage,mostAllyHealing;
    public int totalEnemiesKilled,mostEnemiesKilled;

}
