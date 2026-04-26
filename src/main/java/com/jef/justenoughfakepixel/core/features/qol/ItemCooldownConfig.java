package com.jef.justenoughfakepixel.core.features.qol;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class ItemCooldownConfig {

    @Expose
    @ConfigOption(name = "Enable Overlay", desc = "Shows a cooldown timer overlay for item abilities")
    @ConfigEditorBoolean
    public boolean itemCooldownOverlay = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the cooldown overlay")
    @ConfigEditorColour
    public String itemCooldownBgColor = "0:136:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of the overlay corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    public int itemCooldownCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the cooldown overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    public float itemCooldownScale = 1f;

    @Expose
    @ConfigOption(name = "Show When Empty", desc = "Show the overlay header even when no cooldowns are active")
    @ConfigEditorBoolean
    public boolean itemCooldownShowWhenEmpty = false;

    @Expose
    @ConfigOption(name = "Edit Overlay Position", desc = "Drag the cooldown overlay to reposition it")
    @ConfigEditorButton(runnableId = "openItemCooldownEditor", buttonText = "Edit")
    public boolean itemCooldownEditPosDummy = false;

    @Expose
    public Position itemCooldownPos = new Position(-4, 4, true, false);
}
