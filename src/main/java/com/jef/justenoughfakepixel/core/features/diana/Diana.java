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
}
