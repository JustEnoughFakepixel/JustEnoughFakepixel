package com.jef.justenoughfakepixel.utils;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class OverlayUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean shouldHide() {
        if (mc.gameSettings.showDebugInfo) return true;
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindPlayerList.getKeyCode())) return true;
        return false;
    }
}