package com.jef.justenoughfakepixel.mixins;

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

    @Inject(method = "getLocationCape",at = @At("HEAD"), cancellable = true)
    private void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir){
        String user = this.playerInfo.getGameProfile().getName();

        if(user == null || user.isEmpty()){cir.cancel();return;}
        if(!CapeManager.hasCape(user)){ cir.cancel();return;}

        Cape cape = CapeManager.getCapeForPlayer(user);

        if(cape == null){cir.cancel();return;}

        cir.setReturnValue(cape.texture);
    }

}
