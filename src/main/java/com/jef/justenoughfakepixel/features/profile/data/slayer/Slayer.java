package com.jef.justenoughfakepixel.features.profile.data.slayer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Slayer {

    ZOMBIE("Zombie Slayer",9),
    SPIDER("Spider Slayer",11),
    WOLF("Wolf Slayer",13),
    ENDERMAN("Enderman Slayer",15),
    BLAZE("Blaze Slayer",17)
    ;
    public final String itemName;
    public final int itemSlot;
}
