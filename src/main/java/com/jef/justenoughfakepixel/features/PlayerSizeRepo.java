// Credit: NotEnoughFakepixel (https://github.com/davidbelesp/NotEnoughFakepixel)

package com.jef.justenoughfakepixel.features;

import com.google.gson.reflect.TypeToken;
import com.jef.justenoughfakepixel.repo.JefRepo;
import com.jef.justenoughfakepixel.repo.RepoHandler;
import com.jef.justenoughfakepixel.repo.data.PlayerSizeData;

import java.lang.reflect.Type;
import java.util.List;

public class PlayerSizeRepo {

    private static final Type LIST_TYPE = new TypeToken<List<PlayerSizeData>>(){}.getType();

    private PlayerSizeRepo() {}

    public static PlayerSizeData getScale(String name) {
        List<PlayerSizeData> list = RepoHandler.get(JefRepo.KEY_PLAYERSIZES, LIST_TYPE, null);
        if (list == null || name == null) return null;
        for (PlayerSizeData d : list) if (d != null && name.equals(d.name)) return d;
        return null;
    }
}