package com.jef.justenoughfakepixel.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MixinMinecraft {

    @Accessor("session")
    void setSession(Session session);

    @Accessor("session")
    Session getSession();

}
