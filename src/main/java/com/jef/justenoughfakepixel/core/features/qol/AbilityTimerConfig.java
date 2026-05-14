package com.jef.justenoughfakepixel.core.features.qol;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class AbilityTimerConfig {

    @Expose
    @ConfigOption(name = "Enable Overlay", desc = "Shows a timer while an item ability is active (e.g. Fire Veil Wand 5s wall)")
    @ConfigEditorBoolean
    public boolean itemAbilityTimerOverlay = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the ability timer overlay")
    @ConfigEditorColour
    public String itemAbilityTimerBgColor = "0:136:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of the overlay corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    public int itemAbilityTimerCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the ability timer overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    public float itemAbilityTimerScale = 1f;

    @Expose
    @ConfigOption(name = "Show When Empty", desc = "Show the overlay header even when no ability timers are active")
    @ConfigEditorBoolean
    public boolean itemAbilityTimerShowWhenEmpty = false;

    @Expose
    @ConfigOption(name = "Edit Overlay Position", desc = "Drag the ability timer overlay to reposition it")
    @ConfigEditorButton(runnableId = "openItemAbilityTimerEditor", buttonText = "Edit")
    public boolean itemAbilityTimerEditPosDummy = false;

    @Expose
    public Position itemAbilityTimerPos = new Position(-4, 40, true, false);
}
