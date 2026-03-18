package com.jef.justenoughfakepixel.features.general;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.utils.ColorUtils;
import com.jef.justenoughfakepixel.utils.RomanNumeralParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnchantProcessor {

    private static final Pattern ENCHANT_PATTERN =
            Pattern.compile("(?<name>[A-Za-z][A-Za-z -]+) (?<level>[IVXLCDM]+|\\d+)(?=, |$| [\\d,]+$)");
    private static final String COMMA = ", ";

    private static final Map<String, EnchantMeta> ENCHANTS_BY_LORE = new HashMap<>();
    private static final Map<String, EnchantMeta> ENCHANTS_BY_NBT = new HashMap<>();
    private static boolean loaded;

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onTooltip(ItemTooltipEvent event) {
        if (event == null || event.itemStack == null || event.toolTip == null || JefConfig.feature == null) return;
        if (!JefConfig.feature.general.enchantHighlight) return;

        ensureLoaded();
        if (ENCHANTS_BY_LORE.isEmpty()) return;

        Set<String> presentEnchantIds = getPresentEnchantIds(event.itemStack);
        int[] range = findEnchantRange(event.toolTip);
        if (range[0] < 0 || range[1] < range[0]) return;

        List<FormattedEnchant> parsed = parseEnchants(event.toolTip, range[0], range[1], presentEnchantIds);
        if (parsed.isEmpty()) return;

        parsed.sort(Comparator
                .comparingInt((FormattedEnchant e) -> e.meta.sortType)
                .thenComparing(e -> e.meta.loreName));

        List<String> rebuilt = buildLayout(parsed);
        if (rebuilt.isEmpty()) return;

        event.toolTip.subList(range[0], range[1] + 1).clear();
        event.toolTip.addAll(range[0], rebuilt);
    }

    private void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        loadSbaEnchantMeta();
    }

    private void loadSbaEnchantMeta() {
        try (Scanner scanner = new Scanner(Objects.requireNonNull(
                EnchantProcessor.class.getResourceAsStream("/assets/justenoughfakepixel/enchants/enchants.json")
        ), "UTF-8")) {
            String json = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            JsonObject root = new JsonParser().parse(json).getAsJsonObject();
            loadSection(root, "NORMAL", 2);
            loadSection(root, "ULTIMATE", 0);
            loadSection(root, "STACKING", 1);
        } catch (Exception ignored) {
        }
    }

    private void loadSection(JsonObject root, String section, int sortType) {
        if (!root.has(section)) return;
        JsonObject bucket = root.getAsJsonObject(section);
        for (Map.Entry<String, JsonElement> entry : bucket.entrySet()) {
            try {
                JsonObject obj = entry.getValue().getAsJsonObject();
                EnchantMeta meta = new EnchantMeta(
                        obj.get("loreName").getAsString(),
                        obj.get("nbtName").getAsString().toLowerCase(Locale.US),
                        obj.get("goodLevel").getAsInt(),
                        obj.get("maxLevel").getAsInt(),
                        sortType
                );
                ENCHANTS_BY_LORE.put(meta.loreName.toLowerCase(Locale.US), meta);
                ENCHANTS_BY_NBT.put(meta.nbtName, meta);
            } catch (Exception ignored) {
            }
        }
    }

    private int[] findEnchantRange(List<String> tooltip) {
        int start = -1, end = -1;
        for (int i = 1; i < tooltip.size(); i++) {
            String clean = ColorUtils.stripColor(tooltip.get(i)).trim();
            boolean lineHasEnchant = ENCHANT_PATTERN.matcher(clean).find();
            if (start == -1) {
                if (lineHasEnchant) start = i;
            } else {
                if (!lineHasEnchant && clean.isEmpty()) {
                    end = i - 1;
                    break;
                }
                if (lineHasEnchant) end = i;
            }
        }
        if (start != -1 && end == -1) end = start;
        return new int[]{start, end};
    }

    private List<FormattedEnchant> parseEnchants(List<String> tooltip, int start, int end, Set<String> presentIds) {
        List<FormattedEnchant> out = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            String clean = ColorUtils.stripColor(tooltip.get(i));
            Matcher m = ENCHANT_PATTERN.matcher(clean);
            while (m.find()) {
                String loreName = m.group("name").trim();
                int level = parseLevel(m.group("level"));
                EnchantMeta meta = ENCHANTS_BY_LORE.get(loreName.toLowerCase(Locale.US));
                if (meta == null) {
                    String rawId = loreName.toLowerCase(Locale.US).replace(' ', '_');
                    meta = ENCHANTS_BY_NBT.get(rawId);
                }
                if (meta == null) continue;
                if (!presentIds.isEmpty() && !presentIds.contains(meta.nbtName)) continue;
                out.add(new FormattedEnchant(meta, level));
            }
        }
        return out;
    }

    private int parseLevel(String raw) {
        if (raw.chars().allMatch(Character::isDigit)) return Integer.parseInt(raw);
        try {
            return RomanNumeralParser.parse(raw);
        } catch (Exception ignored) {
            return 1;
        }
    }

    private Set<String> getPresentEnchantIds(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return Collections.emptySet();
        NBTTagCompound extra = stack.getTagCompound().getCompoundTag("ExtraAttributes");
        if (extra == null || !extra.hasKey("enchantments", 10)) return Collections.emptySet();
        Set<String> out = new HashSet<>();
        for (String key : extra.getCompoundTag("enchantments").getKeySet()) {
            out.add(key.toLowerCase(Locale.US));
        }
        return out;
    }

    private List<String> buildLayout(List<FormattedEnchant> entries) {
        int layout = JefConfig.feature.general.enchantLayout;
        if (layout == 2) {
            List<String> lines = new ArrayList<>(entries.size());
            for (FormattedEnchant entry : entries) lines.add(entry.formatted());
            return lines;
        }

        if (layout == 1 && entries.size() > 1) {
            FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
            int maxWidth = 180;
            List<String> lines = new ArrayList<>();
            StringBuilder line = new StringBuilder();
            for (FormattedEnchant entry : entries) {
                String next = entry.formatted();
                String candidate = line.length() == 0 ? next : line + grayComma() + next;
                if (line.length() > 0 && fr.getStringWidth(candidate) > maxWidth) {
                    lines.add(line.toString());
                    line = new StringBuilder(next);
                } else {
                    line = new StringBuilder(candidate);
                }
            }
            if (line.length() > 0) lines.add(line.toString());
            return lines;
        }

        List<String> lines = new ArrayList<>((entries.size() + 1) / 2);
        for (int i = 0; i < entries.size(); i += 2) {
            if (i + 1 < entries.size()) lines.add(entries.get(i).formatted() + grayComma() + entries.get(i + 1).formatted());
            else lines.add(entries.get(i).formatted());
        }
        return lines;
    }

    private String grayComma() {
        return "§7" + COMMA;
    }

    private String formatColor(EnchantMeta meta, int level) {
        String color;
        if (meta.sortType == 0) color = JefConfig.feature.general.enchantUltimateColor;
        else if (level >= meta.maxLevel) color = JefConfig.feature.general.enchantPerfectColor;
        else if (level > meta.goodLevel) color = JefConfig.feature.general.enchantGreatColor;
        else if (level == meta.goodLevel) color = JefConfig.feature.general.enchantGoodColor;
        else color = JefConfig.feature.general.enchantPoorColor;

        int argb = ChromaColour.specialToSimpleRGB(color);
        String prefix = nearestMcColor(argb);
        if (JefConfig.feature.general.enchantChroma && ChromaColour.getSpeed(color) > 0) prefix += "§z";
        if (meta.sortType == 0) prefix += "§l";
        return prefix;
    }

    private String nearestMcColor(int argb) {
        int r = (argb >> 16) & 255;
        int g = (argb >> 8) & 255;
        int b = argb & 255;
        String[] codes = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        int[] values = {0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
                0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF};
        int best = 0;
        long bestDist = Long.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            int cr = (values[i] >> 16) & 255;
            int cg = (values[i] >> 8) & 255;
            int cb = values[i] & 255;
            long dist = sq(r - cr) + sq(g - cg) + sq(b - cb);
            if (dist < bestDist) {
                bestDist = dist;
                best = i;
            }
        }
        return "§" + codes[best];
    }

    private long sq(long x) {
        return x * x;
    }

    private String toRoman(int number) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] numerals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder out = new StringBuilder();
        int n = Math.max(1, number);
        for (int i = 0; i < values.length; i++) {
            while (n >= values[i]) {
                n -= values[i];
                out.append(numerals[i]);
            }
        }
        return out.toString();
    }

    private class FormattedEnchant {
        private final EnchantMeta meta;
        private final int level;

        private FormattedEnchant(EnchantMeta meta, int level) {
            this.meta = meta;
            this.level = Math.max(1, level);
        }

        private String formatted() {
            String lvl = JefConfig.feature.general.romanNumerals ? Integer.toString(level) : toRoman(level);
            return formatColor(meta, level) + meta.loreName + " " + lvl;
        }
    }

    private static class EnchantMeta {
        private final String loreName;
        private final String nbtName;
        private final int goodLevel;
        private final int maxLevel;
        private final int sortType;

        private EnchantMeta(String loreName, String nbtName, int goodLevel, int maxLevel, int sortType) {
            this.loreName = loreName;
            this.nbtName = nbtName;
            this.goodLevel = goodLevel;
            this.maxLevel = maxLevel;
            this.sortType = sortType;
        }
    }
}
