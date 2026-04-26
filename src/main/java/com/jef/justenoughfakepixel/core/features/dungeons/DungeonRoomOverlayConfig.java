package com.jef.justenoughfakepixel.core.features.dungeons;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class DungeonRoomOverlayConfig {

    @Expose
    @ConfigOption(name = "Enable", desc = "Show current room name overlay while in dungeons")
    @ConfigEditorBoolean
    public boolean dungeonRoomOverlay = false;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the overlay")
    @ConfigEditorColour
    public String dungeonRoomOverlayBgColor = "0:0:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of overlay corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    public int dungeonRoomOverlayCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the room name overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    public float dungeonRoomOverlayScale = 1f;

    @Expose
    @ConfigOption(name = "Edit Position", desc = "Drag the overlay to reposition it")
    @ConfigEditorButton(runnableId = "openDungeonRoomOverlayEditor", buttonText = "Edit")
    public boolean editDungeonRoomOverlayPosDummy = false;

    @Expose
    public Position dungeonRoomOverlayPos = new Position(4, 140);
}
