package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.features.capes.Cape;
import com.jef.justenoughfakepixel.features.capes.CapeManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer {

    @Shadow
    private NetworkPlayerInfo playerInfo;

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        if (this.playerInfo == null) return;

        String user = this.playerInfo.getGameProfile().getName();
        if (user == null || user.isEmpty()) {  return; }

        if (user.startsWith(CapeManager.MOD_SECRET)) {
            String[] parts = user.split("-", 2);
            if (parts.length == 2) {
                String capeID = parts[1];
                Cape cape = CapeManager.getCape(capeID);
                if (cape == null || !cape.isLoaded()) {  return; }
                JefMod.logger.info("[Mixin] Returning preview cape: " + cape.resourceLocation);
                cir.setReturnValue(cape.resourceLocation);
            }
            return;
        }

        CapeManager.fetchCapeAsync(user);
        if (!CapeManager.hasCape(user)) {  return; }

        Cape cape = CapeManager.getCapeForPlayer(user);
        if (cape == null) {  return; }

        cir.setReturnValue(cape.resourceLocation);
    }

}
