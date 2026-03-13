package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class Misc {

    @Expose
    @ConfigOption(name = "Performance HUD", desc = "Settings for the performance HUD")
    @ConfigEditorAccordion(id = 3)
    public boolean performanceHudAccordion = false;

    @Expose
    @ConfigOption(name = "Enable", desc = "Show the performance HUD")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean performanceHud = false;

    @Expose
    @ConfigOption(name = "Show FPS", desc = "Show FPS counter")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean hudShowFps = true;

    @Expose
    @ConfigOption(name = "Show TPS", desc = "Show server TPS")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean hudShowTps = true;

    @Expose
    @ConfigOption(name = "Show Ping", desc = "Show current ping")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean hudShowPing = true;

    @Expose
    @ConfigOption(name = "Vertical", desc = "Stack entries vertically, otherwise horizontal")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean hudVertical = true;

    @Expose
    @ConfigOption(name = "Background", desc = "Draw a dark background behind the HUD")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean hudBackground = true;

    @Expose
    @ConfigOption(name = "Edit HUD Position", desc = "Drag the HUD to reposition it")
    @ConfigEditorButton(runnableId = "openHudEditor", buttonText = "Edit")
    @ConfigAccordionId(id = 3)
    public boolean editHudPosDummy = false;

    @Expose
    @ConfigOption(name = "HUD Scale", desc = "Size of the performance HUD")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    @ConfigAccordionId(id = 3)
    public float hudScale = 1f;

    @Expose
    public Position hudPos = new Position(2, 2);

    @Expose
    @ConfigOption(name = "Item Stack Tips", desc = "Shows enchant levels on books and floor numbers on Catacombs passes")
    @ConfigEditorBoolean
    public boolean itemStackTips = true;

    @Expose
    @ConfigOption(name = "Skill XP Display", desc = "Hold SHIFT on a skill item to see XP remaining to max level")
    @ConfigEditorBoolean
    public boolean skillXpDisplay = true;

    @Expose
    @ConfigOption(name = "Search Bar", desc = "Search bar settings")
    @ConfigEditorAccordion(id = 10)
    public boolean searchBarAccordion = false;

    @Expose
    @ConfigOption(name = "Enable", desc = "Shows a search bar in supported GUIs")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 10)
    public boolean searchBar = true;

    @Expose
    @ConfigOption(name = "Highlight Color", desc = "Color used to highlight matching items in the search results")
    @ConfigEditorColour
    @ConfigAccordionId(id = 10)
    public String searchBarHighlightColor = "0:102:255:0:0";

    @Expose
    @ConfigOption(name = "No Swap Animation", desc = "Removes the item lowering animation when switching hotbar slots")
    @ConfigEditorBoolean
    public boolean noItemSwitchAnimation = true;

    @Expose
    @ConfigOption(name = "Show Own Nametag", desc = "Shows your own nametag in third person")
    @ConfigEditorBoolean
    public boolean showOwnNametag = false;

    @Expose
    @ConfigOption(name = "Disable Entity Fire", desc = "Hides the fire overlay rendered on burning entities")
    @ConfigEditorBoolean
    public boolean disableEntityFire = false;
}