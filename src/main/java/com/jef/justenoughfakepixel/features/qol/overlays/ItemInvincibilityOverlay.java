package com.jef.justenoughfakepixel.features.qol.overlays;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.utils.overlay.TimerOverlay;
import com.jef.justenoughfakepixel.features.qol.timers.ItemInvincibilityTimers;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import lombok.Getter;
import net.minecraft.item.ItemStack;

import java.util.List;

@RegisterEvents
public class ItemInvincibilityOverlay extends TimerOverlay {

    @Getter
    private static ItemInvincibilityOverlay instance;

    public ItemInvincibilityOverlay() {
        super();
        instance = this;
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.qol.itemInvincibilityPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.qol.itemInvincibilityScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.qol.itemInvincibilityBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.qol.itemInvincibilityCornerRadius;
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature != null && JefConfig.feature.qol.itemInvincibilityOverlay;
    }

    @Override
    protected String getHeaderText() {
        return "§b§lInvincibility";
    }

    @Override
    protected List<String> getActiveTimers() {
        return ItemInvincibilityTimers.getActiveTimers();
    }

    @Override
    protected ItemStack findItemStack(String id) {
        return ItemInvincibilityTimers.findItemStack(id);
    }

    @Override
    protected long getRemainingMs(String id) {
        return ItemInvincibilityTimers.getRemainingMs(id);
    }

    @Override
    protected boolean shouldShowWhenEmpty() {
        return JefConfig.feature != null && JefConfig.feature.qol.itemInvincibilityShowWhenEmpty;
    }

    @Override
    protected String getPreviewItemName() {
        return "§5Bonzo's Mask";
    }
}