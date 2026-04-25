package com.jef.justenoughfakepixel.features.profile.data;

import com.jef.justenoughfakepixel.features.profile.data.base.BaseData;
import com.jef.justenoughfakepixel.features.profile.data.dungeon.DungeonData;
import com.jef.justenoughfakepixel.features.profile.data.skills.Skill;
import com.jef.justenoughfakepixel.features.profile.data.skills.SkillData;
import lombok.AllArgsConstructor;

import java.util.EnumMap;

@AllArgsConstructor
public class ProfileData {

    public BaseData baseData;
    public InventoryData inventoryData;
    public EnumMap<Skill, SkillData> skillData;
    public HOTMData hotmData;
    public DungeonData dungeonData;

}
