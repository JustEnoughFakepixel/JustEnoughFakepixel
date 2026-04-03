package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;

public class Storage {

    @Expose
    @ConfigOption(name = "Enable/Disable Overlay",desc = "Whether to use the custom storage overlay or the default miencraft one.")
    @ConfigEditorBoolean
    public boolean enabled = true;

    @Expose
    @ConfigOption(name = "Scrolling", desc = "Control how the scrolling feels while in the storage overlay")
    @ConfigEditorAccordion(id = 1)
    public boolean scrollAccordian = false;

    @Expose
    @ConfigOption(name = "Scroll Speed", desc = "Control how fast the scrolling in the storage overlay is")
    @ConfigEditorSliderAnnotation(minValue = 0.25f, maxValue = 3f, minStep = 0.05f)
    @ConfigAccordionId(id = 1)
    public float scrollSpeed = 1;

    @Expose
    @ConfigOption(name = "Smooth Scroll", desc = "Enable Smooth scrolling in the storage overlay")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean smoothScroll = false;

}
