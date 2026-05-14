package com.jef.justenoughfakepixel.core.features.misc;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class PerformanceHudConfig {

    @Expose
    @ConfigOption(name = "Enable", desc = "Show the performance HUD")
    @ConfigEditorBoolean
    public boolean performanceHud = false;

    @Expose
    @ConfigOption(name = "Show FPS", desc = "Show FPS counter")
    @ConfigEditorBoolean
    public boolean hudShowFps = true;

    @Expose
    @ConfigOption(name = "Show TPS", desc = "Show server TPS")
    @ConfigEditorBoolean
    public boolean hudShowTps = true;

    @Expose
    @ConfigOption(name = "Show Ping", desc = "Show current ping")
    @ConfigEditorBoolean
    public boolean hudShowPing = true;

    @Expose
    @ConfigOption(name = "Show Coordinates", desc = "Show your current X / Y / Z coordinates")
    @ConfigEditorBoolean
    public boolean hudShowCoords = false;

    @Expose
    @ConfigOption(name = "Show Rotation", desc = "Show your current yaw and pitch")
    @ConfigEditorBoolean
    public boolean hudShowRotation = false;

    @Expose
    @ConfigOption(name = "Vertical", desc = "Stack entries vertically, otherwise horizontal")
    @ConfigEditorBoolean
    public boolean hudVertical = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the HUD (alpha controls opacity)")
    @ConfigEditorColour
    public String hudBgColor = "0:136:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of HUD corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    public int hudCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Edit HUD Position", desc = "Drag the HUD to reposition it")
    @ConfigEditorButton(runnableId = "openHudEditor", buttonText = "Edit")
    public boolean editHudPosDummy = false;

    @Expose
    @ConfigOption(name = "HUD Scale", desc = "Size of the performance HUD")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    public float hudScale = 1f;

    @Expose
    public Position hudPos = new Position(2, 2);
}
