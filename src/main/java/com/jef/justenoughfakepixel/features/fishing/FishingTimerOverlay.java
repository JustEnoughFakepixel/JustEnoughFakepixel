package com.jef.justenoughfakepixel.features.fishing;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@RegisterEvents
public class FishingTimerOverlay {

    private boolean alertPlayed = false;

    private boolean isEnabled() {
        return JefConfig.feature != null && JefConfig.feature.fishing.fishingTimer;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        EntityFishHook hook = mc.thePlayer.fishEntity;
        if (hook == null) {
            alertPlayed = false;
            return;
        }

        float seconds = hook.ticksExisted / 20f;
        int alertTime = JefConfig.feature.fishing.fishingTimerAlertTime;

        if (seconds >= alertTime && !alertPlayed) {
            alertPlayed = true;
            playAlertSound();
        } else if (seconds < alertTime) {
            alertPlayed = false;
        }
    }

    private void playAlertSound() {
        try {
            ISound sound = new PositionedSound(new ResourceLocation("random.orb")) {{
                volume = 1f;
                pitch = 2f;
                repeat = false;
                repeatDelay = 0;
                attenuationType = ISound.AttenuationType.NONE;
            }};
            Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!isEnabled()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        EntityFishHook hook = mc.thePlayer.fishEntity;
        if (hook == null) return;

        double x = hook.lastTickPosX + (hook.posX - hook.lastTickPosX) * event.partialTicks;
        double y = hook.lastTickPosY + (hook.posY - hook.lastTickPosY) * event.partialTicks + 0.5;
        double z = hook.lastTickPosZ + (hook.posZ - hook.lastTickPosZ) * event.partialTicks;

        renderText(x, y, z, getTimerText(), getCurrentColor());
    }

    private String getTimerText() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.thePlayer.fishEntity == null) return "";

        float seconds = mc.thePlayer.fishEntity.ticksExisted / 20f;
        return String.format("%.2fs", seconds);
    }

    private int getCurrentColor() {
        if (JefConfig.feature == null) return 0xFFFFFFFF;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.thePlayer.fishEntity == null) return 0xFFFFFFFF;

        float seconds = mc.thePlayer.fishEntity.ticksExisted / 20f;
        boolean alerted = seconds >= JefConfig.feature.fishing.fishingTimerAlertTime;

        return ChromaColour.specialToChromaRGB(alerted ? JefConfig.feature.fishing.fishingTimerAlertColor : JefConfig.feature.fishing.fishingTimerNormalColor);
    }

    private void renderText(double x, double y, double z, String text, int color) {
        Minecraft mc = Minecraft.getMinecraft();

        double viewerX = mc.getRenderManager().viewerPosX;
        double viewerY = mc.getRenderManager().viewerPosY;
        double viewerZ = mc.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x - viewerX, y - viewerY, z - viewerZ);

        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1, 0, 0);

        float scale = 0.025f;
        GlStateManager.scale(-scale, -scale, scale);

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        FontRenderer fr = mc.fontRendererObj;
        int width = fr.getStringWidth(text) / 2;

        fr.drawString(text, -width, 0, color, true);

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}