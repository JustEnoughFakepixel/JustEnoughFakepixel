package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.JefMod;
import com.jef.justenoughfakepixel.features.price.bzparser.parser.BazaarParser;
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
public class MixinGuiContainer_BazaarParser extends GuiScreen {

    @Shadow
    public int guiLeft;
    @Shadow
    public int guiTop;
    @Shadow
    public Container inventorySlots;
    @Unique
    public GuiButton justEnoughfakepixel$button;

    @Inject(method = "initGui",at = @At("RETURN"))
    public void initGui(CallbackInfo ci) {
        Container container = this.inventorySlots;
        if (container instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest) container;
            JefMod.logger.info(chest.getLowerChestInventory().getName());
            if (chest.getLowerChestInventory().getName().startsWith("Bazaar ➜")) {
                this.justEnoughfakepixel$button = new GuiButton(1000,
                        this.guiLeft - 200,
                        this.guiTop,
                        120,20,
                        "Parse Bazaar Prices [ Interactive, Don't AFK ]");

//                this.buttonList.add(justEnoughfakepixel$button);
            }
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (BazaarParser.highlightSlot < 0) return;
        Container container = this.inventorySlots;
        if (container instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest) container;
            Slot slot = chest.getSlot(BazaarParser.highlightSlot);
            if (slot == null) return;

            int x = this.guiLeft + slot.xDisplayPosition;
            int y = this.guiTop + slot.yDisplayPosition;

            long time = System.currentTimeMillis();
            float phase = (time % 1000) / 1000f;
            float alpha = 0.675f + 0.325f * (float) Math.sin(phase * 2 * Math.PI);
            int color = new Color(0, 255, 0, (int)(alpha * 255)).getRGB();

            drawRect(x, y, x + 16, y + 16, color);
            int borderColor = 0xFFFFFF55;
            drawRect(x, y, x + 16, y + 1, borderColor);
            drawRect(x, y + 15, x + 16, y + 16, borderColor);
            drawRect(x, y, x + 1, y + 16, borderColor);
            drawRect(x + 15, y, x + 16, y + 16, borderColor);
        }
    }


    @Inject(method = "mouseReleased",at = @At("HEAD"))
    public void mouseReleased(int mouseX, int mouseY, int state, CallbackInfo ci) {
        if(justEnoughfakepixel$button == null) return;
        if(mouseX > justEnoughfakepixel$button.xPosition && mouseX < justEnoughfakepixel$button.xPosition + justEnoughfakepixel$button.width
                && mouseY > justEnoughfakepixel$button.yPosition && mouseY < justEnoughfakepixel$button.yPosition + justEnoughfakepixel$button.height) {
            BazaarParser.startParsingSequence((ContainerChest)this.inventorySlots);
        }
    }

}
