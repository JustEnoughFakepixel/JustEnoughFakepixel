package com.jef.justenoughfakepixel.features.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.core.JefConfig;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;

public class StorageSaving {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static HashMap<String,SContainer> loadStorageData(){
        HashMap<String,SContainer> map = new HashMap<>();
        File folder = new File(JefConfig.configDirectory,"storage");
        if(!folder.exists()){
            folder.mkdirs();
            return new HashMap<>();
        }
        for(File file : folder.listFiles()) {
            try {
                if(!file.exists()){
                    file.createNewFile();
                    continue;
                }
                SContainer container = gson.fromJson(new FileReader(file), SContainer.class);
                if(container != null){
                    map.put(container.id,container);
                }
            } catch (IOException e) {
                JefMod.logger.info("Error while trying to load " + file.getName() + " ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return map;
    }
    public static void saveStorageData(Collection<SContainer> containers){
        File folder = new File(JefConfig.configDirectory,"storage");
        if(!folder.exists()){
            folder.mkdirs();
        }
        for (SContainer container : containers) {
            File file = new File(folder,container.id + ".json");
            try{
                if(!file.exists()){
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
