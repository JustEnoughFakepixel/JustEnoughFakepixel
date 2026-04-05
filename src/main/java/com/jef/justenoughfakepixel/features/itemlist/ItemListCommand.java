package com.jef.justenoughfakepixel.features.itemlist;

import com.jef.justenoughfakepixel.core.config.command.SimpleCommand;
import com.jef.justenoughfakepixel.features.misc.SearchBar;
import com.jef.justenoughfakepixel.init.RegisterCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@RegisterCommand
public class ItemListCommand extends SimpleCommand {

    @Override
    public String getName() {return "jefitems";
    }

    @Override public String getUsage() {return "/jefitems";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        boolean now = SearchBar.toggleItemListMode();
    }
}