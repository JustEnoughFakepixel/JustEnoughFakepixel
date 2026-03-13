package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;

public class Scoreboard {

    // ── Master accordion ─────────────────────────────────────────────────────

    @Expose
    @ConfigOption(name = "Cleaner Scoreboard", desc = "Settings for the custom scoreboard overlay")
    @ConfigEditorAccordion(id = 20)
    public boolean scoreboardAccordion = false;

    @Expose
    @ConfigOption(name = "Enable", desc = "Replace the vanilla sidebar with a custom scoreboard panel")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 20)
    public boolean enabled = true;

    //  ─────────────────────────────────────────────────────────

    @Expose
    @ConfigOption(name = "Opacity", desc = "Background opacity (0–100)")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 100f, minStep = 5f)
    @ConfigAccordionId(id = 20)
    public float opacity = 70f;

    @Expose
    @ConfigOption(name = "Scale", desc = "Size of the scoreboard panel")
    @ConfigEditorSliderAnnotation(minValue = 0.5f, maxValue = 2.5f, minStep = 0.1f)
    @ConfigAccordionId(id = 20)
    public float scale = 1.0f;

    @Expose
    @ConfigOption(name = "Edit Position", desc = "Drag the scoreboard to reposition it")
    @ConfigEditorButton(runnableId = "openScoreboardEditor", buttonText = "Edit")
    @ConfigAccordionId(id = 20)
    public boolean editPosDummy = false;

    @Expose
    @ConfigOption(name = "Corner Radius", desc = "Roundness of the scoreboard corners")
    @ConfigEditorSliderAnnotation(minValue = 0f, maxValue = 20f, minStep = 1f)
    @ConfigAccordionId(id = 20)
    public float cornerRadius = 8f;

    @Expose
    public Position position = new Position(-2, 10);
}
