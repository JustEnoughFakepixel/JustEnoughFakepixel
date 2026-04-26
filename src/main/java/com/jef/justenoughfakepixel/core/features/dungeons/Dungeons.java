package com.jef.justenoughfakepixel.core.features.dungeons;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.core.features.dungeons.*;

import java.util.HashMap;
import java.util.Map;

public class Dungeons {

    @Expose
    @ConfigOption(name = "Blood Mob Highlight", desc = "Highlight blood room mobs. Box = bounding box, Outline = body glow, Off = disabled")
    @ConfigEditorDropdown(values = {"Box", "Outline", "Off"})
    public int bloodMobHighlight = 2;

    @Expose
    @ConfigOption(name = "Blood Mob Color", desc = "Color used for blood mob box and outline highlight")
    @ConfigEditorColour
    public String bloodMobColor = "200:255:50:50:255";

    @Expose
    @Category(name = "Boss Highlights", desc = "Highlight dungeon bosses and their minions")
    public BossHighlightConfig bossHighlight = new BossHighlightConfig();

    @Expose
    @Category(name = "Splits Overlay", desc = "Run timers, end-of-run stats and overlay settings")
    public DungeonOverlayConfig dungeonOverlay = new DungeonOverlayConfig();

    @Expose
    @Category(name = "D.Breaker Overlay", desc = "Shows Dungeon Breaker charges while in dungeons")
    public DungeonBreakerConfig dungeonBreaker = new DungeonBreakerConfig();

    @Expose
    @Category(name = "D.Room Overlay", desc = "Shows the name of your current dungeon room on screen")
    public DungeonRoomOverlayConfig dungeonRoomOverlayConfig = new DungeonRoomOverlayConfig();

    @Expose
    @Category(name = "Chest Case Opening", desc = "CS:GO style animation when opening dungeon chests")
    public CaseOpeningConfig caseOpening = new CaseOpeningConfig();

    // ── flat fields kept for runtime code compatibility ──────────────────────
    @Expose public int bonzoHighlight = 2;
    @Expose public String bonzoColor = "200:255:140:0:255";
    @Expose public int scarfHighlight = 2;
    @Expose public String scarfColor = "200:180:0:255:255";
    @Expose public int scarfMinionHighlight = 2;
    @Expose public String scarfMinionColor = "150:180:0:200:255";
    @Expose public int professorHighlight = 2;
    @Expose public String professorColor = "200:0:200:255:255";

    @Expose public boolean dungeonStats = false;
    @Expose public boolean dungeonStatsShowAll = true;
    @Expose public String statsBgColor = "0:136:0:0:0";
    @Expose public int statsCornerRadius = 4;
    @Expose public float statsScale = 1f;
    @Expose public Position statsPos = new Position(4, 100);

    @Expose public boolean dungeonRoomOverlay = false;
    @Expose public boolean dungeonBreakerOverlay = false;
    @Expose public String dungeonBreakerBgColor = "0:0:0:0:0";
    @Expose public int dungeonBreakerCornerRadius = 4;
    @Expose public float dungeonBreakerScale = 1f;
    @Expose public Position dungeonBreakerPos = new Position(4, 120);

    
    @Expose public String dungeonRoomOverlayBgColor = "0:0:0:0:0";
    @Expose public int dungeonRoomOverlayCornerRadius = 4;
    @Expose public float dungeonRoomOverlayScale = 1f;
    @Expose public Position dungeonRoomOverlayPos = new Position(4, 140);

    @Expose public boolean caseOpeningAnimation = false;
    @Expose public boolean caseOpeningAllowText = true;
    @Expose public float caseOpeningTextScale = 0.5f;
    @Expose public float caseOpeningSlowTime = 3f;
    @Expose public int caseOpeningSlowDistance = 8;

    @Expose public Map<String, Long> floorPbs = new HashMap<>();

    public long getPb(String key) { Long v = floorPbs.get(key); return v == null ? 0L : v; }
    public void setPb(String key, long ms) { floorPbs.put(key, ms); }
}
