package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.features.misc.SearchBar;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public class MixinRenderItem_SearchHighlight {

    @Inject(method = "renderItemIntoGUI(Lnet/minecraft/item/ItemStack;II)V", at = @At("TAIL"))
    private void jef$renderSearchHighlight(ItemStack itemStack, int x, int y, CallbackInfo ci) {
        SearchBar.renderHighlightForItem(itemStack, x, y);
    }
}
