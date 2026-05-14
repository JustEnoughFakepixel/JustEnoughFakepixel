package com.jef.justenoughfakepixel.features.misc.protect

import com.jef.justenoughfakepixel.core.config.command.SimpleCommand
import com.jef.justenoughfakepixel.init.RegisterCommand
import com.jef.justenoughfakepixel.utils.item.ItemUtils
import net.minecraft.client.Minecraft
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting

@RegisterCommand
class ProtectItemCommand : SimpleCommand() {

    private companion object {
        private val mc = Minecraft.getMinecraft()
        private const val PREFIX = "§b[JEF] §r"
    }

    override fun getName() = "jefprotect"
    override fun getUsage() = "/jefprotect [list|clear]"

    override fun execute(sender: ICommandSender, args: Array<String>) {
        val player = mc.thePlayer ?: return

        if (args.isNotEmpty()) {
            when (args[0].lowercase()) {
                "list" -> handleListCommand(player)
                "clear" -> handleClearCommand(player)
                else -> showUsage(player)
            }
            return
        }

        handleToggleProtection(player)
    }

    override fun addTabCompletionOptions(sender: ICommandSender, args: Array<String>, pos: BlockPos): List<String> {
        return if (args.size == 1) listOf("list", "clear") else emptyList()
    }


    private fun handleListCommand(player: EntityPlayer) {
        val uuids = ProtectedItemStorage.protectedUuids
        if (uuids.isEmpty()) {
            player.addChatMessage(ChatComponentText("$PREFIX${EnumChatFormatting.YELLOW}No protected items."))
        } else {
            player.addChatMessage(ChatComponentText("$PREFIX${EnumChatFormatting.GREEN}Protected items (${uuids.size}):"))
            uuids.forEach { uuid ->
                player.addChatMessage(ChatComponentText("  §7- $uuid"))
            }
        }
    }

    private fun handleClearCommand(player: EntityPlayer) {
        val count = ProtectedItemStorage.protectedUuids.size
        ProtectedItemStorage.protectedUuids.clear()
        ProtectedItemStorage.save()
        player.addChatMessage(
            ChatComponentText("$PREFIX${EnumChatFormatting.GREEN}Cleared $count protected item(s).")
        )
    }

    private fun handleToggleProtection(player: EntityPlayer) {
        val held = player.heldItem
        if (held == null) {
            player.addChatMessage(
                ChatComponentText("$PREFIX${EnumChatFormatting.RED}You are not holding an item!")
            )
            return
        }

        val uuid = ItemUtils.getItemUuid(held)
        if (uuid == null) {
            player.addChatMessage(
                ChatComponentText(
                    "$PREFIX${EnumChatFormatting.RED}This item has no SkyBlock UUID and cannot be protected."
                )
            )
            return
        }

        if (ProtectedItemStorage.contains(uuid)) {
            ProtectedItemStorage.remove(uuid)
            player.addChatMessage(
                ChatComponentText(
                    "$PREFIX${EnumChatFormatting.YELLOW}${held.displayName} " + "${EnumChatFormatting.GRAY}is no longer protected."
                )
            )
        } else {
            ProtectedItemStorage.add(uuid)
            player.addChatMessage(
                ChatComponentText(
                    "$PREFIX${EnumChatFormatting.GREEN}${held.displayName} " + "${EnumChatFormatting.GRAY}is now protected!"
                )
            )
        }
    }

    private fun showUsage(player: EntityPlayer) {
        player.addChatMessage(
            ChatComponentText("$PREFIX${EnumChatFormatting.RED}Usage: ${getUsage()}")
        )
    }
}