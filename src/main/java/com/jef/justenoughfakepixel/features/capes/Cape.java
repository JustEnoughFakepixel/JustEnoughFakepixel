package com.jef.justenoughfakepixel.features.capes;

import net.minecraft.util.ResourceLocation;

public class Cape {
    public final String id;
    public final String name;
    public final String texture;

    public ResourceLocation resourceLocation;

    public Cape(String id, String name, String texture) {
        this.id = id;
        this.name = name;
        this.texture = texture;
    }

    public boolean isLoaded() {
        return resourceLocation != null;
    }
}
