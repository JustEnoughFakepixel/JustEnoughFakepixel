package com.jef.justenoughfakepixel.core;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.features.*;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;

public class Config {

    @Expose
    @Category(name = "General", desc = "General features")
    public final General general = new General();

    @Expose
    @Category(name = "Misc", desc = "Misc features")
    public final Misc misc = new Misc();

    @Expose
    @Category(name = "Waypoints", desc = "Waypoints config & GUI")
    public final Waypoints waypoints = new Waypoints();

    @Expose
    @Category(name = "Mining", desc = "Mining features")
    public final Mining mining = new Mining();

    @Expose
    @Category(name = "Diana", desc = "Diana event tracking & overlays")
    public final Diana diana = new Diana();

    @Expose
    @Category(name = "Dungeons", desc = "Dungeon features")
    public final Dungeons dungeons = new Dungeons();

    public void executeRunnable(String runnableId) {
        switch (runnableId) {
            case "openWaypointGroupGui":  JefConfig.openWaypointGroupGui();   break;
            case "openStatsEditor":       JefConfig.openStatsEditor();        break;
            case "openHudEditor":         JefConfig.openHudEditor();          break;
            case "openFetchurEditor":     JefConfig.openFetchurEditor();      break;
            case "openDianaEventEditor":  JefConfig.openDianaEventEditor();   break;
            case "openDianaLootEditor":   JefConfig.openDianaLootEditor();    break;
        }
    }
}