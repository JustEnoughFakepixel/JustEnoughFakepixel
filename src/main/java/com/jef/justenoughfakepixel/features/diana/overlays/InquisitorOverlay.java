package com.jef.justenoughfakepixel.features.diana.overlays;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.features.diana.DianaStats;
import com.jef.justenoughfakepixel.features.diana.LootshareDetect;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.overlay.Overlay;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RegisterEvents
public class InquisitorOverlay extends Overlay {
    @Getter
    private static InquisitorOverlay instance;

    public InquisitorOverlay() {
        super(160, LINE_HEIGHT + PADDING * 2);
        instance = this;
    }

    @Override
    protected int getBaseWidth() {
        return 160;
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.diana.inqHealthPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.diana.inqScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.diana.inqBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.diana.inqCornerRadius;
    }

    @Override
    protected boolean extraGuard() {
        return DianaStats.getInstance().isTracking();
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature.diana.enabled && JefConfig.feature.diana.showInqHealthOverlay;
    }

    @Override
    public List<String> getLines(boolean preview) {
        if (preview) return Collections.singletonList("§dMinos Inquisitor §c1,200,000§f/§a2,000,000HP");

        String raw = LootshareDetect.getClosestInqName();
        if (raw == null) return new ArrayList<>();
        return Collections.singletonList(raw);
    }
}