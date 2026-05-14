package com.jef.justenoughfakepixel.features.profile.data.slayer;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class SlayerData {

    public int curLevel;
    public long curExp,reqExp;
    public int t1Kills,t2Kills,t3Kills,t4Kills,t5Kills;
    public Map<String,Integer> dropMap;

}
