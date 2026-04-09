package com.jef.justenoughfakepixel.features.waypoints;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.render.WorldRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

@RegisterEvents
public class WaypointRenderer {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static Color argbToColor(int argb) {
        return new Color((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        WaypointState state = WaypointState.getInstance();
        if (!state.enabled || !state.hasGroup()) return;

        if (JefConfig.feature != null) {
            state.advanceRange = JefConfig.feature.waypoints.advanceRange;
            state.advanceDelayMs = (long) JefConfig.feature.waypoints.advanceDelayMs;
        }

        tickAdvance(state);

        WaypointPoint target = state.getNext();
        if (target == null) return;

        double vx = mc.getRenderManager().viewerPosX;
        double vy = mc.getRenderManager().viewerPosY;
        double vz = mc.getRenderManager().viewerPosZ;

        // ESP boxes
        WorldRenderUtils.beginWorldRender(2f);
        GL11.glPushMatrix();
        GL11.glTranslated(-vx, -vy, -vz);
        if (state.setupMode) {
            for (WaypointPoint wp : state.loadedGroup.waypoints)
                WorldRenderUtils.drawEspBox(wp.x, wp.y, wp.z, boxColor());
        } else {
            WorldRenderUtils.drawEspBox(target.x, target.y, target.z, boxColor());
        }
        GL11.glPopMatrix();
        WorldRenderUtils.endWorldRender();

        // Tracer
        WorldRenderUtils.drawTracer(new Vec3(target.x + 0.5, target.y + 0.5, target.z + 0.5), event.partialTicks, tracerColor());

        // Labels
        GL11.glPushMatrix();
        GL11.glTranslated(-vx, -vy, -vz);
        if (state.setupMode) drawSetupLabels(state);
        else drawNormalLabels(state);
        GL11.glPopMatrix();
    }

    private void tickAdvance(WaypointState state) {
        if (mc.thePlayer == null) return;
        WaypointPoint next = state.getNext();
        if (next == null) return;
        double dist = next.distanceTo(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        if (dist <= state.advanceRange) {
            if (state.advanceTimerStart < 0) state.advanceTimerStart = System.currentTimeMillis();
            else if (System.currentTimeMillis() - state.advanceTimerStart >= state.advanceDelayMs) state.advance();
        } else {
            state.advanceTimerStart = -1L;
        }
    }

    private void drawSetupLabels(WaypointState state) {
        List<WaypointPoint> wps = state.loadedGroup.waypoints;
        int cur = state.currentIndex;
        int nxt = state.getNextIndex();
        for (int i = 0; i < wps.size(); i++) {
            WaypointPoint wp = wps.get(i);
            String col = (i == cur) ? "§a" : (i == nxt) ? "§e" : "§7";
            String label = (wp.name != null && !wp.name.isEmpty()) ? wp.name : String.valueOf(i + 1);
            drawLabel(wp.x + 0.5, wp.y + 2.2, wp.z + 0.5, col + label);
        }
    }

    private void drawNormalLabels(WaypointState state) {
        WaypointPoint prev = state.getPrev();
        WaypointPoint cur = state.getCurrent();
        WaypointPoint nxt = state.getNext();
        if (prev != null && prev != cur)
            drawLabel(prev.x + 0.5, prev.y + 2.2, prev.z + 0.5, "§7" + safeName(prev, "Prev"));
        if (cur != null) drawLabel(cur.x + 0.5, cur.y + 2.2, cur.z + 0.5, "§a" + safeName(cur, "Current"));
        if (nxt != null && nxt != cur) drawLabel(nxt.x + 0.5, nxt.y + 2.2, nxt.z + 0.5, "§e" + safeName(nxt, "Next"));
    }

    private void drawLabel(double wx, double wy, double wz, String text) {
        if (mc.thePlayer == null || mc.fontRendererObj == null) return;

        double dx = wx - mc.thePlayer.posX;
        double dy = wy - mc.thePlayer.posY;
        double dz = wz - mc.thePlayer.posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        float scale = Math.max(0.025f, (float) (Math.min(dist, 50.0) / 300.0));

        String nameStr = StringUtils.stripControlCodes(text);
        String distStr = (int) Math.round(dist) + "m";

        int nameW = mc.fontRendererObj.getStringWidth(nameStr);
        int distW = mc.fontRendererObj.getStringWidth(distStr);
        int totalW = nameW + mc.fontRendererObj.getStringWidth(" ") + distW;

        int nameColor = labelColor() | 0xFF000000;
        int distColor = distanceLabelColor() | 0xFF000000;

        GL11.glPushMatrix();
        GL11.glTranslated(wx, wy, wz);
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0f, 1f, 0f);
        GL11.glRotatef(mc.getRenderManager().playerViewX, 1f, 0f, 0f);
        GL11.glScalef(-scale, -scale, scale);

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        float sx = -totalW / 2f;
        mc.fontRendererObj.drawString(nameStr, sx, 0f, nameColor, false);
        mc.fontRendererObj.drawString(distStr, sx + nameW + mc.fontRendererObj.getStringWidth(" "), 0f, distColor, false);

        GlStateManager.enableDepth();
        GL11.glPopMatrix();
    }

    private String safeName(WaypointPoint wp, String fallback) {
        return (wp.name != null && !wp.name.isEmpty()) ? wp.name : fallback;
    }

    private Color boxColor() {
        if (JefConfig.feature == null) return Color.YELLOW;
        return argbToColor(ChromaColour.specialToChromaRGB(JefConfig.feature.waypoints.boxColour));
    }

    private Color tracerColor() {
        if (JefConfig.feature == null) return Color.YELLOW;
        return argbToColor(ChromaColour.specialToChromaRGB(JefConfig.feature.waypoints.tracerColour));
    }

    private int labelColor() {
        if (JefConfig.feature == null) return 0xFFFFFFFF;
        try {
            return ChromaColour.specialToChromaRGB(JefConfig.feature.waypoints.labelColour);
        } catch (Exception e) {
            return 0xFFFFFFFF;
        }
    }

    private int distanceLabelColor() {
        if (JefConfig.feature == null) return 0xFF55FFFF;
        try {
            return ChromaColour.specialToChromaRGB(JefConfig.feature.waypoints.distanceLabelColour);
        } catch (Exception e) {
            return 0xFF55FFFF;
        }
    }
}