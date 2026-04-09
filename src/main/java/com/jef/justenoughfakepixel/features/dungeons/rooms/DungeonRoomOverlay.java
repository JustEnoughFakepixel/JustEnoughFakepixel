package com.jef.justenoughfakepixel.features.dungeons.rooms;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.features.dungeons.DungeonStats;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.data.SkyblockData;
import com.jef.justenoughfakepixel.utils.overlay.Overlay;
import lombok.Getter;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class DungeonRoomOverlay extends Overlay {

    public static String currentRoomName = null;
    public static String currentRoomCategory = null;
    public static String currentRoomNotes = null;
    public static boolean currentRoomHasFairySoul = false;
    @Getter
    private static DungeonRoomOverlay instance;

    public DungeonRoomOverlay() {
        super(130, 20);
        instance = this;
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.dungeons.dungeonRoomOverlayPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.dungeons.dungeonRoomOverlayScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.dungeons.dungeonRoomOverlayBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.dungeons.dungeonRoomOverlayCornerRadius;
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature.dungeons.dungeonRoomOverlay;
    }

    @Override
    protected boolean extraGuard() {
        return SkyblockData.getCurrentLocation() == SkyblockData.Location.DUNGEON && !DungeonStats.isInBossFight();
    }

    @Override
    public List<String> getLines(boolean preview) {
        List<String> out = new ArrayList<>();

        if (preview) {
            out.add(EnumChatFormatting.GRAY + "Category  " + EnumChatFormatting.LIGHT_PURPLE + "Puzzle");
            out.add(EnumChatFormatting.WHITE + "❖  " + EnumChatFormatting.AQUA + "Box" + EnumChatFormatting.DARK_PURPLE + "  ✿");
            out.add(EnumChatFormatting.YELLOW + "✎  " + EnumChatFormatting.WHITE + "Example note about this room");
            return out;
        }

        if (currentRoomName != null) {
            // Line 1: category with colour-coded label
            String categoryColor = getCategoryColor(currentRoomCategory);
            out.add(EnumChatFormatting.GRAY + "Category  " + categoryColor + (currentRoomCategory != null ? currentRoomCategory : "Unknown"));

            // Line 2: room name, with optional fairy soul indicator
            String nameLine = EnumChatFormatting.WHITE + "❖  " + EnumChatFormatting.AQUA + currentRoomName;
            if (currentRoomHasFairySoul) {
                nameLine += "  " + EnumChatFormatting.DARK_PURPLE + "✿";
            }
            out.add(nameLine);

            // Line 3: notes — only shown when present
            if (currentRoomNotes != null) {
                out.add(EnumChatFormatting.YELLOW + "✎  " + EnumChatFormatting.WHITE + currentRoomNotes);
            }
        }

        return out;
    }

    private String getCategoryColor(String category) {
        if (category == null) return EnumChatFormatting.WHITE.toString();
        switch (category.toLowerCase()) {
            case "puzzle":
                return EnumChatFormatting.LIGHT_PURPLE.toString();
            case "trap":
            case "champion":
                return EnumChatFormatting.RED.toString();
            case "miniboss":
                return EnumChatFormatting.GOLD.toString();
            case "fairy":
                return EnumChatFormatting.DARK_PURPLE.toString();
            case "rare":
                return EnumChatFormatting.YELLOW.toString();
            default:
                return EnumChatFormatting.GREEN.toString();
        }
    }
}