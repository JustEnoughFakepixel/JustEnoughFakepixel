package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import org.lwjgl.input.Keyboard;

public class Debug {

    @Expose
    @ConfigOption(name = "Scoreboard Debug Key", desc = "Print scoreboard JSON to chat")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_NONE)
    public int scoreboardDebugKey = Keyboard.KEY_NONE;
}