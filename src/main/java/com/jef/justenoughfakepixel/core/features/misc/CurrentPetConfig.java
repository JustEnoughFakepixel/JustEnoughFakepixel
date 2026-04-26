package com.jef.justenoughfakepixel.core.features.misc;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class CurrentPetConfig {

    @Expose
    @ConfigOption(name = "Enable", desc = "Show the current pet overlay")
    @ConfigEditorBoolean
    public boolean showCurrentPet = true;

    @Expose
    @ConfigOption(name = "Background Color", desc = "Background color of the pet overlay (alpha controls opacity)")
    @ConfigEditorColour
    public String currentPetBgColor = "0:0:0:0:0";

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of the pet overlay corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 12f, minStep = 1f)
    public int currentPetCornerRadius = 4;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the pet overlay")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 3f, minStep = 0.1f)
    public float currentPetScale = 1.5f;

    @Expose
    @ConfigOption(name = "Edit Position", desc = "Drag to reposition the pet overlay")
    @ConfigEditorButton(runnableId = "openCurrentPetEditor", buttonText = "Edit")
    public boolean editCurrentPetPosDummy = false;

    @Expose
    public Position currentPetPos = new Position(18, 14);
}
