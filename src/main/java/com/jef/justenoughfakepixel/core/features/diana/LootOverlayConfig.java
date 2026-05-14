package com.jef.justenoughfakepixel.core.features.diana;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class LootOverlayConfig {

    @Expose
    @ConfigOption(name = "Show Loot Overlay", desc = "Show the Diana Loot HUD")
    @ConfigEditorBoolean
    public boolean showLootOverlay = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the Loot overlay (alpha controls opacity)")
    @ConfigEditorColour
    public String lootBgColor = "0:136:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of the Loot overlay corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    public int lootCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the Loot overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    public float lootScale = 1f;

    @Expose
    public Position lootOverlayPos = new Position(4, 310);
}
