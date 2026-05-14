package com.jef.justenoughfakepixel.core.features.qol;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;

public class EnchantParserConfig {

    @Expose
    @ConfigOption(name = "Enable", desc = "Color enchants by level and sort ultimates to the top")
    @ConfigEditorBoolean
    public boolean enchantHighlight = true;

    @Expose
    @ConfigOption(name = "Layout", desc = "Normal: 2 per line | Compress: pack to fit | Expand: one per line with descriptions")
    @ConfigEditorDropdown(values = {"Normal", "Compress", "Expand"})
    public int enchantLayout = 0;

    @Expose
    @ConfigOption(name = "Chroma", desc = "Animate enchant colors with a rainbow chroma effect when chroma is selected in color picker")
    @ConfigEditorBoolean
    public boolean enchantChroma = true;

    @Expose
    @ConfigOption(name = "Chroma Speed", desc = "Speed of the chroma animation (lower = faster)")
    @ConfigEditorSliderAnnotation(minValue = 10f, maxValue = 5000f, minStep = 10f)
    public int enchantChromaSpeed = 1000;

    @Expose
    @ConfigOption(name = "Chroma Mode", desc = "All Same: one hue | Fade: position-based gradient")
    @ConfigEditorDropdown(values = {"All Same", "Fade"})
    public int enchantChromaMode = 1;

    @Expose
    @ConfigOption(name = "Chroma Size", desc = "Gradient size for fade mode")
    @ConfigEditorSliderAnnotation(minValue = 20f, maxValue = 400f, minStep = 5f)
    public float enchantChromaSize = 120f;

    @Expose
    @ConfigOption(name = "Poor Color", desc = "Color for enchants below half max level")
    @ConfigEditorColour
    public String enchantPoorColor = "0:170:170:170:170";

    @Expose
    @ConfigOption(name = "Good Color", desc = "Color for enchants at or above half max level")
    @ConfigEditorColour
    public String enchantGoodColor = "0:255:85:255:85";

    @Expose
    @ConfigOption(name = "Great Color", desc = "Color for enchants one below max level")
    @ConfigEditorColour
    public String enchantGreatColor = "0:255:85:85:255";

    @Expose
    @ConfigOption(name = "Perfect Color", desc = "Color for enchants at max level")
    @ConfigEditorColour
    public String enchantPerfectColor = "0:255:255:85:255";

    @Expose
    @ConfigOption(name = "Ultimate Color", desc = "Color for ultimate enchants")
    @ConfigEditorColour
    public String enchantUltimateColor = "0:255:255:85:255";
}
