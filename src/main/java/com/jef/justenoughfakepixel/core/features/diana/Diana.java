package com.jef.justenoughfakepixel.core.features.diana;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.core.features.diana.*;

public class Diana {

    @Expose
    @ConfigOption(name = "Diana Tracker", desc = "Enables tracking")
    @ConfigEditorBoolean
    public boolean enabled = true;

    @Expose
    @ConfigOption(name = "Edit Overlay Positions", desc = "Drag all Diana overlays to reposition them individually")
    @ConfigEditorButton(runnableId = "openDianaOverlayEditor", buttonText = "Edit")
    public boolean editOverlayPosDummy = false;

    @Expose
    @Category(name = "Event Overlay", desc = "Diana Event HUD – playtime, burrows, and mob rates")
    public EventOverlayConfig eventOverlay = new EventOverlayConfig();

    @Expose
    @Category(name = "Loot Overlay", desc = "Diana Loot HUD – chimeras, rare drops, and coins")
    public LootOverlayConfig lootOverlay = new LootOverlayConfig();

    @Expose
    @Category(name = "Inquisitor Overlay", desc = "Live HP bar for the nearest Minos Inquisitor")
    public InquisitorHpConfig inquisitorHp = new InquisitorHpConfig();

    @Expose
    @Category(name = "DianaMob Overlay", desc = "Live HP bar for the nearest non-Inquisitor Diana mob")
    public DianaMobHpConfig dianaMobHp = new DianaMobHpConfig();

    // ── flat fields kept for runtime code compatibility ──────────────────────
    @Expose public boolean showEventOverlay = true;
    @Expose public String eventBgColor = "0:136:0:0:0";
    @Expose public int eventCornerRadius = 4;
    @Expose public float eventScale = 1f;
    @Expose public Position eventOverlayPos = new Position(4, 200);

    @Expose public boolean showLootOverlay = true;
    @Expose public String lootBgColor = "0:136:0:0:0";
    @Expose public int lootCornerRadius = 4;
    @Expose public float lootScale = 1f;
    @Expose public Position lootOverlayPos = new Position(4, 310);

    @Expose public boolean showInqHealthOverlay = true;
    @Expose public String inqBgColor = "0:136:0:0:0";
    @Expose public int inqCornerRadius = 4;
    @Expose public float inqScale = 1f;
    @Expose public Position inqHealthPos = new Position(4, 400);

    @Expose public boolean showDianaMobHealthOverlay = true;
    @Expose public String mobBgColor = "0:136:0:0:0";
    @Expose public int mobCornerRadius = 4;
    @Expose public float mobScale = 1f;
    @Expose public Position dianaMobHealthPos = new Position(4, 420);
}
