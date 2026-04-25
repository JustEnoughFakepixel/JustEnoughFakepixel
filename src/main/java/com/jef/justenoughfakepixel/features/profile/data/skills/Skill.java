package com.jef.justenoughfakepixel.features.profile.data.skills;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Skill {

    FARMING("Farming"),
    MINING("Mining"),
    COMBAT("Combat"),
    FORAGING("Foraging"),
    FISHING("Fishing"),
    ENCHANTING("Enchanting"),
    ALCHEMY("Alchemy"),
    RUNECRAFTING("Runecrafting"),
    SOCIAL("Social"),
    TAMING("Taming");

    public final String name;

    public static Skill get(String s) {
        for(Skill skill : Skill.values()) {
            if(skill.name.equalsIgnoreCase(s)) return skill;
        }
        return null;
    }
}
