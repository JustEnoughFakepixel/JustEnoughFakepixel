package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.events.SignSubmitEvent;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiEditSign.class)
public class MixinGuiEditSign {

    @Redirect(method = "onGuiClosed", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/tileentity/TileEntitySign;signText:[Lnet/minecraft/util/IChatComponent;"))
    public IChatComponent[] onSignSubmit(TileEntitySign instance) {
        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = instance.signText[i].getUnformattedText();
        }
        
        SignSubmitEvent event = new SignSubmitEvent((GuiEditSign) (Object) this, lines);
        MinecraftForge.EVENT_BUS.post(event);
        
        IChatComponent[] result = new IChatComponent[4];
        for (int i = 0; i < 4; i++) {
            result[i] = new ChatComponentText(event.lines[i]);
        }
        return result;
    }
}
