package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;

public class General {

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
}