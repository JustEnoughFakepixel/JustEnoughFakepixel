package com.jef.justenoughfakepixel.features.storage;

import com.jef.justenoughfakepixel.core.JefConfig;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.awt.*;


public class StorageOverlay extends GuiScreen {

    private double SCROLL_SPEED = 100;

    public double scrollOffset = 0;

    @Override
    public void initGui() {
        SCROLL_SPEED = 100 * (JefConfig.feature.storage.scrollSpeed);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(5,5,100,100,new Color(0,0,0,100).getRGB());
        double offset = this.scrollOffset;
        if(!JefConfig.feature.storage.smoothScroll){
            offset = Math.round(this.scrollOffset);
        }

        this.drawCenteredString(mc.fontRendererObj,"Offset: " + offset,
                53,52,new Color(255,255,255).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() {
        int scroll = Mouse.getDWheel();
        if(scroll != 0){
            this.scrollOffset += SCROLL_SPEED/scroll;
        }
    }
}
