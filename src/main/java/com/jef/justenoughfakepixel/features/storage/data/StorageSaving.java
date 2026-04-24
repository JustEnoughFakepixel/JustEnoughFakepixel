package com.jef.justenoughfakepixel.features.storage.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.features.storage.utils.SContainer;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class StorageSaving {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static File getStorageFolder() {
        String username = Minecraft.getMinecraft().getSession().getUsername();
        return new File(JefConfig.configDirectory, "storage/" + username);
    }

    public static LinkedHashMap<String, SContainer> loadStorageData() {
        TreeMap<String, SContainer> sorted = new TreeMap<>((a, b) -> {
            String[] partsA = a.split("-", 2);
            String[] partsB = b.split("-", 2);
            int prefixCmp = partsB[0].compareTo(partsA[0]);
            if (prefixCmp != 0) return prefixCmp;
            try {
                return Integer.compare(Integer.parseInt(partsA[1]), Integer.parseInt(partsB[1]));
            } catch (NumberFormatException e) {
                return partsA[1].compareTo(partsB[1]);
            }
        });
        File folder = getStorageFolder();
        if (!folder.exists()) {
            folder.mkdirs();
            return new LinkedHashMap<>();
        }
        for (File file : folder.listFiles()) {
            try {
                if (!file.exists()) {
                    file.createNewFile();
                    continue;
                }
                SContainer container = gson.fromJson(new FileReader(file), SContainer.class);
                if (container != null && !container.empty) {
                    sorted.put(container.id, container);
                }
            } catch (IOException e) {
                JefMod.logger.info("Error while trying to load " + file.getName() + " ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return new LinkedHashMap<>(sorted);
    }

    public static void saveStorageData(Collection<SContainer> containers) {
        File folder = getStorageFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        for (SContainer container : containers) {
            if (container.empty) continue;
            File file = new File(folder, container.id + ".json");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter writer = new FileWriter(file);
                writer.write(gson.toJson(container));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                JefMod.logger.info("ERROR While Saving " + container.id + " ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}