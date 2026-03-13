package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class Diana {

    @Expose
    @ConfigOption(name = "Diana Tracker", desc = "Enables tracking")
    @ConfigEditorBoolean
    public boolean enabled = true;

    @Expose
    @ConfigOption(name = "Show Event Overlay", desc = "Show the Diana Event HUD (playtime, borrows, mob rates)")
    @ConfigEditorBoolean
    public boolean showEventOverlay = true;

    @Expose
    @ConfigOption(name = "Show Loot Overlay", desc = "Show the Diana Loot HUD (chimeras, drops)")
    @ConfigEditorBoolean
    public boolean showLootOverlay = true;

    @Expose
    @ConfigOption(name = "Overlay Background", desc = "Draw a dark background behind both overlays")
    @ConfigEditorBoolean
    public boolean overlayBackground = true;

    @Expose
    @ConfigOption(name = "Overlay Scale", desc = "Size of both Diana overlays")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    public float overlayScale = 1f;

    @Expose
    @ConfigOption(name = "Edit Event Overlay Position", desc = "Drag to reposition the Diana Event overlay")
    @ConfigEditorButton(runnableId = "openDianaEventEditor", buttonText = "Edit")
    public boolean editEventPosDummy = false;

    @Expose
    @ConfigOption(name = "Edit Loot Overlay Position", desc = "Drag to reposition the Diana Loot overlay")
    @ConfigEditorButton(runnableId = "openDianaLootEditor", buttonText = "Edit")
    public boolean editLootPosDummy = false;

    @Expose
    public Position eventOverlayPos = new Position(4, 200);

    @Expose
    public Position lootOverlayPos = new Position(4, 310);
}