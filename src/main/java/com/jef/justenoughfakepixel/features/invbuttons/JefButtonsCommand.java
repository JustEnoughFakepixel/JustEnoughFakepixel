package com.jef.justenoughfakepixel.features.invbuttons;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.init.RegisterCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

@RegisterCommand
public class JefButtonsCommand extends CommandBase {
    @Override public String getCommandName() { return "jefbuttons"; }
    @Override public String getCommandUsage(ICommandSender sender) { return "/jefbuttons"; }
    @Override public int getRequiredPermissionLevel() { return 0; }
    @Override public void processCommand(ICommandSender sender, String[] args) {
        JefConfig.openInvButtonEditor();
    }
}
