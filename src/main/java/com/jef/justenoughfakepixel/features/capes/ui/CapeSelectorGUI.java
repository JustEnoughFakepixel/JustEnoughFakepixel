package com.jef.justenoughfakepixel.features.capes.ui;

import com.jef.justenoughfakepixel.features.capes.CapeManager;
import com.jef.justenoughfakepixel.utils.render.NineSliceUtils;
import com.jef.justenoughfakepixel.utils.render.ResolutionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CapeSelectorGUI extends GuiScreen {

    public List<CapeDisplay> capes = new ArrayList<>();
    private static final ResourceLocation CONTAINER_BG =
            new ResourceLocation("justenoughfakepixel",
                    "textures/gui/storage_container_bg.png");

    @Override
    public void initGui() {
        super.initGui();
        capes.clear();

        CapeManager.capes.values().forEach(val -> capes.add(new CapeDisplay(val)));
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int boxW = (int)ResolutionUtils.getXStatic(900);
        int boxH = (int)ResolutionUtils.getYStatic(350);
        int boxX =  (this.width / 2) - (boxW / 2);
        int boxY =  (this.height / 2) - (boxH / 2);
        NineSliceUtils.draw(CONTAINER_BG,boxX,boxY,boxW,boxH,6,18);

        int PADDING = 5;

        AtomicInteger xPos = new AtomicInteger(boxX + PADDING);
        AtomicInteger yPos = new AtomicInteger(boxY + PADDING);

        capes.forEach(cape ->{
            if(xPos.get() > boxX + boxW) { return; }
        cape.draw(xPos.get(), yPos.get(),
                isHovering(mouseX,mouseY, xPos.get(), yPos.get(),cape.width,cape.height),mc);
            xPos.addAndGet(cape.width + PADDING);
        });

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public boolean isHovering(int mouseX, int mouseY, int xPos, int yPos, int width, int height) {
        return mouseX > xPos && mouseX < xPos + width && mouseY > yPos && mouseY < yPos + height;
    }


    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        capes.forEach(cape -> {
            if(cape.xPos >= 0 && cape.yPos >= 0){
                if(isHovering(mouseX,mouseY,cape.xPos,cape.yPos,cape.width,cape.height)){
                    CapeManager.applyCape(Minecraft.getMinecraft().thePlayer.getGameProfile().getName(),
                            CapeManager.getCape(cape.capeID));
                }
            }
        });
        super.mouseReleased(mouseX, mouseY, state);
    }
}
