package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.events.ItemTossEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP_ItemToss {

    @Inject(method = "dropOneItem", at = @At("HEAD"), cancellable = true)
    private void onDropOneItem(boolean dropAll, CallbackInfoReturnable<EntityItem> cir) {
        EntityPlayerSP self = (EntityPlayerSP) (Object) this;
        ItemStack held = self.inventory.getCurrentItem();
        if (held == null) return;

        ItemTossEvent event = new ItemTossEvent(held, dropAll);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.setReturnValue(null);
        }
    }
}
