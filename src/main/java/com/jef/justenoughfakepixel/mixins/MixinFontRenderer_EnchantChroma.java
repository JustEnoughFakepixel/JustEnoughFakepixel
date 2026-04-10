package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.features.qol.helpers.EnchantChromaRenderer;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontRenderer.class)
public class MixinFontRenderer_EnchantChroma {

    @Redirect(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Ljava/lang/String;indexOf(I)I"))
    private int jef$interceptFormatCodeRender(String formatCodes, int c) {
        int idx = formatCodes.indexOf(c);
        if (idx == -1 && (c == 'z' || c == 'Z')) {
            EnchantChromaRenderer.onChromaCode();
            return 22;
        }
        if (idx < 16 || idx == 21) EnchantChromaRenderer.onColorCode();
        return idx;
    }

    @Redirect(method = "getStringWidth", at = @At(value = "INVOKE", target = "Ljava/lang/String;indexOf(I)I"))
    private int jef$interceptFormatCodeWidth(String formatCodes, int c) {
        if (c == 'z' || c == 'Z') return 22;
        return formatCodes.indexOf(c);
    }

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