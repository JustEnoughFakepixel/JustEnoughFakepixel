package com.jef.justenoughfakepixel.core.features.dungeons;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;

public class CrushAlertConfig {

    @Expose
    @ConfigOption(name = "Crush Alert", desc = "Alert when Storm is within range of a pillar in F7/M7 phase 2")
    @ConfigEditorBoolean
    public boolean enabled = true;

    @Expose
    @ConfigOption(name = "Alert Range", desc = "Horizontal distance from pillar center to trigger the alert")
    @ConfigEditorSliderAnnotation(minValue = 1, maxValue = 7, minStep = 1)
    public int alertRange = 6;
}