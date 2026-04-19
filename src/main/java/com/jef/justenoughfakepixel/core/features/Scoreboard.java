package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scoreboard {

    @Expose
    @ConfigOption(name = "Enable", desc = "Replace the vanilla sidebar with a custom scoreboard")
    @ConfigEditorBoolean
    public boolean enabled = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the scoreboard")
    @ConfigEditorColour
    public String scoreboardBg = "0:136:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of the scoreboard corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 20f, minStep = 1f)
    public float cornerRadius = 8f;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the scoreboard")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 2.5f, minStep = 0.1f)
    public float scale = 1.0f;

    @Expose
    @ConfigOption(name = "Hide when Tab held", desc = "Hide the scoreboard when the tab key is held")
    @ConfigEditorBoolean
    public boolean hideOnTab = true;

    @Expose
    @ConfigOption(name = "Edit Position", desc = "Drag to reposition the scoreboard")
    @ConfigEditorButton(runnableId = "openScoreboardEditor", buttonText = "Edit")
    public boolean editPosDummy = false;

    @Expose
    @ConfigOption(name = "Scoreboard Lines", desc = "Choose which lines to show and drag to reorder. Lines not found on the scoreboard are hidden automatically.")
    @ConfigEditorDraggableList(exampleText = {
            "§e03/15/26 §8hub-67",              // 0  SERVER
            "§fLate Summer §b11th",            // 1  SEASON
            "§f10:40pm",                            // 2  TIME
            "§7♲ Ironman (Profile Type)",                     // 3  PROFILE_TYPE
            "§7㋖ §bSkyblock Hub",                         // 4  ISLAND
            "§7⏣ §bVillage",                     // 5  LOCATION
            "§8─────────────────", // 6  EMPTY
            "§fPurse: §6952,763,737",           // 7  PURSE
            "§fBank: §6969M",                  // 8  BANK
            "§fBits: §b59,364,034",                // 9  BITS
            "§fGems: §a67,676,767",                // 10 GEMS
            "§8─────────────────", // 11 EMPTY
            "§6Fishing/Mining/Spooky/Travelling_Zoo(Events) §f12m 30s",    // 12 EVENT
            "§dCookie Buff: §f67d 21h",         // 13 COOKIE
            "§fPower: §dBizzare", // 14 POWER
            "§8─────────────────", // 15 EMPTY
            "§fFetchur: §eMilk",               // 16 FETCHUR
            "§fSlayer Quest\n§4Voidgloom Seraph IV\n§7(17/6,767) Combat XP",  // 17 SLAYER
            "§8──────────────────(emptyline)", // 18 EMPTY
            "§8──────────────────(emptyline)", // 19 EMPTY
            "§8──────────────────(emptyline)", // 20 EMPTY
            "§8──────────────────(emptyline)"  // 21 EMPTY
    })
    public List<Integer> scoreboardLines = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17));

    @Expose
    public Position position = new Position(-2, 140);
}