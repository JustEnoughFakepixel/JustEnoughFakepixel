package com.jef.justenoughfakepixel.features.diana.overlays;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.features.diana.LootshareDetect;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.overlay.Overlay;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RegisterEvents
public class DianaMobHealthOverlay extends Overlay {

    @Getter
    private static DianaMobHealthOverlay instance;

    public DianaMobHealthOverlay() {
        super(180, LINE_HEIGHT + PADDING * 2);
        instance = this;
    }

    @Override
    protected int getBaseWidth() {
        return 180;
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.diana.dianaMobHealthPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.diana.mobScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.diana.mobBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.diana.mobCornerRadius;
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature.diana.enabled && JefConfig.feature.diana.showDianaMobHealthOverlay;
    }

    @Override
    public List<String> getLines(boolean preview) {
        if (preview) return Collections.singletonList("§2[Lv260] ✤✿ Gaia Construct 839.6k§f/§a1.5M§c❤");

        String raw = LootshareDetect.getClosestNonInqMobName();
        if (raw == null) return new ArrayList<>();
        return Collections.singletonList(raw);
    }
}