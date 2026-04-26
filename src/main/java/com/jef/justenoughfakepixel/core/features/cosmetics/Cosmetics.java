package com.jef.justenoughfakepixel.core.features.cosmetics;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.features.cosmetics.*;

public class Cosmetics {

    @Expose
    @Category(name = "Capes", desc = "Settings for the Capes")
    public CapesConfig capes = new CapesConfig();

    // ── flat fields kept for runtime code compatibility ──────────────────────
    @Expose public boolean capesEnabled = true;
    @Expose public String reloadCapes = "";
    @Expose public int reloadInterval = 30;
}
