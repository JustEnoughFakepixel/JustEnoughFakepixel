package com.jef.justenoughfakepixel.core.categories;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class MiscConfig {

    @Expose
    @ConfigOption(name = "Performance HUD", desc = "Shows FPS, TPS and Ping")
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
    @ConfigOption(name = "Vertical", desc = "Stack entries vertically")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean hudVertical = true;

    @Expose
    @ConfigOption(name = "Background", desc = "Draw a background behind HUD")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean hudBackground = true;

    @Expose
    @ConfigOption(name = "Edit HUD Position", desc = "Drag HUD to reposition")
    @ConfigEditorButton(runnableId = "openHudEditor", buttonText = "Edit")
    @ConfigAccordionId(id = 3)
    public boolean editHudPosDummy = false;

    @Expose
    @ConfigOption(name = "HUD Scale", desc = "Size of the HUD")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    @ConfigAccordionId(id = 3)
    public float hudScale = 1f;

    @Expose
    public Position hudPos = new Position(2, 2);

    @Expose
    @ConfigOption(name = "Item Stack Tips", desc = "Shows enchant levels on books")
    @ConfigEditorBoolean
    public boolean itemStackTips = true;

    @Expose
    @ConfigOption(name = "Skill XP Display", desc = "Hold SHIFT to see XP remaining")
    @ConfigEditorBoolean
    public boolean skillXpDisplay = true;

    @Expose
    @ConfigOption(name = "Enable Search Bar", desc = "Shows GUI search bar")
    @ConfigEditorBoolean
    public boolean searchBar = true;

    @Expose
    @ConfigOption(name = "Edit Search Bar Position", desc = "Drag the search bar to reposition it")
    @ConfigEditorButton(runnableId = "openSearchBarEditor", buttonText = "Edit")
    public boolean editSearchBarPosDummy = false;

    @Expose
    public Position searchBarPos = new Position(0, -30, true, false);

    @Expose
    @ConfigOption(name = "No Swap Animation", desc = "Removes item switch animation")
    @ConfigEditorBoolean
    public boolean noItemSwitchAnimation = true;

    @Expose
    @ConfigOption(name = "Show Own Nametag", desc = "Shows your nametag in 3rd person")
    @ConfigEditorBoolean
    public boolean showOwnNametag = false;

    @Expose
    @ConfigOption(name = "Disable Entity Fire", desc = "Hides burning entity fire")
    @ConfigEditorBoolean
    public boolean disableEntityFire = false;
}