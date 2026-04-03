package com.jef.justenoughfakepixel.features.itemlist;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.core.JefConfig;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ItemRegistry {

    // LINKS
    private static final String NEU_REPO = "https://github.com/NotEnoughUpdates/NotEnoughUpdates-REPO/archive/refs/heads/master.zip";
    private static final String JEF_REPO = "https://github.com/protocol-8/JustEnoughFakepixel-REPO/archive/refs/heads/master.zip";

    public static ConcurrentHashMap<String,String> itemData = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, SkyblockItem> skyblockItems = new ConcurrentHashMap<>();


    public static void initialise(boolean force){
        if(!force){
            if(!JefConfig.feature.misc.enableItemList) return;
        }
        itemData.clear();
        skyblockItems.clear();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(ItemRegistry::loadJEFItems);
        executor.execute(ItemRegistry::loadNEUItems);
        executor.shutdown();

        try {
            if (!executor.awaitTermination(2, java.util.concurrent.TimeUnit.MINUTES)) {
                JefMod.logger.warning("Repo downloads took too long and timed out.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        itemData.forEach((name,data) -> {
            JsonReader reader = new JsonReader(new StringReader(data));
            reader.setLenient(true);
            try {
                JsonObject object = new JsonParser().parse(reader).getAsJsonObject();

                SkyblockItem item = SkyblockItem.fromJson(object);
                if(item == null){
                    JefMod.logger.info("Corrupted Data for " + name);
                    return;
                }
                skyblockItems.put(item.skyblockID,item);
            } catch (JsonSyntaxException e) {
                JefMod.logger.info("Syntax Error While loading item " + name);
            }
        });
        itemData.clear();
        JefMod.logger.info("Loaded " + skyblockItems.size() + " items from repo.");
    }
    public static void initialise(){
        initialise(false);
    }

    public static void loadJEFItems(){
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new URL(JEF_REPO).openStream()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().contains("/items/") && entry.getName().endsWith(".json")) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    String fileName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1);
                    itemData.put(fileName, out.toString("UTF-8"));
                }
            }
            JefMod.logger.info("JEF Repo fetched! Total items currently: " + itemData.size());
        }catch (JsonSyntaxException e) {
            JefMod.logger.info("Skipping malformed JSON file" );
        } catch (Exception e) {
            JefMod.logger.info("Error while loading JEF Repo: " + e.getMessage());
            JefMod.logger.info( Arrays.toString(e.getStackTrace()));
        }
    }

    public static void loadNEUItems(){
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new URL(NEU_REPO).openStream()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().contains("/items/") && entry.getName().endsWith(".json")) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    String fileName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1);
                    if (!itemData.containsKey(fileName)) {
                        itemData.put(fileName, out.toString("UTF-8"));
                    }
                }
            }
            JefMod.logger.info("NEU Repo fetched! Total items currently: " + itemData.size());
        }catch (JsonSyntaxException e) {
            JefMod.logger.info("Skipping malformed JSON file" );
        } catch (Exception e) {
            JefMod.logger.info("Error while loading NEU Repo: " + e.getMessage());
            JefMod.logger.info( Arrays.toString(e.getStackTrace()));
        }
    }

}
