package com.jef.justenoughfakepixel.features.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.core.JefConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WaiterLogs {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static ArrayList<String> logs = new ArrayList<>();

    public static void addLog(String log){
        logs.add(log);
    }

    public static void saveLogs(){
        File file = new File(JefConfig.configDirectory,"guiWaiterLogs.log");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file,true);
            writer.write(gson.toJson(logs));
            writer.close();
        } catch (IOException e) {
            JefMod.logger.info("Error Saving GUI Waiter Logs.");
        }
    }

}
