package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;

public class Storage {

    @Expose
    @ConfigOption(name = "Enable", desc = "Enable Custom Storage Overlay")
    @ConfigEditorBoolean
    public boolean enabled = true;

    @Expose
    @ConfigOption(name = "Scroll Speed", desc = "Change how fast or slow the scrolling is")
    @ConfigEditorSliderAnnotation(minValue = 0.1f,maxValue = 3,minStep = 0.01f)
    public float scrollSpeed = 1f;

    @Expose
    @ConfigOption(name = "Horizontal Scrolling", desc = "Make the grid have horizontal scrolling instead of vertical")
    @ConfigEditorBoolean
    public boolean horizontal = true;
}
