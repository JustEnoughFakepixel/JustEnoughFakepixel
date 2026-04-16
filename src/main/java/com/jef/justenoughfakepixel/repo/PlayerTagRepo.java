package com.jef.justenoughfakepixel.repo;

import com.jef.justenoughfakepixel.repo.data.PlayerTagData;

public class PlayerTagRepo {

    private PlayerTagRepo() {}

    /**
     * Returns the tag entry for a given IGN, or null if none exists.
     * Matching is case-insensitive.
     */
    public static PlayerTagData.Entry getTag(String ign) {
        PlayerTagData data = RepoHandler.get(JefRepo.KEY_TAGS, PlayerTagData.class, null);
        if (data == null || data.tags == null || ign == null) return null;
        String lower = ign.toLowerCase();
        for (PlayerTagData.Entry entry : data.tags) {
            if (entry != null && lower.equals(entry.name != null ? entry.name.toLowerCase() : null)) {
                return entry;
            }
        }
        return null;
    }
}
