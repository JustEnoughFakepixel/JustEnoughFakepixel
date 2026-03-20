package com.jef.justenoughfakepixel.features.invbuttons;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SkyblockItemCache {

    private static final SkyblockItemCache INSTANCE = new SkyblockItemCache();
    public static SkyblockItemCache getInstance() { return INSTANCE; }

    private static final String API_URL = "https://api.hypixel.net/v2/resources/skyblock/items";

    private final Map<String, String> skullItems = new LinkedHashMap<>();
    private final List<String> allItemIds = new ArrayList<>();
    private volatile boolean loaded = false;

    private final ExecutorService loader = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "JEF-ItemCache"); t.setDaemon(true); return t;
    });

    private SkyblockItemCache() {}

    public void loadAsync() {
        if (loaded) return;
        loader.submit(this::loadSync);
    }

    private void loadSync() {
        try {
            System.out.println("[JEF] Connecting to Hypixel API...");
            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("User-Agent", "JEF/1.0");

            int code = conn.getResponseCode();
            System.out.println("[JEF] Response code: " + code);
            if (code != 200) {
                loaded = true;
                return;
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }

            System.out.println("[JEF] Response length: " + sb.length());
            System.out.println("[JEF] First 200 chars: " +
                    sb.substring(0, Math.min(200, sb.length())));

            JsonObject root = new JsonParser().parse(sb.toString()).getAsJsonObject();
            System.out.println("[JEF] Has 'items': " + root.has("items"));

            JsonArray items = root.getAsJsonArray("items");

            int skulls = 0;
            for (JsonElement el : items) {
                if (!el.isJsonObject()) continue;

                JsonObject obj = el.getAsJsonObject();
                if (!obj.has("id")) continue;

                String id = obj.get("id").getAsString();

                synchronized (allItemIds) {
                    allItemIds.add(id);
                }

                if (obj.has("skin") && obj.get("skin").isJsonObject()) {
                    JsonObject skinObj = obj.getAsJsonObject("skin");
                    if (skinObj.has("value")) {
                        String hash = hashFromB64(skinObj.get("value").getAsString());
                        if (hash != null) {
                            synchronized (skullItems) {
                                skullItems.put(id, hash);
                            }
                            skulls++;
                        }
                    }
                }
            }

            synchronized (allItemIds) {
                Collections.sort(allItemIds);
            }

            loaded = true;
            System.out.println("[JEF] Loaded " + allItemIds.size() + " items, " + skulls + " skulls.");

        } catch (Exception e) {
            System.err.println("[JEF] Item cache failed!");
            e.printStackTrace(); // <-- important: full stack trace
            loaded = true;
        }
    }

    private String hashFromB64(String b64) {
        try {
            String decoded = new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);
            int i = decoded.indexOf("/texture/");
            if (i == -1) return null;
            i += "/texture/".length();
            int end = decoded.indexOf("\"", i);
            if (end == -1) end = decoded.length();
            String h = decoded.substring(i, end).trim();
            return h.isEmpty() ? null : h;
        } catch (Exception e) { return null; }
    }

    public boolean isLoaded() { return loaded; }

    public List<String> getAllItemIds() {
        synchronized (allItemIds) { return new ArrayList<>(allItemIds); }
    }

    public Map<String, String> getSkullItems() {
        synchronized (skullItems) { return new LinkedHashMap<>(skullItems); }
    }
}
