package com.jef.justenoughfakepixel.core.features.diana;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class EventOverlayConfig {

    @Expose
    @ConfigOption(name = "Show Event Overlay", desc = "Show the Diana Event HUD")
    @ConfigEditorBoolean
    public boolean showEventOverlay = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the Event overlay (alpha controls opacity)")
    @ConfigEditorColour
    public String eventBgColor = "0:136:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of the Event overlay corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    public int eventCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the Event overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    public float eventScale = 1f;

    @Expose
    public Position eventOverlayPos = new Position(4, 200);
}
