package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class Mining {

    @Expose
    @ConfigOption(name = "Fetchur Overlay", desc = "Settings for the Fetchur item overlay")
    @ConfigEditorAccordion(id = 20)
    public boolean fetchurAccordion = false;

    @Expose
    @ConfigOption(name = "Show Fetchur Overlay", desc = "Shows today's Fetchur item on screen while in Skyblock")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 20)
    public boolean showFetchurOverlay = true;

    @Expose
    @ConfigOption(name = "Background", desc = "Draw a dark background behind the overlay")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 20)
    public boolean overlayBackground = true;

    @Expose
    @ConfigOption(name = "Overlay Scale", desc = "Size of the Fetchur overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    @ConfigAccordionId(id = 20)
    public float fetchurOverlayScale = 1f;

    @Expose
    @ConfigOption(name = "Edit Overlay Position", desc = "Drag to reposition the Fetchur overlay")
    @ConfigEditorButton(runnableId = "openFetchurEditor", buttonText = "Edit")
    @ConfigAccordionId(id = 20)
    public boolean editFetchurPosDummy = false;

    @Expose
    public Position fetchurOverlayPos = new Position(4, 4);
}