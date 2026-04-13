package com.jef.justenoughfakepixel.features.qol.overlays;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.utils.overlay.TimerOverlay;
import com.jef.justenoughfakepixel.features.qol.timers.ItemCooldowns;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import lombok.Getter;
import net.minecraft.item.ItemStack;

import java.util.List;

@RegisterEvents
public class ItemCooldownOverlay extends TimerOverlay {

    @Getter
    private static ItemCooldownOverlay instance;

    public ItemCooldownOverlay() {
        super();
        instance = this;
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.qol.itemCooldownPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.qol.itemCooldownScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.qol.itemCooldownBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.qol.itemCooldownCornerRadius;
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature != null && JefConfig.feature.qol.itemCooldownOverlay;
    }

    @Override
    protected String getHeaderText() {
        return "§b§lCooldowns";
    }

    @Override
    protected List<String> getActiveTimers() {
        return ItemCooldowns.getActiveCooldowns();
    }

    @Override
    protected ItemStack findItemStack(String id) {
        return ItemCooldowns.findItemStack(id);
    }

    @Override
    protected long getRemainingMs(String id) {
        return ItemCooldowns.getRemainingMs(id);
    }

    @Override
    protected boolean shouldShowWhenEmpty() {
        return JefConfig.feature != null && JefConfig.feature.qol.itemCooldownShowWhenEmpty;
    }

    @Override
    protected String getPreviewItemName() {
        return "§5Example Item";
    }
}