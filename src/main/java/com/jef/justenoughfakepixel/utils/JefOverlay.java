package com.jef.justenoughfakepixel.utils;

import com.jef.justenoughfakepixel.core.config.utils.Position;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Base class for all JEF overlays
 * Subclasses implement getLines(), getPosition(), getScale(), showBackground(),
 * and optionally getBaseWidth() / extraGuard()
 */
public abstract class JefOverlay {

    protected static final int LINE_HEIGHT = 10;
    protected static final int PADDING     = 3;

    protected int lastW;
    protected int lastH;

    protected JefOverlay(int defaultW, int defaultH) {
        this.lastW = defaultW;
        this.lastH = defaultH;
    }

    /** Width used by GuiPositionEditor (updated each render pass)*/
    public int getOverlayWidth()  { return lastW; }
    /** Height used by GuiPositionEditor (updated each render pass) */
    public int getOverlayHeight() { return lastH; }

    /** Lines to render. Return empty list to skip rendering */
    public abstract List<String> getLines(boolean preview);

    /** Which config Position field to use */
    public abstract Position getPosition();

    /** Which config scale field to use */
    public abstract float getScale();

    /** Whether to draw the dark background rect */
    public abstract boolean showBackground();


    /** Minimum pixel width before text measurement Default 20 */
    protected int getBaseWidth() { return 20; }


    protected boolean extraGuard() { return true; }

    public void render(boolean preview) {
        if (!preview && (OverlayUtils.shouldHide() || !extraGuard())) return;

        List<String> lines = getLines(preview);
        if (lines == null || lines.isEmpty()) return;

        Minecraft mc = Minecraft.getMinecraft();
        float scale = getScale();

        int w = getBaseWidth();
        for (String line : lines)
            w = Math.max(w, mc.fontRendererObj.getStringWidth(line) + PADDING * 2);
        int h = lines.size() * LINE_HEIGHT + PADDING * 2;
        lastW = w;
        lastH = h;

        ScaledResolution sr  = new ScaledResolution(mc);
        Position         pos = getPosition();
        int x = pos.getAbsX(sr, (int)(w * scale));
        int y = pos.getAbsY(sr, (int)(h * scale));
        if (pos.isCenterX()) x -= (int)(w * scale / 2);
        if (pos.isCenterY()) y -= (int)(h * scale / 2);

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scale, scale, 1f);

        if (showBackground())
            Gui.drawRect(-PADDING, -PADDING, w, h - PADDING, 0x88000000);

        int dy = 0;
        for (String line : lines) {
            mc.fontRendererObj.drawStringWithShadow(line, 0, dy, 0xFFFFFF);
            dy += LINE_HEIGHT;
        }

        GL11.glPopMatrix();
    }
}