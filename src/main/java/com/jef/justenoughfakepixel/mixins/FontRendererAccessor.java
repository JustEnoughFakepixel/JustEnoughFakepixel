package com.jef.justenoughfakepixel.mixins;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FontRenderer.class)
public interface FontRendererAccessor {

    @Accessor("posX")
    float jef$getPosX();

    @Accessor("posY")
    float jef$getPosY();
}
