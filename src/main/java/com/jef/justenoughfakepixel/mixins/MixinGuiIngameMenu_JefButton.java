package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.JefOptionsGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameMenu.class)
public abstract class MixinGuiIngameMenu_JefButton extends GuiScreen {

    @Unique
    private static final int BTN_JEF = 0x4EF;

    @Unique
    @Inject(method = "initGui", at = @At("TAIL"))
    private void jef$addButton(CallbackInfo ci) {
        // Find the lowest Y position among all existing buttons so we never overlap
        int lowestY = this.height / 4 + 8;
        for (GuiButton btn : this.buttonList) {
            lowestY = Math.max(lowestY, btn.yPosition + btn.height);
        }

        this.buttonList.add(new GuiButton(
                BTN_JEF,
                this.width / 2 - 100,
                lowestY + 4,
                200,
                20,
                "Just Enough Fakepixel"
        ));
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void jef$actionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == BTN_JEF) {
            Minecraft.getMinecraft().displayGuiScreen(new JefOptionsGui());
            ci.cancel();
        }
    }
}