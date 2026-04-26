package com.jef.justenoughfakepixel.core.features.mining;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.core.features.mining.*;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mining {

    @Expose
    @Category(name = "Fetchur Overlay", desc = "Settings for the Fetchur item overlay")
    public FetchurConfig fetchur = new FetchurConfig();

    @Expose
    @Category(name = "Powder Tracker", desc = "Tracks gemstone powder and chest drops in Crystal Hollows")
    public PowderTrackerConfig powderTrackerConfig = new PowderTrackerConfig();

    @Expose
    @Category(name = "/hotm Powder Display", desc = "Powder cost info on HOTM perk tooltips")
    public HotmPowderConfig hotmPowder = new HotmPowderConfig();

    // ── flat fields kept for runtime code compatibility ──────────────────────
    @Expose public boolean showFetchurOverlay = true;
    @Expose public String overlayBgColor = "0:136:0:0:0";
    @Expose public int overlayCornerRadius = 4;
    @Expose public float fetchurOverlayScale = 1f;
    @Expose public Position fetchurOverlayPos = new Position(4, 4);

    @Expose public boolean powderTracker = true;
    @Expose public int powderToggleKey = Keyboard.KEY_NONE;
    @Expose public String powderBgColor = "0:136:0:0:0";
    @Expose public int powderCornerRadius = 4;
    @Expose public float powderOverlayScale = 1f;
    @Expose public List<Integer> powderDisplayLines = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23));
    @Expose public Position powderOverlayPos = new Position(4, 60);

    @Expose public boolean hotmPowderSpent = true;
    @Expose public int hotmPowderSpentDesign = 0;
    @Expose public boolean hotmPowderFor10Levels = true;
}
