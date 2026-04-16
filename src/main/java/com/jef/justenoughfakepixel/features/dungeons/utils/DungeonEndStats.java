package com.jef.justenoughfakepixel.features.dungeons.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class DungeonEndStats {
    
    @Getter
    private final List<String> xp = new ArrayList<>();
    
    @Getter @Setter
    private String bossName;
    
    @Getter @Setter
    private String score;
    
    @Getter @Setter
    private String grade;
    
    @Getter @Setter
    private boolean scorePB;
    
    public void addXp(String xpLine) {
        xp.add(xpLine);
    }
    
    public void reset() {
        bossName = score = grade = null;
        scorePB = false;
        xp.clear();
    }
}
