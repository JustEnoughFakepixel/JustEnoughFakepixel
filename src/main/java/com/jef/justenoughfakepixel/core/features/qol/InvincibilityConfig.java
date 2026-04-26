package com.jef.justenoughfakepixel.core.features.qol;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class InvincibilityConfig {

    @Expose
    @ConfigOption(name = "Enable Overlay", desc = "Shows a timer for the invincibility window granted by Bonzo's Mask and Spirit Mask")
    @ConfigEditorBoolean
    public boolean itemInvincibilityOverlay = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the invincibility overlay")
    @ConfigEditorColour
    public String itemInvincibilityBgColor = "0:136:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of the overlay corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    public int itemInvincibilityCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the invincibility overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    public float itemInvincibilityScale = 1f;

    @Expose
    @ConfigOption(name = "Show When Empty", desc = "Show the overlay header even when no invincibility timers are active")
    @ConfigEditorBoolean
    public boolean itemInvincibilityShowWhenEmpty = false;

    @Expose
    @ConfigOption(name = "Edit Overlay Position", desc = "Drag the invincibility overlay to reposition it")
    @ConfigEditorButton(runnableId = "openItemInvincibilityEditor", buttonText = "Edit")
    public boolean itemInvincibilityEditPosDummy = false;

    @Expose
    public Position itemInvincibilityPos = new Position(-4, 76, true, false);
}
