package com.jef.justenoughfakepixel.features.mining;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.utils.JefOverlay;
import com.jef.justenoughfakepixel.utils.ScoreboardUtils;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collections;
import java.util.List;

public class FetchurOverlay extends JefOverlay {

    private static FetchurOverlay instance;

    public FetchurOverlay() {
        super(160, LINE_HEIGHT + PADDING * 2);
        instance = this;
    }

    public static FetchurOverlay getInstance() { return instance; }

    @Override protected int     getBaseWidth()   { return 160; }
    @Override public Position   getPosition()    { return JefConfig.feature.mining.fetchurOverlayPos; }
    @Override public float      getScale()       { return JefConfig.feature.mining.fetchurOverlayScale; }
    @Override public boolean    showBackground() { return JefConfig.feature.mining.overlayBackground; }
    @Override protected boolean extraGuard()     { return ScoreboardUtils.isOnSkyblock(); }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (JefConfig.feature == null || !JefConfig.feature.mining.showFetchurOverlay) return;
        render(false);
    }

    @Override
    public List<String> getLines(boolean preview) {
        String item = preview ? "Yellow Stained Glass x20" : FetchurHelper.getTodaysItem();
        return Collections.singletonList(
                EnumChatFormatting.GOLD + "Fetchur: " + EnumChatFormatting.YELLOW + item);
    }
}