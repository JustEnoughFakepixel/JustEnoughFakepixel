package com.jef.justenoughfakepixel;

import com.jef.justenoughfakepixel.core.JefConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DebugLogger {

    private static final Logger LOG = LogManager.getLogger("JEF");
    private static final String PREFIX = "§8[§6JEF Debug§8] §r";

    private DebugLogger() {}

    public static void log(String message) {
        LOG.info("[JEF DEBUG] {}", message);
        if (JefConfig.feature != null && JefConfig.feature.debug.enableDebug) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.thePlayer != null)
                mc.thePlayer.addChatMessage(new ChatComponentText(PREFIX + message));
        }
    }
}