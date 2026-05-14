package com.jef.justenoughfakepixel.core.features.fishing;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class TrophyFishConfig {

    @Expose
    @ConfigOption(name = "Enable Overlay", desc = "Show the trophy fish count overlay")
    @ConfigEditorBoolean
    public boolean trophyOverlay = true;

    @Expose
    @ConfigOption(name = "Only in Crimson Isle", desc = "Only show the overlay while in Crimson Isle")
    @ConfigEditorBoolean
    public boolean trophyOnlyCrimson = true;

    @Expose
    @ConfigOption(name = "Modify Chat Messages", desc = "Replace the default trophy fish catch message with a formatted version showing the catch count")
    @ConfigEditorBoolean
    public boolean trophyChatModify = true;

    @Expose
    @ConfigOption(name = "Hide Bronze Repeats", desc = "Suppress repeat Bronze trophy fish chat messages (first catch still shown)")
    @ConfigEditorBoolean
    public boolean trophyBronzeHider = false;

    @Expose
    @ConfigOption(name = "Hide Silver Repeats", desc = "Suppress repeat Silver trophy fish chat messages (first catch still shown)")
    @ConfigEditorBoolean
    public boolean trophySilverHider = false;

    @Expose
    @ConfigOption(name = "Odger Tooltip Total", desc = "Add total catch count to trophy fish tooltips in the Odger Trophy Fishing GUI")
    @ConfigEditorBoolean
    public boolean trophyOdgerTotal = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the trophy fish overlay (alpha controls opacity)")
    @ConfigEditorColour
    public String trophyFishBgColor = "160:0:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of overlay corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    public int trophyFishCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the trophy fish overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    public float trophyFishScale = 1f;

    @Expose
    @ConfigOption(name = "Edit Position", desc = "Drag the overlay to reposition it")
    @ConfigEditorButton(runnableId = "openTrophyFishEditor", buttonText = "Edit")
    public boolean editTrophyFishPosDummy = false;

    @Expose
    public Position trophyFishPos = new Position(4, 140);
}
