package com.jef.justenoughfakepixel.features.capes;

import net.minecraft.util.ResourceLocation;

public class Cape {

    public String id,name;
    public ResourceLocation texture;

    public  Cape(String id, String name, ResourceLocation texture) {
        this.id = id;
        this.name = name;
        this.texture = texture;
    }

}
