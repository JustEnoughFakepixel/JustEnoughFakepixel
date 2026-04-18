package com.jef.justenoughfakepixel.features.capes;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.features.capes.impl.TestCape;

import java.util.HashMap;

public class CapeManager {

    public static HashMap<String,Cape> capes = new HashMap<>();
    public static HashMap<String,String> activeCapes = new HashMap<>();

    public static void applyCape(String player,Cape cape){
        activeCapes.put(player,cape.id);
    }

    public static Cape getCapeForPlayer(String pl){
        return capes.get(activeCapes.get(pl));
    }

    public static boolean hasCape(String user) {
        if(!JefConfig.feature.cosmetics.capesEnabled){return false;}
        return activeCapes.containsKey(user);
    }

    public static void initialise(boolean force){
        if(!JefConfig.feature.cosmetics.capesEnabled && !force) return;
        capes.clear();
        register(new TestCape());
    }

    public static void register(Cape cape){
        capes.put(cape.id, cape);
    }

    public static Cape getCape(String id){
        return capes.get(id);
    }

    public static void reload() {
        capes.clear();
        initialise(true);
    }

}
