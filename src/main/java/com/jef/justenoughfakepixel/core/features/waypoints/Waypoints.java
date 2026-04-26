package com.jef.justenoughfakepixel.core.features.waypoints;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.features.waypoints.*;
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
    @Category(name = "Colors", desc = "Waypoint rendering colors")
    public WaypointColorsConfig colors = new WaypointColorsConfig();

    @Expose
    @Category(name = "Auto Advance", desc = "Settings for automatic waypoint progression")
    public AutoAdvanceConfig autoAdvance = new AutoAdvanceConfig();

    // ── flat fields kept for runtime code compatibility ──────────────────────
    @Expose public String boxColour = "0:217:255:255:0";
    @Expose public String tracerColour = "0:255:255:255:0";
    @Expose public String labelColour = "0:255:255:255:255";
    @Expose public String distanceLabelColour = "0:255:85:255:255";
    @Expose public float advanceRange = 5.0f;
    @Expose public float advanceDelayMs = 2000f;
}
