package com.jef.justenoughfakepixel.features.dungeons.overlays;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.ColorUtils;
import com.jef.justenoughfakepixel.utils.ItemUtils;
import com.jef.justenoughfakepixel.utils.overlay.Overlay;
import com.jef.justenoughfakepixel.utils.data.SkyblockData;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RegisterEvents
public class DungeonBreakerOverlay extends Overlay {

    private static final String ITEM_ID = "DUNGEONBREAKER";

    private static final Pattern CHARGES = Pattern.compile("Charges:\\s*(\\d+)/(\\d+)");

    private static final String C_LABEL  = EnumChatFormatting.RED.toString();
    private static final String C_FULL   = EnumChatFormatting.GREEN.toString();
    private static final String C_SPENT  = EnumChatFormatting.RED.toString();
    private static final String C_VAL    = EnumChatFormatting.GREEN.toString();
    private static final String C_SEP    = EnumChatFormatting.GRAY.toString();

    private static DungeonBreakerOverlay instance;

    public DungeonBreakerOverlay() {
        super(90, 20);
        instance = this;
    }

    public static DungeonBreakerOverlay getInstance() { return instance; }

    @Override public Position getPosition()     { return JefConfig.feature.dungeons.dungeonBreakerPos; }
    @Override public float    getScale()        { return JefConfig.feature.dungeons.dungeonBreakerScale; }
    @Override public int      getBgColor()      { return ChromaColour.specialToChromaRGB(JefConfig.feature.dungeons.dungeonBreakerBgColor); }
    @Override public int      getCornerRadius() { return JefConfig.feature.dungeons.dungeonBreakerCornerRadius; }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature.dungeons.dungeonBreakerOverlay;
    }

    @Override
    protected boolean extraGuard() {
        return SkyblockData.getCurrentLocation() == SkyblockData.Location.DUNGEON;
    }

    @Override
    public List<String> getLines(boolean preview) {
        List<String> out = new ArrayList<>();

        if (preview) {
            out.add(C_LABEL + "Dungeon Breaker");
            out.add(C_LABEL + "Charges: " + C_FULL + "20" + C_SEP + "/" + C_VAL + "20");
            return out;
        }

        ItemStack breaker = findBreakerInHotbar();
        if (breaker == null) return out;

        int[] charges = parseCharges(breaker);
        if (charges == null) return out;

        int current = charges[0];
        int max     = charges[1];
        String chargeColor = current == max ? C_FULL : current == 0 ? C_SPENT : C_VAL;

        out.add(C_LABEL + "Dungeonbreaker");
        out.add(C_SEP + "Charges: " + chargeColor + current + C_SEP + "/" + C_VAL + max);
        return out;
    }

    private static ItemStack findBreakerInHotbar() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return null;
        ItemStack[] hotbar = mc.thePlayer.inventory.mainInventory;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = hotbar[i];
            if (stack != null && ITEM_ID.equals(ItemUtils.getInternalName(stack)))
                return stack;
        }
        return null;
    }

    private static int[] parseCharges(ItemStack item) {
        for (String line : ItemUtils.getLoreLines(item)) {
            Matcher m = CHARGES.matcher(ColorUtils.stripColor(line));
            if (!m.find()) continue;
            try {
                return new int[]{ Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)) };
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}