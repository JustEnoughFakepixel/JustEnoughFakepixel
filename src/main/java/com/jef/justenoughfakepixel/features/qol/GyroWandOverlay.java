package com.jef.justenoughfakepixel.features.qol;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.overlay.Overlay;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@RegisterEvents
public class GyroWandOverlay extends Overlay {

    private static final long COOLDOWN_MS = 30_000L;

    @Getter
    private static GyroWandOverlay instance;
    private long lastUsedMs = 0;

    public GyroWandOverlay() {
        super(80, 12);
        instance = this;
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.qol.gyroWandPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.qol.gyroWandScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.qol.gyroWandBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.qol.gyroWandCornerRadius;
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature.qol.gyroWandTimer && (GyroWandHelper.isHoldingGyroStatic() || JefConfig.feature.qol.gyroWandTimerAlways);
    }

    @Override
    public List<String> getLines(boolean preview) {
        if (preview) return Collections.singletonList("§cGyro: §f5s");
        if (!isOnCooldown()) return Collections.emptyList();
        long remaining = (COOLDOWN_MS - (System.currentTimeMillis() - lastUsedMs)) / 1000 + 1;
        return Collections.singletonList("§cGyro: §f" + remaining + "s");
    }

    public void markUsed() {
        lastUsedMs = System.currentTimeMillis();
    }

    public boolean isOnCooldown() {
        return (System.currentTimeMillis() - lastUsedMs) < COOLDOWN_MS;
    }
}