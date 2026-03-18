package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.features.general.EnchantChromaRenderer;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontRenderer.class)
public class MixinFontRenderer_EnchantChroma {

    @Inject(method = "renderStringAtPos", at = @At("HEAD"))
    private void jef$beginRenderString(String text, boolean shadow, CallbackInfo ci) {
        EnchantChromaRenderer.beginRenderString(text, shadow);
    }

    @Inject(method = "renderChar", at = @At("HEAD"))
    private void jef$changeTextColor(char ch, boolean italic, CallbackInfoReturnable<Float> cir) {
        EnchantChromaRenderer.changeTextColor();
    }

    @Inject(method = "renderStringAtPos", at = @At("RETURN"))
    private void jef$endRenderString(String text, boolean shadow, CallbackInfo ci) {
        EnchantChromaRenderer.endRenderString();
    }
}
