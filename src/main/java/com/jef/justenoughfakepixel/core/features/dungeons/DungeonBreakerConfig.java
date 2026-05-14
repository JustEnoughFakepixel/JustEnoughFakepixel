package com.jef.justenoughfakepixel.core.features.dungeons;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class DungeonBreakerConfig {

    @Expose
    @ConfigOption(name = "Enable", desc = "Show the Dungeon Breaker charge overlay (only visible in dungeons with the item in hotbar)")
    @ConfigEditorBoolean
    public boolean dungeonBreakerOverlay = false;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the overlay (alpha controls opacity; 0 = fully transparent)")
    @ConfigEditorColour
    public String dungeonBreakerBgColor = "0:0:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of overlay corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    public int dungeonBreakerCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the Dungeon Breaker overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    public float dungeonBreakerScale = 1f;

    @Expose
    @ConfigOption(name = "Edit Position", desc = "Drag the overlay to reposition it")
    @ConfigEditorButton(runnableId = "openDungeonBreakerEditor", buttonText = "Edit")
    public boolean editDungeonBreakerPosDummy = false;

    @Expose
    public Position dungeonBreakerPos = new Position(4, 120);
}
