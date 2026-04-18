package com.jef.justenoughfakepixel.features.capes.ui;

import com.jef.justenoughfakepixel.features.capes.Cape;
import com.jef.justenoughfakepixel.utils.render.NineSliceUtils;
import com.jef.justenoughfakepixel.utils.render.ResolutionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class CapeDisplay {

    public String capeID;
    public int width,height;
    public int xPos = -1, yPos = -1;

    public CapeDisplay(Cape cape){
        this.capeID = cape.id;
        width = (int)(ResolutionUtils.getXStatic(150));
        height = (int)(ResolutionUtils.getYStatic(300));
    }
    public void draw(int xPos, int yPos, boolean hovering, Minecraft mc){
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        NineSliceUtils.draw(new ResourceLocation("justenoughfakepixel",
                "textures/gui/storage_container_bg.png"),xPos,yPos,width,height,
                6,18);
        this.xPos = xPos;
        this.yPos = yPos;

        mc.fontRendererObj.drawString("ID: " + capeID,xPos + 25,yPos + 100,-1);
        if(hovering){
            mc.fontRendererObj.drawString("Hovering", xPos + 25,yPos + 80,-1);
        }
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

}
