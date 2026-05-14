package com.jef.justenoughfakepixel.features.misc

import com.jef.justenoughfakepixel.core.config.command.SimpleCommand
import com.jef.justenoughfakepixel.init.RegisterCommand
import net.minecraft.command.ICommandSender

@RegisterCommand
class PretendThisDoesntExist : SimpleCommand() {
    
    override fun getName() = "jefthisisatestdontusethispls"
    
    override fun getUsage() = "/jefthisisatestdontusethispls"
    
    override fun execute(sender: ICommandSender, args: Array<String>) {
        DVD.forceCornerHit()
    }
}
