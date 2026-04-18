package com.jef.justenoughfakepixel.core.features;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;

public class Cosmetics {

    @Expose
    @ConfigOption(name = "Capes", desc = "Settings for the Capes")
    @ConfigEditorAccordion(id = 0)
    public boolean capesAccordian = false;

    @Expose
    @ConfigOption(name = "Enable", desc = "Enable/Disable whether you use your custom cape")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 0)
    public boolean capesEnabled = true;

    @Expose
    @ConfigOption(name = "Reload Capes", desc = "Reload and refetch all cape textures.")
    @ConfigEditorButton(runnableId = "reloadCapes",buttonText = "Reload")
    @ConfigAccordionId(id = 0)
    public String reloadCapes = "";

}
