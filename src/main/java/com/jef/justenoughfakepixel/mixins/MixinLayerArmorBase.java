package com.jef.justenoughfakepixel.mixins;

// Ported from https://github.com/Polyfrost/PolyPatcher/blob/main/src/main/java/club/sk1er/patcher/mixins/features/disableenchantglint/LayerItemBaseMixin_DisableEnchantGlint.java

import com.jef.justenoughfakepixel.core.JefConfig;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {

    @Inject(method = "renderGlint", at = @At("HEAD"), cancellable = true)
    private void jef$disableEnchantGlint(CallbackInfo ci) {
        if (JefConfig.feature != null && JefConfig.feature.qol.disableEnchantGlint)
            ci.cancel();
    }
}