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

    // ── flat fields kept for runtime code compatibility ──────────────────────
    @Expose public boolean trophyOverlay = true;
    @Expose public boolean trophyOnlyCrimson = true;
    @Expose public boolean trophyChatModify = true;
    @Expose public boolean trophyBronzeHider = false;
    @Expose public boolean trophySilverHider = false;
    @Expose public boolean trophyOdgerTotal = true;
    @Expose public String trophyFishBgColor = "160:0:0:0:0";
    @Expose public int trophyFishCornerRadius = 4;
    @Expose public float trophyFishScale = 1f;
    @Expose public Position trophyFishPos = new Position(4, 140);

    @Expose public boolean fishingTimer = true;
    @Expose public int fishingTimerAlertTime = 15;
    @Expose public String fishingTimerNormalColor = "237:255:255:0:0";
    @Expose public String fishingTimerAlertColor = "0:255:255:246:0";
}
