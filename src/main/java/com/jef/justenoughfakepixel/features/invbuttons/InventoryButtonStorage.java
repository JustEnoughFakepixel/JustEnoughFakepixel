package com.jef.justenoughfakepixel.features.invbuttons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class InventoryButtonStorage {

    private static final InventoryButtonStorage INSTANCE = new InventoryButtonStorage();
    public static InventoryButtonStorage getInstance() { return INSTANCE; }

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
    private static final Type TYPE = new TypeToken<List<InventoryButton>>(){}.getType();

    private File storageFile;
    private List<InventoryButton> buttons = new ArrayList<>();

    private InventoryButtonStorage() {}

    public void initFile(File configDir) {
        this.storageFile = new File(configDir, "invbuttons.json");
    }

    public void load() {
        if (storageFile == null || !storageFile.exists()) {
            buttons = createDefaults();
            save();
            return;
        }
        try (BufferedReader r = new BufferedReader(new InputStreamReader(
                Files.newInputStream(storageFile.toPath()), StandardCharsets.UTF_8))) {
            List<InventoryButton> loaded = GSON.fromJson(r, TYPE);
            buttons = loaded != null ? loaded : createDefaults();
        } catch (Exception e) {
            e.printStackTrace();
            buttons = createDefaults();
        }
    }

    public void save() {
        if (storageFile == null) return;
        try {
            if (!storageFile.exists()) storageFile.createNewFile();
            try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                    Files.newOutputStream(storageFile.toPath()), StandardCharsets.UTF_8))) {
                w.write(GSON.toJson(buttons));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<InventoryButton> getButtons() { return buttons; }
    public void setButtons(List<InventoryButton> b) { this.buttons = b; save(); }

    private static List<InventoryButton> createDefaults() {
        List<InventoryButton> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) list.add(new InventoryButton(87 + 21 * i, 63, true, false, false, 0, "", ""));
        for (int i = 0; i < 4; i++) list.add(new InventoryButton(87 + 21 * i, 5,  true, false, false, 0, "", ""));
        list.add(new InventoryButton(87,      25,      true, false, false, 0, "", ""));
        list.add(new InventoryButton(87 + 18, 25,      true, false, false, 0, "", ""));
        list.add(new InventoryButton(87,      25 + 18, true, false, false, 0, "", ""));
        list.add(new InventoryButton(87 + 18, 25 + 18, true, false, false, 0, "", ""));
        list.add(new InventoryButton(143, 35, true, false, false, 0, "", ""));
        list.add(new InventoryButton(60, 8,  true, false, false, 0, "", ""));
        list.add(new InventoryButton(60, 60, true, false, false, 0, "", ""));
        list.add(new InventoryButton(26, 8,  true, false, false, 0, "", ""));
        list.add(new InventoryButton(26, 60, true, false, false, 0, "", ""));
        return list;
    }
}
