package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.features.storage.StorageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiContainer.class)
public class MixinGuiContainer_StorageOverlay {

    @Inject(method = "isMouseOverSlot", at = @At("HEAD"), cancellable = true)
    public void isMouseOverSlot(Slot slotIn, int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir) {
        StorageManager.overrideIsMouseOverSlot(slotIn, mouseX, mouseY, cir);
    }

    @Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
    public void drawSlot(Slot slot, CallbackInfo ci) {
        if (StorageManager.isOverlayActive() && Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
            ci.cancel();
        }
    }
}