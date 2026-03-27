package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.features.farming.mouse.LockMouse;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHelper.class)
public class MixinMouseHelper_LockMouse {

    @Shadow public int deltaX;
    @Shadow public int deltaY;

    @Inject(method = "mouseXYChange", at = @At("RETURN"))
    private void jef$lockMouse(CallbackInfo ci) {
        if (LockMouse.isLocked() && Minecraft.getMinecraft().currentScreen == null) {
            deltaX = 0;
            deltaY = 0;
        }
    }
}