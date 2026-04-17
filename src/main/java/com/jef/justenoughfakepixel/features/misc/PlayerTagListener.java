package com.jef.justenoughfakepixel.features.misc;

import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.repo.PlayerTagRepo;
import com.jef.justenoughfakepixel.repo.data.PlayerTagData;
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
            "^(.*\\s)([A-Za-z0-9_]{1,16})\\s*:\\s*(.+)$"
    );

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {

        String formatted = event.message.getFormattedText();
        String plain = StringUtils.stripControlCodes(formatted);

        Matcher m = CHAT_PATTERN.matcher(plain);
        if (!m.matches()) return;

        String ign = m.group(2);

        PlayerTagData.Entry entry = PlayerTagRepo.getTag(ign);
        if (entry == null) return;

        int plainIgnEnd = plain.indexOf(ign) + ign.length();
        if (plain.indexOf(ign) == -1) return;

        int formattedEnd = mapToFormatted(formatted, plainIgnEnd);
        if (formattedEnd == -1) return;

        String beforeTag = formatted.substring(0, formattedEnd);
        String afterTag  = formatted.substring(formattedEnd);

        String tagStr = " §r" + entry.buildTag() + "§r";

        if (entry.hover != null && !entry.hover.isEmpty()) {
            IChatComponent part1   = new ChatComponentText(beforeTag);
            IChatComponent tagComp = new ChatComponentText(tagStr);
            IChatComponent part2   = new ChatComponentText(afterTag);

            tagComp.getChatStyle().setChatHoverEvent(
                    new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new ChatComponentText(entry.hover)
                    )
            );

            part1.appendSibling(tagComp);
            part1.appendSibling(part2);
            event.message = part1;
        } else {
            event.message = new ChatComponentText(beforeTag + tagStr + afterTag);
        }
    }

    /** Maps a plain-text index (no § codes) to its position in formatted text. */
    private int mapToFormatted(String formatted, int targetPlainIndex) {
        int plainCounter = 0;

        for (int i = 0; i < formatted.length(); i++) {
            if (formatted.charAt(i) == '§') {
                i++;
                continue;
            }

            if (plainCounter == targetPlainIndex) {
                return i;
            }

            plainCounter++;
        }

        if (plainCounter == targetPlainIndex) return formatted.length();

        return -1;
    }
}