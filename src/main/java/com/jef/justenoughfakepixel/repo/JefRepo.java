// Credit: NotEnoughFakepixel (https://github.com/davidbelesp/NotEnoughFakepixel)

package com.jef.justenoughfakepixel.repo;

public class JefRepo {

    public static final String KEY_UPDATE = "update";
    public static final String KEY_PLAYERSIZES = "playersizes";
    public static final String KEY_ENCHANTS = "enchants";
    public static final String KEY_TIMERS = "timers";
    public static final String KEY_TAGS = "tags";
    private static final String BASE = "https://raw.githubusercontent.com/JustEnoughFakepixel/JustEnoughFakepixel-REPO/main/";

    private JefRepo() {
    }

    public static void init() {
        RepoHandler.register(KEY_UPDATE, BASE + "data/update.json");
        RepoHandler.register(KEY_PLAYERSIZES, BASE + "data/playersizes.json");
        RepoHandler.register(KEY_ENCHANTS, BASE + "data/enchants.json");
        RepoHandler.register(KEY_TIMERS, BASE + "data/timers.json");
        RepoHandler.register(KEY_TAGS, BASE + "data/tags.json");
        RepoHandler.warmupAll();
    }
}