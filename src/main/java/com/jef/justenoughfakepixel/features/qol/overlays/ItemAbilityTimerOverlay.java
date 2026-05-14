package com.jef.justenoughfakepixel.features.qol.overlays;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.utils.overlay.TimerOverlay;
import com.jef.justenoughfakepixel.features.qol.timers.ItemAbilityTimers;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import lombok.Getter;
import net.minecraft.item.ItemStack;

import java.util.List;

@RegisterEvents
public class ItemAbilityTimerOverlay extends TimerOverlay {

    @Getter
    private static ItemAbilityTimerOverlay instance;

    public ItemAbilityTimerOverlay() {
        super();
        instance = this;
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.qol.abilityTimer.itemAbilityTimerPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.qol.abilityTimer.itemAbilityTimerScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.qol.abilityTimer.itemAbilityTimerBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.qol.abilityTimer.itemAbilityTimerCornerRadius;
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature != null && JefConfig.feature.qol.abilityTimer.itemAbilityTimerOverlay;
    }

    @Override
    protected String getHeaderText() {
        return "§b§lAbility Timers";
    }

    @Override
    protected List<String> getActiveTimers() {
        return ItemAbilityTimers.getActiveTimers();
    }

    @Override
    protected ItemStack findItemStack(String id) {
        return ItemAbilityTimers.findItemStack(id);
    }

    @Override
    protected long getRemainingMs(String id) {
        return ItemAbilityTimers.getRemainingMs(id);
    }

    @Override
    protected boolean shouldShowWhenEmpty() {
        return JefConfig.feature != null && JefConfig.feature.qol.abilityTimer.itemAbilityTimerShowWhenEmpty;
    }

    @Override
    protected String getPreviewItemName() {
        return "§6Fire Veil";
    }
}