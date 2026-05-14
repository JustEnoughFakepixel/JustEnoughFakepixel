package com.jef.justenoughfakepixel.core.features.misc;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;

public class InvButtonsConfig {

    @Expose
    @ConfigOption(name = "Enable", desc = "Show inventory buttons")
    @ConfigEditorBoolean
    public boolean enableInvButtons = true;

    @Expose
    @ConfigOption(name = "Open Button Editor", desc = "Open the button editor (/jefbuttons)")
    @ConfigEditorButton(runnableId = "openInvButtonEditor", buttonText = "Open")
    public boolean openInvButtonEditorDummy = false;

    @Expose
    @ConfigOption(name = "Click Type", desc = "Mouse Down or Mouse Up to fire")
    @ConfigEditorDropdown(values = {"Mouse Down", "Mouse Up"})
    public int invButtonClickType = 0;

    @Expose
    @ConfigOption(name = "Tooltip Delay (ms)", desc = "Hover time before command tooltip appears")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 1500f, minStep = 50f)
    public int invButtonTooltipDelay = 600;
}
