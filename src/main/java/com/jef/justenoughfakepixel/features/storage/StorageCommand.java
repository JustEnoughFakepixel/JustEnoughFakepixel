package com.jef.justenoughfakepixel.features.storage;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.command.SimpleCommand;
import com.jef.justenoughfakepixel.init.RegisterCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class StorageCommand extends SimpleCommand {


    @Override
    public String getName() {
        return "storage";
    }

    @Override
    public String getUsage() {
        return "/" + getName();
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        JefConfig.screenToOpen = new StorageOverlay();
    }
}
