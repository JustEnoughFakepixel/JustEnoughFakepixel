package com.jef.justenoughfakepixel.features.misc.SkyblockExp;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.events.ActionBarXpGainEvent;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.chat.ChatUtils;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class SkyblockXpInChat {

    private static final String PREFIX = EnumChatFormatting.DARK_AQUA + "[SkyBlock XP] " + EnumChatFormatting.RESET;

    @SubscribeEvent
    public void onXpGain(ActionBarXpGainEvent event) {
        if (JefConfig.feature == null || !JefConfig.feature.misc.skyblockXpInChat) return;

        ChatUtils.sendMessage(PREFIX + event.getFormattedText());
    }
}