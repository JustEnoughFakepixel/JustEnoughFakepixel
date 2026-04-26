package com.jef.justenoughfakepixel.core.features.farming;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import org.lwjgl.input.Keyboard;

public class Farming {

    @Expose
    @ConfigOption(name = "Lock Mouse", desc = "Locks yaw and pitch so you can't accidentally move your camera")
    @ConfigEditorBoolean
    public boolean lockMouse = false;

    @Expose
    @ConfigOption(name = "Lock Toggle Key", desc = "Keybind to toggle mouse lock on/off")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_NONE)
    public int lockMouseKey = Keyboard.KEY_NONE;
}