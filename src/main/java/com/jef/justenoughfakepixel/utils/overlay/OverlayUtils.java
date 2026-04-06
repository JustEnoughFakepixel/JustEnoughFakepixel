package com.jef.justenoughfakepixel.utils.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;

public class OverlayUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean shouldHide() {
        if (mc.gameSettings.showDebugInfo) return true;
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindPlayerList.getKeyCode())) return true;
        return mc.currentScreen instanceof GuiChat;
    }
}