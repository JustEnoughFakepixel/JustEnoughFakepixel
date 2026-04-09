package com.jef.justenoughfakepixel.features.mining.powder;

import com.jef.justenoughfakepixel.core.config.command.SimpleCommand;
import com.jef.justenoughfakepixel.init.RegisterCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RegisterCommand
public class PowderCommand extends SimpleCommand {

    private static final String PREFIX = EnumChatFormatting.AQUA + "[Powder] " + EnumChatFormatting.RESET;

    @Override
    public String getName() {
        return "powdertracker";
    }

    @Override
    public String getUsage() {
        return "/powdertracker <reset|toggle>";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("pdt");
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.YELLOW + "Usage: /pdt <reset|toggle>"));
            return;
        }

        PowderStats stats = PowderStats.getInstance();

        switch (args[0].toLowerCase()) {
            case "reset":
                stats.reset();
                sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.GREEN + "Powder tracker data has been reset."));
                break;

            case "toggle":
                boolean now = stats.toggleTracking();
                sender.addChatMessage(new ChatComponentText(PREFIX + (now ? EnumChatFormatting.GREEN + "Tracker enabled." : EnumChatFormatting.RED + "Tracker paused.")));
                break;

            default:
                sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.RED + "Unknown subcommand. Use: reset, toggle"));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) return Arrays.asList("reset", "toggle");
        return Collections.emptyList();
    }
}