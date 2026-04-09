package com.jef.justenoughfakepixel.features.misc.invbuttons;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.command.SimpleCommand;
import com.jef.justenoughfakepixel.init.RegisterCommand;
import net.minecraft.command.ICommandSender;

@RegisterCommand
public class JefButtonsCommand extends SimpleCommand {
    @Override
    public String getName() {
        return "jefbuttons";
    }

    @Override
    public String getUsage() {
        return "/jefbuttons";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        JefConfig.openInvButtonEditor();
    }
}