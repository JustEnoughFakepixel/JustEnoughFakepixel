package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.features.PlayerSizeRepo;
import com.jef.justenoughfakepixel.repo.data.PlayerSizeData;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {

    @Inject(method = "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V",
            at = @At("TAIL"))
    private void jef$repoPlayerScale(AbstractClientPlayer player, float partialTicks, CallbackInfo ci) {
        PlayerSizeData data = PlayerSizeRepo.getScale(player.getName());
        if (data == null) return;
        float x = data.x(), y = data.y(), z = data.z();
        if (x == 0 || y == 0 || z == 0) return;
        if (y < 0) GlStateManager.translate(0f, y * 2, 0f);
        GlStateManager.scale(x, y, z);
    }
}