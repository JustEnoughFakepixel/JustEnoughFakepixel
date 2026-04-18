package com.jef.justenoughfakepixel.features.capes;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.command.SimpleCommand;
import com.jef.justenoughfakepixel.features.capes.ui.CapeSelectorGUI;
import com.jef.justenoughfakepixel.init.RegisterCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@RegisterCommand
public class CapeMenuCommand extends SimpleCommand {
    @Override
    public String getName() {
        return "capes";
    }

    @Override
    public String getUsage() {
        return "/" + getName();
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof EntityPlayer)) return;
        JefConfig.screenToOpen = new CapeSelectorGUI();
    }
}
