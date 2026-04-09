package com.jef.justenoughfakepixel.features.fishing.trophy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TrophyFishStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static TrophyFishStorage INSTANCE;
    private File file;
    private StoredData data = new StoredData();

    private TrophyFishStorage() {
    }

    public static TrophyFishStorage getInstance() {
        if (INSTANCE == null) INSTANCE = new TrophyFishStorage();
        return INSTANCE;
    }

    public void initFile(File configDir) {
        this.file = new File(configDir, "trophy_fish.json");
    }

    public void load() {
        if (file == null || !file.exists()) return;
        try (Reader r = new FileReader(file)) {
            StoredData loaded = GSON.fromJson(r, StoredData.class);
            if (loaded != null) data = loaded;
        } catch (Exception e) {
            System.err.println("[JEF/TrophyFish] Failed to load trophy_fish.json: " + e.getMessage());
        }
    }

    public void save() {
        if (file == null) return;
        try (Writer w = new FileWriter(file)) {
            GSON.toJson(data, w);
        } catch (Exception e) {
            System.err.println("[JEF/TrophyFish] Failed to save trophy_fish.json: " + e.getMessage());
        }
    }

    public Map<String, Map<String, Integer>> getFish() {
        return data.fish;
    }

    public int getCount(String fishName, TrophyRarity rarity) {
        Map<String, Integer> counts = data.fish.get(fishName);
        if (counts == null) return 0;
        return counts.getOrDefault(rarity.name(), 0);
    }

    public int incrementCount(String fishName, TrophyRarity rarity) {
        Map<String, Integer> counts = data.fish.computeIfAbsent(fishName, k -> new LinkedHashMap<>());
        int next = counts.getOrDefault(rarity.name(), 0) + 1;
        counts.put(rarity.name(), next);
        return next;
    }

    public void setCount(String fishName, TrophyRarity rarity, int count) {
        data.fish.computeIfAbsent(fishName, k -> new LinkedHashMap<>()).put(rarity.name(), count);
    }

    public int getTotal(String fishName) {
        Map<String, Integer> counts = data.fish.get(fishName);
        if (counts == null) return 0;
        return counts.values().stream().mapToInt(Integer::intValue).sum();
    }

    public TrophyRarity getBestRarity(String fishName) {
        TrophyRarity best = TrophyRarity.BRONZE;
        for (TrophyRarity r : TrophyRarity.values()) {
            if (getCount(fishName, r) > 0) best = r;
        }
        return best;
    }

    private static class StoredData {
        public Map<String, Map<String, Integer>> fish = new LinkedHashMap<>();
    }
}