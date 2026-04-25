package com.jef.justenoughfakepixel.features.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.features.profile.data.*;

import java.util.*;

import com.jef.justenoughfakepixel.features.profile.data.base.BaseData;
import com.jef.justenoughfakepixel.features.profile.data.base.NetworthData;
import com.jef.justenoughfakepixel.features.profile.data.base.Statistics;
import com.jef.justenoughfakepixel.features.profile.data.dungeon.DungeonData;
import com.jef.justenoughfakepixel.features.profile.data.dungeon.Floor;
import com.jef.justenoughfakepixel.features.profile.data.dungeon.FloorData;
import com.jef.justenoughfakepixel.features.profile.data.skills.Skill;
import com.jef.justenoughfakepixel.features.profile.data.skills.SkillData;
import com.jef.justenoughfakepixel.features.profile.vars.EquipmentSlot;
import com.jef.justenoughfakepixel.features.profile.vars.ProfileMode;
import com.jef.justenoughfakepixel.utils.ColorUtils;
import com.jef.justenoughfakepixel.utils.RomanNumeralParser;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ProfileParser {

    public static HashMap<String, ProfileData> profileData = new HashMap<>();
    public static String lastCachedProfile = "";
    public static boolean parsing = false;
    public static int windowID = -1;

    public static void parse(String player, Container container) {
        BaseData base = parseBasicInfo(container);
        if (base == null) return;
        base.playerName = base.playerName + "-" + lastCachedProfile;
        parsing = true;

        Minecraft mc = Minecraft.getMinecraft();

        JefMod.logger.info("[ProfileParser] Pending inventory for: " + base.playerName);
        windowID = container.windowId;
        mc.playerController.windowClick(
                windowID, 19, 0, 0, Minecraft.getMinecraft().thePlayer
        );

        // INVENTORY
        GuiWaiter.waitFor("View Inventory", 2, 8,invChest -> {
            InventoryData invData = parseInvData(invChest);
            if (invData == null) {
                JefMod.logger.info("[ProfileParser] InventoryData was null for: " + base.playerName);
                parsing = false;
                return;
            }
            JefMod.logger.info("[ProfileParser] InventoryData parsed for: " + base.playerName);

            GuiWaiter.waitFor("View Profile",2,-1,prof1 -> {
                windowID = prof1.windowId;

                mc.playerController.windowClick(windowID, 21, 0, 0, mc.thePlayer);
                GuiWaiter.waitFor("View Skills", 2,49, skillChest -> {
                    EnumMap<Skill, SkillData> skillData = parseSkills(skillChest);
                    if (skillData.isEmpty()) {
                        JefMod.logger.info("[ProfileParser] SkillData was null for: " + base.playerName);
                        parsing = false;
                        return;
                    }
                    JefMod.logger.info("[ProfileParser] SkillData parsed for: " + base.playerName);

                    GuiWaiter.waitFor("View Profile",2,-1,prof2 ->  {
                        windowID = prof2.windowId;
                        mc.playerController.windowClick(windowID,41,0,0,mc.thePlayer);
                        GuiWaiter.waitFor("View HOTM",2,31,hotm -> {
                            HOTMData hotmData = parseHOTM(hotm);
                            if(hotmData == null){
                                JefMod.logger.info("[ProfileParser] HOTMData was null for: " + base.playerName);
                                parsing = false;
                                return;
                            }
                            JefMod.logger.info("[ProfileParser] HOTMData parsed for: " + base.playerName);
                            GuiWaiter.waitFor("View Profile",2,-1,prof3 -> {
                                windowID = prof3.windowId;
                                mc.playerController.windowClick(windowID,43,0,0,mc.thePlayer);
                                GuiWaiter.waitFor("View Dungeon Stats",2,49,dungeon -> {
                                    DungeonData dungeonData = parseDungeon(dungeon);
                                    if(dungeonData == null){
                                        JefMod.logger.info("[ProfileParser] DungeonData was null for: " + base.playerName);
                                        parsing = false;
                                        return;
                                    }
                                    JefMod.logger.info("[ProfileParser] DungeonData parsed for: " + base.playerName);
                                    ProfileData profile = new ProfileData(base, invData, skillData,hotmData,dungeonData);
                                    profileData.put(base.playerName, profile);
                                    writeToJson(profile);
                                    parsing = false;
                                    JefMod.logger.info("[ProfileParser] Saved profile: " + base.playerName);
                                });
                            });
                        });

                    });
                });
            });
        });
    }

    public static DungeonData parseDungeon(ContainerChest container){
        if (container == null) return null;
        String title = ColorUtils.stripColor(
                container.getLowerChestInventory().getDisplayName().getUnformattedText()
        ).trim();
        if (!title.equals("View Dungeon Stats")) return null;

        ItemStack skill = container.getSlot(4).getStack();
        if(skill == null) return null;
        String name = ColorUtils.stripColor(skill.getDisplayName()).trim();
        String[] words = name.split(" ");
        int level,hLevel = -1,mLevel = -1,aLevel = -1,bLevel = -1,tLevel = -1;
        long curPorgress = -1,reqProgress = -1;
        level = Integer.parseInt(words[words.length-1]);

        List<String> lore = getLore(skill);
        for(String line : lore){
            if(line.startsWith("Healer")){
                hLevel = RomanNumeralParser.parse(line.split(" ")[1].trim());
            }
            if(line.startsWith("Mage")){
                mLevel = RomanNumeralParser.parse(line.split(" ")[1].trim());
            }
            if(line.startsWith("Archer")){
                aLevel = RomanNumeralParser.parse(line.split(" ")[1].trim());
            }
            if(line.startsWith("Berserk")){
                bLevel = RomanNumeralParser.parse(line.split(" ")[1].trim());
            }
            if(line.startsWith("Tank")){
                tLevel = RomanNumeralParser.parse(line.split(" ")[1].trim());
            }
            if (line.contains("/") && !line.contains(" ")) {
                try {
                    String[] parts = line.split("/");
                    curPorgress = parseRawNumber(parts[0]);
                    reqProgress = parseRawNumber(parts[1]);
                } catch (Exception ignored) {
                }
            }
        }
        if(level < 0 || hLevel < 0 || mLevel < 0 || aLevel < 0 || bLevel < 0 || tLevel < 0)return null;

        EnumMap<Floor, FloorData> floorData = new EnumMap<>(Floor.class);
        int[] floorStats = new int[]{19,20,21,22,23,24,25,31};
        for(int i : floorStats){
            FloorData fData = parseFloor(container.getSlot(i).getStack());
            if(fData == null) continue;
            floorData.put(fData.floor,fData);
        }
        if(floorData.isEmpty()) return null;

        return new DungeonData(level,hLevel,mLevel,aLevel,bLevel,tLevel,curPorgress,reqProgress,floorData);
    }

    public static FloorData parseFloor(ItemStack stack){
        if(stack == null)return null;

        Floor floor = Floor.getFloor(ColorUtils.stripColor(stack.getDisplayName()));
        if(floor == null) return null;

        int bossKills = -1,bestScore = -1,totalEnemiesKilled = -1,mostEnemiesKilled = -1;
        long mHDamage = -1,mMDamage = -1,mADamage = -1,mBDamage = -1,mTDamage = -1,mLDamage = -1;
        long fastestTime = -1,fastestSTime = -1,fastestSPlusTime = -1;

        List<String> lore = getLore(stack);
        try {
            for (String s : lore) {
                if (s.startsWith(floor.bossName + " Kills:")) {
                    bossKills = Integer.parseInt(s.split(":")[1].trim());
                }
                if (s.startsWith("Best Score:")) {
                    bestScore = Integer.parseInt(s.split(":")[1].trim());
                }
                if (s.startsWith("Total Enemies Killed:")) {
                    totalEnemiesKilled = Integer.parseInt(s.split(":")[1].replace(",","").trim());
                }
                if (s.startsWith("Most Enemies Killed:")) {
                    mostEnemiesKilled = Integer.parseInt(s.split(":")[1].replace(",","").trim());
                }
                if (s.startsWith("Fastest Time:")) {
                    fastestTime = parseFinishTimeToSeconds(s.split(":")[1].trim());
                }
                if (s.startsWith("Fastest Time (S):")) {
                    fastestSTime = parseFinishTimeToSeconds(s.split(":")[1].trim());
                }
                if (s.startsWith("Fastest Time (S+):")) {
                    fastestSPlusTime = parseFinishTimeToSeconds(s.split(":")[1].trim());
                }
                if (s.startsWith("Most")) {
                    String amount = s.split(":")[1].trim();
                    if (s.contains("Healer")) {
                        if (!amount.equals("N/A")) {
                            mHDamage = parseRawNumber(amount);
                        }
                    }
                    if (s.contains("Mage")) {
                        if (!amount.equals("N/A")) {
                            mMDamage = parseRawNumber(amount);
                        }
                    }
                    if (s.contains("Archer")) {
                        if (!amount.equals("N/A")) {
                            mADamage = parseRawNumber(amount);
                        }
                    }
                    if (s.contains("Berserk")) {
                        if (!amount.equals("N/A")) {
                            mBDamage = parseRawNumber(amount);
                        }
                    }
                    if (s.contains("Tank")) {
                        if (!amount.equals("N/A")) {
                            mTDamage = parseRawNumber(amount);
                        }
                    }
                    if (s.contains("Ally")) {
                        if (!amount.equals("N/A")) {
                            mLDamage = parseRawNumber(amount);
                        }
                    }
                }
            }
        }catch (NumberFormatException ignored){}
        if(bossKills < 0) return null;
        return new FloorData(floor,bossKills,fastestTime,fastestSTime,fastestSPlusTime,bestScore,mHDamage,mMDamage,mADamage,mBDamage,mTDamage,mLDamage,totalEnemiesKilled,mostEnemiesKilled);
    }
    public static HOTMData parseHOTM(ContainerChest container) {
        if (container == null) return null;
        String title = ColorUtils.stripColor(
                container.getLowerChestInventory().getDisplayName().getUnformattedText()
        ).trim();
        if (!title.equals("View HOTM")) return null;

        ItemStack hotm = container.getSlot(11).getStack();
        if(hotm == null) return null;
        List<String> lore = getLore(hotm);
        if(lore.isEmpty()) return null;
        int tier = -1,tokens = -1,mithril = -1,gemstone = -1,commisions = -1;
        long curProgress = -1, reqProgress = -1;
        for(String s : lore){
            if(s.startsWith("Tier:")){
                String[] words = s.split(" ");
                if(words.length < 2)continue;
                tier = Integer.parseInt(words[1].trim().replace(",",""));
            }
            if(s.startsWith("Total Token of the Mountain: ")){
                String[] words = s.split(":");
                if(words.length < 2)continue;
                tokens = Integer.parseInt(words[1].trim().replace(",",""));
            }
            if(s.startsWith("Total Mithril Powder:")){
                String[] words = s.split(":");
                if (words.length < 2)continue;
                mithril = Integer.parseInt(words[1].trim().replace(",",""));
            }
            if(s.startsWith("Total Gemstone Powder:")){
                String[] words = s.split(":");
                if (words.length < 2)continue;
                gemstone = Integer.parseInt(words[1].trim().replace(",",""));
            }
            if(s.startsWith("Commissions Complete:")){
                String[]  words = s.split(":");
                if (words.length < 2)continue;
                commisions = Integer.parseInt(words[1].trim().replace(",",""));
            }
            if (s.contains("/") && !s.contains(" ")) {
                try {
                    String[] parts = s.split("/");
                    JefMod.logger.info("[ProfileParser] Parsing " + parts[0] + " " + parts[1]);
                    curProgress = parseRawNumber(parts[0]);
                    reqProgress = parseRawNumber(parts[1]);
                } catch (Exception ignored) {
                }
            }
        }
        if(tier < 0 || tokens < 0 || mithril < 0 || gemstone < 0 || commisions < 0){
            JefMod.logger.info("Tier: " + tier + " | Tokens: " + tokens + " | Mithril: " + mithril + " | Gemstone: " + gemstone + " | Comms: " + commisions);
            return null;
        }
        return new HOTMData(tier,tokens,mithril,gemstone,commisions,curProgress,reqProgress);
    }

    public static InventoryData parseInvData(ContainerChest container) {
        if (container == null) return null;
        String title = ColorUtils.stripColor(
                container.getLowerChestInventory().getDisplayName().getUnformattedText()
        ).trim();
        if (!title.equals("View Inventory")) return null;

        HashMap<EquipmentSlot, String> armorData = new HashMap<>();
        HashMap<Integer, String>       invData   = new HashMap<>();

        ItemStack helmet     = container.getSlot(2).getStack();
        ItemStack chestplate = container.getSlot(3).getStack();
        ItemStack leggings   = container.getSlot(4).getStack();
        ItemStack boots      = container.getSlot(5).getStack();
        ItemStack necklace   = container.getSlot(11).getStack();
        ItemStack cloak      = container.getSlot(12).getStack();
        ItemStack belt       = container.getSlot(13).getStack();
        ItemStack gloves     = container.getSlot(14).getStack();

        if (helmet     != null) armorData.put(EquipmentSlot.HELMET,     itemToNBT(helmet));
        if (chestplate != null) armorData.put(EquipmentSlot.CHESTPLATE, itemToNBT(chestplate));
        if (leggings   != null) armorData.put(EquipmentSlot.LEGGINGS,   itemToNBT(leggings));
        if (boots      != null) armorData.put(EquipmentSlot.BOOTS,      itemToNBT(boots));

        if (necklace != null && !ColorUtils.stripColor(necklace.getDisplayName()).equals("Necklace"))
            armorData.put(EquipmentSlot.NECKLACE, itemToNBT(necklace));
        if (cloak    != null && !ColorUtils.stripColor(cloak.getDisplayName()).equals("Cloak"))
            armorData.put(EquipmentSlot.CLOAK,    itemToNBT(cloak));
        if (belt     != null && !ColorUtils.stripColor(belt.getDisplayName()).equals("Belt"))
            armorData.put(EquipmentSlot.BELT,     itemToNBT(belt));
        if (gloves   != null && !ColorUtils.stripColor(gloves.getDisplayName()).equals("Gloves"))
            armorData.put(EquipmentSlot.GLOVES,   itemToNBT(gloves));

        for (int i = 0; i < 36; i++) {
            ItemStack stack = container.getSlot(i + 18).getStack();
            if (stack != null) invData.put(i, itemToNBT(stack));
        }

        return new InventoryData(armorData, invData);
    }

    public static String itemToNBT(ItemStack stack) {
        if (stack == null) return "";
        NBTTagCompound compound = new NBTTagCompound();
        stack.writeToNBT(compound);
        return compound.toString();
    }

    public static ItemStack itemFromNBT(String data) {
        try {
            return ItemStack.loadItemStackFromNBT(JsonToNBT.getTagFromJson(data));
        } catch (NBTException e) {
            JefMod.logger.info("Error parsing item from NBT: " + data);
            return null;
        }
    }

    public static void parseName(ItemStack stack) {
        if (stack == null) return;
        for (String line : getLore(stack)) {
            if (line.startsWith("Profile:")) {
                String[] words = line.split(" ");
                if (words.length >= 2) {
                    lastCachedProfile = words[1];
                    JefMod.logger.info("Profile: " + lastCachedProfile);
                }
            }
        }
    }


    public static BaseData parseBasicInfo(Container container) {
        ItemStack basicInfo = container.getSlot(4).getStack();
        if (basicInfo == null) return null;

        List<String> lore = getLore(basicInfo);
        if (lore.isEmpty()) return null;

        String playerName = ColorUtils.stripColor(basicInfo.getDisplayName()).split("'")[0];
        int level = -1;
        long profileAge = 0L;
        ProfileMode mode = ProfileMode.NORMAL;
        long purse = 0L, bank = 0L;
        int bits = 0;
        long totalNetworth = 0L, itemNetworth = 0L, armorNetworth = 0L,
                petNetworth = 0L, accessoriesNetworth = 0L;
        long playtime = 0L;
        int kills = 0, deaths = 0;
        long highCrit = 0L;

        for (String raw : lore) {
            String line = raw.trim();
            try {
                if (line.startsWith("SkyBlock Level:")) {
                    int open = line.indexOf('['), close = line.indexOf(']');
                    if (open != -1 && close != -1)
                        level = Integer.parseInt(line.substring(open + 1, close).trim());
                } else if (line.startsWith("Profile Age:")) {
                    profileAge = parseTimeToSeconds(line.substring("Profile Age:".length()).trim());
                } else if (line.startsWith("Profile Mode:")) {
                    mode = line.substring("Profile Mode:".length()).trim().equalsIgnoreCase("Ironman")
                            ? ProfileMode.IRONMAN : ProfileMode.NORMAL;
                } else if (line.startsWith("Purse:")) {
                    purse = parseCoins(line.substring("Purse:".length()));
                } else if (line.startsWith("Bank:")) {
                    String bankPart = line.substring("Bank:".length()).trim();
                    int slash = bankPart.indexOf('/');
                    bank = parseCoins(slash != -1 ? bankPart.substring(0, slash) : bankPart);
                } else if (line.startsWith("Bits:")) {
                    bits = (int) parseLongNumber(line.substring("Bits:".length()).replace("Bits", ""));
                } else if (line.startsWith("Estimate Networth:")) {
                    totalNetworth = parseCoins(line.substring("Estimate Networth:".length()));
                } else if (line.startsWith("Items:")) {
                    itemNetworth = parseCoins(line.substring("Items:".length()));
                } else if (line.startsWith("Armor:")) {
                    armorNetworth = parseCoins(line.substring("Armor:".length()));
                } else if (line.startsWith("Pets:")) {
                    petNetworth = parseCoins(line.substring("Pets:".length()));
                } else if (line.startsWith("Accessories:")) {
                    accessoriesNetworth = parseCoins(line.substring("Accessories:".length()));
                } else if (line.startsWith("Playtime:")) {
                    playtime = parseTimeToSeconds(line.substring("Playtime:".length()).trim());
                } else if (line.startsWith("Kills:")) {
                    kills = (int) parseLongNumber(line.substring("Kills:".length()));
                } else if (line.startsWith("Deaths:")) {
                    deaths = (int) parseLongNumber(line.substring("Deaths:".length()));
                } else if (line.startsWith("Highest Critical Damage:")) {
                    highCrit = parseLongNumber(line.substring("Highest Critical Damage:".length()));
                }
            } catch (Exception ignored) {}
        }

        if (level == -1) return null;

        return new BaseData(
                playerName, level, profileAge, mode,
                (int) purse, (int) bank, bits,
                new NetworthData((int) totalNetworth, (int) itemNetworth,
                        (int) armorNetworth, (int) petNetworth, (int) accessoriesNetworth),
                new Statistics(playtime, kills, deaths, (int) highCrit)
        );
    }

    private static List<String> getLore(ItemStack stack) {
        List<String> lore = new ArrayList<>();
        if (stack == null || !stack.hasTagCompound()) return lore;
        NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
        if (!display.hasKey("Lore", 9)) return lore;
        NBTTagList loreList = display.getTagList("Lore", 8);
        for (int i = 0; i < loreList.tagCount(); i++)
            lore.add(ColorUtils.stripColor(loreList.getStringTagAt(i)).trim());
        return lore;
    }

    private static long parseCoins(String raw) {
        String cleaned = raw.trim().replace("Coins", "").replace(",", "").trim();
        int dot = cleaned.indexOf('.');
        if (dot != -1) cleaned = cleaned.substring(0, dot);
        return cleaned.isEmpty() ? 0L : Long.parseLong(cleaned);
    }

    private static long parseLongNumber(String raw) {
        String cleaned = raw.trim().replace(",", "").trim();
        int dot = cleaned.indexOf('.');
        if (dot != -1) cleaned = cleaned.substring(0, dot);
        cleaned = cleaned.split("\\s+")[0];
        return cleaned.isEmpty() ? 0L : Long.parseLong(cleaned);
    }

    private static long parseTimeToSeconds(String raw) {
        long total = 0L;
        String[][] units = {
                {"year",   String.valueOf(365L * 24 * 60 * 60)},
                {"day",    String.valueOf(24L  * 60 * 60)},
                {"hour",   String.valueOf(60L  * 60)},
                {"minute", String.valueOf(60L)},
                {"second", String.valueOf(1L)},
        };
        String text = raw.toLowerCase().replace(",", "").trim();
        for (String[] unit : units) {
            int idx = text.indexOf(unit[0]);
            if (idx == -1) continue;
            String[] tokens = text.substring(0, idx).trim().split("\\s+");
            if (tokens.length == 0) continue;
            try { total += Long.parseLong(tokens[tokens.length - 1]) * Long.parseLong(unit[1]); }
            catch (NumberFormatException ignored) {}
        }
        return total;
    }

    private static long parseFinishTimeToSeconds(String raw){
        long total = 0L;
        String[][] units = {
                {"y",   String.valueOf(365L * 24 * 60 * 60)},
                {"d",    String.valueOf(24L  * 60 * 60)},
                {"h",   String.valueOf(60L  * 60)},
                {"m", String.valueOf(60L)},
                {"s", String.valueOf(1L)},
        };
        String text = raw.toLowerCase().replace(",","").trim();
        for(String[] unit : units){
            int idx = text.indexOf(unit[0]);
            if (idx == -1) continue;
            String[] tokens = text.substring(0,idx).trim().split("\\s+");
            if(tokens.length == 0) continue;
            try { total += Long.parseLong(tokens[tokens.length - 1]) * Long.parseLong(unit[1]); }
            catch (NumberFormatException ignored) {}
        }
        return total;
    }



    public static EnumMap<Skill, SkillData> parseSkills(ContainerChest container) {
        EnumMap<Skill, SkillData> result = new EnumMap<>(Skill.class);
        if (container == null) return result;

        String title = ColorUtils.stripColor(
                container.getLowerChestInventory().getDisplayName().getUnformattedText()
        ).trim();
        if (!title.equals("View Skills")) return result;

        int[] slots = {19,20,21,22,23,24,25,30,31,32};

        for (int slot : slots) {
            ItemStack stack = container.getSlot(slot).getStack();
            if (stack == null) continue;

            Skill skill = Skill.get(ColorUtils.stripColor(stack.getDisplayName().split(" ")[0]));
            if (skill == null) continue;
            List<String> lore = getLore(stack);
            if (lore.isEmpty()) continue;

            int currentLevel = -1;
            long currentXp = 0L;
            long requiredXp = -1L;

            for (String line : lore) {
                if (line.startsWith("Progress to Level")) {
                    try {
                        String afterLevel = line.substring(line.indexOf("Level ") + 6);
                        String levelStr = afterLevel.substring(0, afterLevel.indexOf(':')).trim();
                        int nextLevel = Integer.parseInt(levelStr);
                        currentLevel = nextLevel - 1;
                    } catch (Exception ignored) {
                    }
                }

                if (line.contains("/") && !line.contains(" ")) {
                    try {
                        String[] parts = line.split("/");
                        currentXp = parseRawNumber(parts[0]);
                        requiredXp = parseRawNumber(parts[1]);
                    } catch (Exception ignored) {
                    }
                }
            }

            if (currentLevel == -1) {
                for (String line : lore) {
                    if (line.startsWith("Level ")) {
                        try {
                            currentLevel = Integer.parseInt(line.substring(6).trim());
                        } catch (Exception ignored) {
                        }
                        break;
                    }
                }
            }

            if (currentLevel < 0) continue;

            result.put(skill, new SkillData(skill, currentLevel, currentXp, requiredXp));
        }

        return result;
    }

    private static long parseRawNumber(String raw) {
        String s = raw.trim().replace(",", "");
        if (s.isEmpty()) return 0L;
        char suffix = Character.toUpperCase(s.charAt(s.length() - 1));
        long multiplier = 1L;
        if (suffix == 'K') { multiplier = 1_000L;         s = s.substring(0, s.length() - 1); }
        else if (suffix == 'M') { multiplier = 1_000_000L;     s = s.substring(0, s.length() - 1); }
        else if (suffix == 'B') { multiplier = 1_000_000_000L; s = s.substring(0, s.length() - 1); }
        return (long) (Double.parseDouble(s) * multiplier);
    }

    public static void writeToJson(ProfileData data) {
        if (data == null) return;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(JefConfig.configDirectory, "profile.json");
        if (!file.exists()) {
            try { file.createNewFile(); }
            catch (IOException e) { JefMod.logger.info("Error creating profile.json"); return; }
        }
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
            writer.flush();
        } catch (IOException e) {
            JefMod.logger.info("Error writing to profile.json");
        }
    }
}