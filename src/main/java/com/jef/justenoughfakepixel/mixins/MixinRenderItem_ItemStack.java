package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.events.RenderItemOverlayEvent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public class MixinRenderItem_ItemStack {

    @Inject(method = "renderItemOverlayIntoGUI", at = @At("TAIL"))
    private void jef$onItemOverlay(FontRenderer fr, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (stack != null)
            MinecraftForge.EVENT_BUS.post(new RenderItemOverlayEvent(stack, x, y));
    }
}