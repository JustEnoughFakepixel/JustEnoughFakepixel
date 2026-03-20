package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.events.GuiContainerRenderButtonsEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class MixinGuiContainer_InvButtons {

    @Inject(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/inventory/GuiContainer;func_146979_b(II)V",
                    shift = At.Shift.AFTER
            )
    )
    public void afterDrawForeground(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(
                new GuiContainerRenderButtonsEvent((GuiContainer)(Object)this, mouseX, mouseY));
    }
}