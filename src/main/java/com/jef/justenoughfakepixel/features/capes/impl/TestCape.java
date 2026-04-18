package com.jef.justenoughfakepixel.features.capes.impl;

import com.jef.justenoughfakepixel.features.capes.Cape;
import net.minecraft.util.ResourceLocation;

public class TestCape extends Cape {

    public TestCape() {
        super("test_cape", "TestCape",
                new ResourceLocation("justenoughfakepixel","capes/test_cape.png"));
    }
}
