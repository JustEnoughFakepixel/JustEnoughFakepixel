package com.jef.justenoughfakepixel.core.features.fishing;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.core.features.fishing.*;

public class Fishing {

    @Expose
    @Category(name = "Trophy Fish", desc = "Trophy fish tracking and display")
    public TrophyFishConfig trophyFish = new TrophyFishConfig();

    @Expose
    @Category(name = "Fishing Timer", desc = "Fishing timer overlay settings")
    public FishingTimerConfig fishingTimerConfig = new FishingTimerConfig();
}
