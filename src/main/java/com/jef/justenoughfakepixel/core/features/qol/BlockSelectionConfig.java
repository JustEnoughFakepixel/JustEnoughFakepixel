package com.jef.justenoughfakepixel.core.features.qol;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;

public class BlockSelectionConfig {

    @Expose
    @ConfigOption(name = "Enable", desc = "Replace the vanilla block selection outline with a custom one")
    @ConfigEditorBoolean
    public boolean blockSelectionOverlay = false;

    @Expose
    @ConfigOption(name = "Mode", desc = "Filled: solid color fill | Outline: configurable box outline")
    @ConfigEditorDropdown(values = {"Filled", "Outline"})
    public int blockSelectionMode = 1;

    @Expose
    @ConfigOption(name = "Outline Thickness", desc = "Thickness of the outline when in Outline mode")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 5f, minStep = 0.5f)
    public float blockSelectionThickness = 2f;

    @Expose
    @ConfigOption(name = "Color", desc = "Color of the block highlight")
    @ConfigEditorColour
    public String blockSelectionColor = "180:255:255:255:255";
}
