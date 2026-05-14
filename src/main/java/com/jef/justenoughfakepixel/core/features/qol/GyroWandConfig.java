package com.jef.justenoughfakepixel.core.features.qol;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;

public class GyroWandConfig {

    @Expose
    @ConfigOption(name = "Enable", desc = "Shows the area of effect ring when holding the Gyrokinetic Wand")
    @ConfigEditorBoolean
    public boolean gyroWand = true;

    @Expose
    @ConfigOption(name = "Ring Thickness", desc = "Thickness of the area of effect ring")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 5f, minStep = 0.5f)
    public float gyroWandThickness = 2f;
}
