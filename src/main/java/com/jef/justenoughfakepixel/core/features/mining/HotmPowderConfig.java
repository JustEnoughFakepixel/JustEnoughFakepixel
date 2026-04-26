package com.jef.justenoughfakepixel.core.features.mining;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;

public class HotmPowderConfig {

    @Expose
    @ConfigOption(name = "Powder Spent", desc = "Show total powder invested vs max cost on each HOTM perk tooltip")
    @ConfigEditorBoolean
    public boolean hotmPowderSpent = true;

    @Expose
    @ConfigOption(name = "Powder Spent Format", desc = "How to display the powder spent amount")
    @ConfigEditorDropdown(values = {"Number", "Percentage", "Number and Percentage"})
    public int hotmPowderSpentDesign = 0;

    @Expose
    @ConfigOption(name = "Powder for Next 10 Levels", desc = "Hold Shift on a perk to see powder cost for the next 10 upgrades")
    @ConfigEditorBoolean
    public boolean hotmPowderFor10Levels = true;
}
