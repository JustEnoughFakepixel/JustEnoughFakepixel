package com.jef.justenoughfakepixel.features.itemlist;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class SkyblockItem {

    public String itemID,displayName,skyblockID;
    public List<String> infoLinks;
    public List<String> lore;
    public String NBT;
    public int damage;

    public SkyblockItem(String itemID,String name,String sbID,int dmg,
                        List<String> lore,List<String> infoLinks,String NBT){
        this.itemID = itemID;
        this.displayName = name;
        this.skyblockID = sbID;
        this.damage = dmg;
        this.infoLinks = infoLinks;
        this.lore = lore;
        this.NBT = NBT;
    }

    public static SkyblockItem fromJson(JsonObject jsonObject) {

        if (!jsonObject.has("displayname") || !jsonObject.has("itemid") ||
                !jsonObject.has("internalname")) {
            return null;
        }

        String itemID = jsonObject.get("itemid").getAsString();
        String displayName = jsonObject.get("displayname").getAsString();
        String sbID = jsonObject.get("internalname").getAsString();

        List<String> info = new ArrayList<>();
        if (jsonObject.has("info") && jsonObject.get("info").isJsonArray()) {
            for (JsonElement link : jsonObject.get("info").getAsJsonArray()) {
                info.add(link.getAsString());
            }
        }
        List<String> lore = new ArrayList<>();
        if (jsonObject.has("lore") && jsonObject.get("lore").isJsonArray()) {
            for (JsonElement line : jsonObject.get("lore").getAsJsonArray()) {
                lore.add(line.getAsString());
            }
        }
        int damage = jsonObject.has("damage") ? jsonObject.get("damage").getAsInt() : 0;
        String nbt = jsonObject.has("nbttag") ? jsonObject.get("nbttag").getAsString() : "";
        //TODO: Add Recipe Support
        return new SkyblockItem(itemID,displayName,sbID,damage,lore,info,nbt);
    }

}
