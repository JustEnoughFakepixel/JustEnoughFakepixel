package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.features.price.ahparser.parser.AuctionParser;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiContainer.class)
public class MixinGuiContainer_AuctionParser extends GuiScreen {

    @Shadow public int guiLeft;
    @Shadow public int guiTop;
    @Shadow public Container inventorySlots;

    @Unique public GuiButton justEnoughfakepixel$button;
    @Unique public GuiButton justEnoughfakepixel$nextCatButton;

    @Inject(method = "initGui",at = @At("RETURN"))
    public void initGui(CallbackInfo ci) {
        Container container = this.inventorySlots;
        if (container instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest) container;
            if ("Auction Browser".equals(chest.getLowerChestInventory().getName())) {

                // Existing Start Button
                this.justEnoughfakepixel$button = new GuiButton(1000,
                        this.guiLeft - 200,
                        this.guiTop,
                        130,20,
                        "Start Parsing [Interactive]");
//                this.buttonList.add(justEnoughfakepixel$button);

                // New Skip/Next Category Button (Placed slightly below)
                this.justEnoughfakepixel$nextCatButton = new GuiButton(1001,
                        this.guiLeft - 200,
                        this.guiTop + 25,
                        130, 20,
                        "Skip to Next Category");
//                this.buttonList.add(justEnoughfakepixel$nextCatButton);
            }
        }
    }

    @Inject(method = "drawScreen",at = @At("RETURN"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if(AuctionParser.highlightSlot < 0) return;
        Container container = this.inventorySlots;
        if (container instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest) container;
            if("Auction Browser".equals(chest.getLowerChestInventory().getName())) {
                Slot slot = null;
                for (Slot obj : container.inventorySlots) {
                    if (obj.slotNumber == AuctionParser.highlightSlot) {
                        slot = obj;
                        break;
                    }
                }
                if (slot == null) return;

                int x = this.guiLeft + slot.xDisplayPosition;
                int y = this.guiTop  + slot.yDisplayPosition;

                long time      = System.currentTimeMillis();
                float phase    = (time % 1000) / 800f;
                float alpha = 0.675f + 0.325f * (float) Math.sin(phase * 2 * Math.PI);
                int  alphaInt = (int) (alpha * 255);

                int color = new Color(0,255,0,alphaInt).getRGB();
                drawRect(x, y, x + 16, y + 16, color);

                int borderColor = 0xFFFFFF55;
                drawRect(x,      y,      x + 16, y + 1,  borderColor);
                drawRect(x,      y + 15, x + 16, y + 16, borderColor);
                drawRect(x,      y,      x + 1,  y + 16, borderColor);
                drawRect(x + 15, y,      x + 16, y + 16, borderColor);
            }
        }
    }

    @Inject(method = "mouseReleased",at = @At("HEAD"))
    public void mouseReleased(int mouseX, int mouseY, int state, CallbackInfo ci) {
        // Handle Start Click
        if (justEnoughfakepixel$button != null &&
                mouseX > justEnoughfakepixel$button.xPosition && mouseX < justEnoughfakepixel$button.xPosition + justEnoughfakepixel$button.width &&
                mouseY > justEnoughfakepixel$button.yPosition && mouseY < justEnoughfakepixel$button.yPosition + justEnoughfakepixel$button.height) {

            AuctionParser.startParsingSequence((ContainerChest)this.inventorySlots);
        }

        // Handle Skip Click
        if (justEnoughfakepixel$nextCatButton != null &&
                mouseX > justEnoughfakepixel$nextCatButton.xPosition && mouseX < justEnoughfakepixel$nextCatButton.xPosition + justEnoughfakepixel$nextCatButton.width &&
                mouseY > justEnoughfakepixel$nextCatButton.yPosition && mouseY < justEnoughfakepixel$nextCatButton.yPosition + justEnoughfakepixel$nextCatButton.height) {

            AuctionParser.forceNextCategory((ContainerChest)this.inventorySlots);
        }
    }
}