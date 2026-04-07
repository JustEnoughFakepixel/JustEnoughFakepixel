package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class Qol {

    @Expose
    @ConfigOption(name = "Damage Splashes", desc = "Settings for damage number nametags")
    @ConfigEditorAccordion(id = 0)
    public boolean damageSplashesAccordion = false;

    @Expose
    @ConfigOption(name = "Hide Crit Splashes", desc = "Hides crit damage nametags (\u2727 stars)")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean hideCritSplashes = false;

    @Expose
    @ConfigOption(name = "Hide Non-Crit Splashes", desc = "Hides gray and fire-aspect damage numbers")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean hideNonCritSplashes = false;


    @Expose
    @ConfigOption(name = "Enchant Parser", desc = "Settings for enchants and layout")
    @ConfigEditorAccordion(id = 21)
    public boolean enchantHighlightAccordion = false;

    @Expose
    @ConfigOption(name = "Enable", desc = "Color enchants by level and sort ultimates to the top")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 21)
    public boolean enchantHighlight = true;

    @Expose
    @ConfigOption(name = "Layout", desc = "Normal: 2 per line | Compress: pack to fit | Expand: one per line with descriptions")
    @ConfigEditorDropdown(values = {"Normal", "Compress", "Expand"})
    @ConfigAccordionId(id = 21)
    public int enchantLayout = 0;

    @Expose
    @ConfigOption(name = "Chroma", desc = "Animate enchant colors with a rainbow chroma effect when chroma is selected in color picker")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 21)
    public boolean enchantChroma = true;

    @Expose
    @ConfigOption(name = "Chroma Speed", desc = "Speed of the chroma animation (lower = faster)")
    @ConfigEditorSliderAnnotation(minValue = 10f, maxValue = 5000f, minStep = 10f)
    @ConfigAccordionId(id = 21)
    public int enchantChromaSpeed = 1000;

    @Expose
    @ConfigOption(name = "Chroma Mode", desc = "All Same: one hue | Fade: position-based gradient")
    @ConfigEditorDropdown(values = {"All Same", "Fade"})
    @ConfigAccordionId(id = 21)
    public int enchantChromaMode = 1;

    @Expose
    @ConfigOption(name = "Chroma Size", desc = "Gradient size for fade mode")
    @ConfigEditorSliderAnnotation(minValue = 20f, maxValue = 400f, minStep = 5f)
    @ConfigAccordionId(id = 21)
    public float enchantChromaSize = 120f;

    @Expose
    @ConfigOption(name = "Poor Color", desc = "Color for enchants below half max level")
    @ConfigEditorColour
    @ConfigAccordionId(id = 21)
    public String enchantPoorColor = "0:170:170:170:170";

    @Expose
    @ConfigOption(name = "Good Color", desc = "Color for enchants at or above half max level")
    @ConfigEditorColour
    @ConfigAccordionId(id = 21)
    public String enchantGoodColor = "0:255:85:255:85";

    @Expose
    @ConfigOption(name = "Great Color", desc = "Color for enchants one below max level")
    @ConfigEditorColour
    @ConfigAccordionId(id = 21)
    public String enchantGreatColor = "0:255:85:85:255";

    @Expose
    @ConfigOption(name = "Perfect Color", desc = "Color for enchants at max level")
    @ConfigEditorColour
    @ConfigAccordionId(id = 21)
    public String enchantPerfectColor = "0:255:255:85:255";

    @Expose
    @ConfigOption(name = "Ultimate Color", desc = "Color for ultimate enchants")
    @ConfigEditorColour
    @ConfigAccordionId(id = 21)
    public String enchantUltimateColor = "0:255:255:85:255";

    @Expose
    @ConfigOption(name = "Gyro Wand Ring", desc = "Settings for the Gyrokinetic Wand AoE ring")
    @ConfigEditorAccordion(id = 20)
    public boolean gyroWandAccordion = false;

    @Expose
    @ConfigOption(name = "Enable", desc = "Shows the area of effect ring when holding the Gyrokinetic Wand")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 20)
    public boolean gyroWand = true;

    @Expose
    @ConfigOption(name = "Ring Thickness", desc = "Thickness of the area of effect ring")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 5f, minStep = 0.5f)
    @ConfigAccordionId(id = 20)
    public float gyroWandThickness = 2f;

    @Expose
    @ConfigOption(name = "Item Cooldown Overlay", desc = "Settings for the item ability cooldown overlay")
    @ConfigEditorAccordion(id = 23)
    public boolean itemCooldownAccordion = false;

    @Expose
    @ConfigOption(name = "Enable Overlay", desc = "Shows a cooldown timer overlay for item abilities")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 23)
    public boolean itemCooldownOverlay = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the cooldown overlay")
    @ConfigEditorColour
    @ConfigAccordionId(id = 23)
    public String itemCooldownBgColor = "0:136:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of the overlay corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    @ConfigAccordionId(id = 23)
    public int itemCooldownCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the cooldown overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    @ConfigAccordionId(id = 23)
    public float itemCooldownScale = 1f;

    @Expose
    @ConfigOption(name = "Edit Overlay Position", desc = "Drag the cooldown overlay to reposition it")
    @ConfigEditorButton(runnableId = "openItemCooldownEditor", buttonText = "Edit")
    @ConfigAccordionId(id = 23)
    public boolean itemCooldownEditPosDummy = false;

    @Expose
    public Position itemCooldownPos = new Position(4, 4);

    @Expose
    @ConfigOption(name = "Block Selection Overlay", desc = "Customize the block selection highlight")
    @ConfigEditorAccordion(id = 22)
    public boolean blockSelectionAccordion = false;

    @Expose
    @ConfigOption(name = "Enable", desc = "Replace the vanilla block selection outline with a custom one")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 22)
    public boolean blockSelectionOverlay = false;

    @Expose
    @ConfigOption(name = "Mode", desc = "Filled: solid color fill | Outline: configurable box outline")
    @ConfigEditorDropdown(values = {"Filled", "Outline"})
    @ConfigAccordionId(id = 22)
    public int blockSelectionMode = 1;

    @Expose
    @ConfigOption(name = "Outline Thickness", desc = "Thickness of the outline when in Outline mode")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 5f, minStep = 0.5f)
    @ConfigAccordionId(id = 22)
    public float blockSelectionThickness = 2f;

    @Expose
    @ConfigOption(name = "Color", desc = "Color of the block highlight")
    @ConfigEditorColour
    @ConfigAccordionId(id = 22)
    public String blockSelectionColor = "180:255:255:255:255";

    @Expose
    @ConfigOption(name = "Roman Numerals", desc = "Converts Roman numerals to integers in tooltips and tab list")
    @ConfigEditorBoolean
    public boolean romanNumerals = true;

    @Expose
    @ConfigOption(name = "Prevent Cursor Reset", desc = "Prevents the mouse cursor from resetting when opening GUIs")
    @ConfigEditorBoolean
    public boolean preventCursorReset = true;

    @Expose
    @ConfigOption(name = "Skyblock ID", desc = "Shows the skyblock item ID at the bottom of item tooltips")
    @ConfigEditorBoolean
    public boolean showSkyblockId = true;

    @Expose
    @ConfigOption(name = "Disable Enchant Glint", desc = "Removes the enchantment glint effect")
    @ConfigEditorBoolean
    public boolean disableEnchantGlint = false;

    @Expose
    @ConfigOption(name = "Brewing helper", desc = "Highlights brewing stands when done brewing")
    @ConfigEditorBoolean
    public boolean colorBrewingStands = true;


    @Expose
    @ConfigOption(name = "Missing Enchants", desc = "Hold SHIFT on an enchanted item to see missing enchants")
    @ConfigEditorBoolean
    public boolean missingEnchants = true;

    @Expose
    @ConfigOption(name = "Confirm Disconnect", desc = "Makes you click twice to disconnect")
    @ConfigEditorBoolean
    public boolean confirmDisconnect = true;
}