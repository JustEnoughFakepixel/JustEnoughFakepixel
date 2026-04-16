package com.jef.justenoughfakepixel.features.misc;

import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.repo.PlayerTagRepo;
import com.jef.justenoughfakepixel.repo.data.PlayerTagData;
import com.jef.justenoughfakepixel.utils.chat.ChatUtils;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RegisterEvents
public class PlayerTagListener {

    private static final Pattern CHAT_PATTERN = Pattern.compile(
            "^(.*?)([A-Za-z0-9_]{1,16})(\\s*:\\s*.+)$"
    );

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onChat(ClientChatReceivedEvent event) {
        // Only process server chat (type 1), ignore system/action bar messages
        if (!ChatUtils.isFromServer(event)) return;

        String plain = ChatUtils.clean(event);

        // Skip party, guild, private messages — only modify public/all chat
        if (ChatUtils.isPartyMessage(plain)) return;
        if (ChatUtils.isMsgReceived(plain)) return;
        if (ChatUtils.isMsgSent(plain)) return;

        Matcher m = CHAT_PATTERN.matcher(plain);
        if (!m.matches()) return;

        String prefix = m.group(1); // All the prefix junk before the IGN
        String ign    = m.group(2); // The player's IGN
        String tail   = m.group(3); // ": message"

        PlayerTagData.Entry entry = PlayerTagRepo.getTag(ign);
        if (entry == null) return;

        // Build the tag string: §b[DEV] §c✦§r
        String tagStr = entry.buildTag();

        // Rebuild the chat line with tag injected right before the IGN:
        // <all prefix junk>§b[DEV] §c✦§r kanishka007: message
        if (entry.hover != null && !entry.hover.isEmpty()) {
            // Split into three sibling components so the hover only applies to the tag
            IChatComponent prefixComp = new ChatComponentText(prefix);
            IChatComponent tagComp    = new ChatComponentText(tagStr);
            IChatComponent nameComp   = new ChatComponentText(ign + tail);

            // hover text supports § colors written in the JSON
            tagComp.getChatStyle().setChatHoverEvent(
                new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ChatComponentText(entry.hover)
                )
            );

            prefixComp.appendSibling(tagComp);
            prefixComp.appendSibling(nameComp);
            event.message = prefixComp;
        } else {
            // No hover — just a plain string replacement
            event.message = new ChatComponentText(prefix + tagStr + ign + tail);
        }
    }
    public PlayerTagListener() {
        System.out.println("PlayerTagListener loaded");
    }
}
