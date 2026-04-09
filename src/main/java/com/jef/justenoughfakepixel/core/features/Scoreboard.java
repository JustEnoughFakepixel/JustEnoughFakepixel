package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scoreboard {

    @Expose
    @ConfigOption(name = "Custom Scoreboard", desc = "Settings for the custom scoreboard overlay")
    @ConfigEditorAccordion(id = 20)
    public boolean scoreboardAccordion = false;

    @Expose
    @ConfigOption(name = "Enable", desc = "Replace the vanilla sidebar with a custom scoreboard")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 20)
    public boolean enabled = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the scoreboard")
    @ConfigEditorColour
    @ConfigAccordionId(id = 20)
    public String scoreboardBg = "0:136:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of the scoreboard corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 20f, minStep = 1f)
    @ConfigAccordionId(id = 20)
    public float cornerRadius = 8f;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the scoreboard")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 2.5f, minStep = 0.1f)
    @ConfigAccordionId(id = 20)
    public float scale = 1.0f;

    @Expose
    @ConfigOption(name = "Hide when Tab held", desc = "Hide the scoreboard when the tab key is held")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 20)
    public boolean hideOnTab = true;

    @Expose
    @ConfigOption(name = "Edit Position", desc = "Drag to reposition the scoreboard")
    @ConfigEditorButton(runnableId = "openScoreboardEditor", buttonText = "Edit")
    @ConfigAccordionId(id = 20)
    public boolean editPosDummy = false;

    @Expose
    @ConfigOption(name = "Scoreboard Lines", desc = "Choose which lines to show and drag to reorder. Lines not found on the scoreboard are hidden automatically.")
    @ConfigEditorDraggableList(exampleText = {"§e03/15/26 §8dh-1",              // 0  SERVER
            "§fLate Summer §b11th",            // 1  SEASON
            "§f10:40pm",                            // 2  TIME
            "§7♲ Ironman",                     // 3  PROFILE_TYPE
            "㋖§6 Hub",                         // 4  ISLAND
            "§b⏣ Village",                     // 5  LOCATION
            "§8─────────────────", // 6  EMPTY
            "§fPurse: §652,763,737",           // 7  PURSE
            "§fBank: §6249M",                  // 8  BANK
            "§fBits: §b59,364",                // 9  BITS
            "§fGems: §a57,873",                // 10 GEMS
            "§8─────────────────", // 11 EMPTY
            "§6Fishing Festival §f12m 30s",    // 12 EVENT
            "§dCookie Buff: §f3d 17h",         // 13 COOKIE
            "§fPower: §dSighted", // 14 POWER
            "§8─────────────────", // 15 EMPTY
            "§fFetchur: §eSand",               // 16 FETCHUR
            "§fSlayer Quest\n§4Voidgloom Seraph IV\n§7(1227/1,400) Combat XP",  // 17 SLAYER
            "§8─────────────────", // 18 EMPTY
            "§8─────────────────", // 19 EMPTY
            "§8─────────────────", // 20 EMPTY
            "§8─────────────────"  // 21 EMPTY
    })
    @ConfigAccordionId(id = 20)
    public List<Integer> scoreboardLines = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17));

    @Expose
    public Position position = new Position(-2, 140);
}