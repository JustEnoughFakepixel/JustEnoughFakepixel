package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import org.lwjgl.input.Keyboard;

public class Waypoints {

    @Expose
    @ConfigOption(name = "Manage Waypoints", desc = "Open waypoint manager")
    @ConfigEditorButton(runnableId = "openWaypointGroupGui", buttonText = "Open")
    public boolean manageGroupsDummy = false;

    @Expose
    @ConfigOption(name = "Manager Key", desc = "Keybind to open the waypoint manager")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_NONE)
    public int waypointManagerKey = Keyboard.KEY_NONE;

    @Expose
    @ConfigOption(name = "Colors", desc = "Waypoint rendering colors")
    @ConfigEditorAccordion(id = 1)
    public boolean colorsAccordion = false;

    @Expose
    @ConfigOption(name = "Box Colour", desc = "Colour of the ESP box drawn around the next waypoint")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1)
    public String boxColour = "0:217:255:255:0";

    @Expose
    @ConfigOption(name = "Tracer Colour", desc = "Colour of the tracer from your position to the next waypoint")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1)
    public String tracerColour = "0:255:255:255:0";

    @Expose
    @ConfigOption(name = "Label Colour", desc = "Colour of the waypoint name / number above each waypoint")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1)
    public String labelColour = "0:255:255:255:255";

    @Expose
    @ConfigOption(name = "Distance Colour", desc = "Colour of the distance number shown next to each waypoint label")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1)
    public String distanceLabelColour = "0:255:85:255:255";

    @Expose
    @ConfigOption(name = "Auto Advance", desc = "Settings for automatic waypoint progression")
    @ConfigEditorAccordion(id = 2)
    public boolean autoAdvanceAccordion = false;

    @Expose
    @ConfigOption(name = "Advance Range", desc = "How close (blocks) you must be to the next waypoint before the timer starts")
    @ConfigEditorSliderAnnotation(minValue = 1f, maxValue = 30f, minStep = 0.5f)
    @ConfigAccordionId(id = 2)
    public float advanceRange = 5.0f;

    @Expose
    @ConfigOption(name = "Advance Delay (ms)", desc = "How long (ms) you must stay within range before the waypoint auto-advances")
    @ConfigEditorSliderAnnotation(minValue = 250f, maxValue = 10000f, minStep = 250f)
    @ConfigAccordionId(id = 2)
    public float advanceDelayMs = 2000f;
}