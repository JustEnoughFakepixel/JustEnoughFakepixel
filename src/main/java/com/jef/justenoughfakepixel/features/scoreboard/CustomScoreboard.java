package com.jef.justenoughfakepixel.features.scoreboard;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.utils.JefOverlay;
import com.jef.justenoughfakepixel.utils.OverlayUtils;
import com.jef.justenoughfakepixel.utils.ScoreboardUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomScoreboard extends JefOverlay {

    // ── Layout ────────────────────────────────────────────────────────────────
    private static final int PAD_X      = 4;
    private static final int PAD_Y      = 4;
    private static final int LINE_GAP   = 1;
    private static final int SUPERSAMPLE = 2;

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final int TITLE_COL = 0xFFFFAA00;

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static CustomScoreboard instance;

    public CustomScoreboard() {
        super(130, 90);
        instance = this;
    }

    public static CustomScoreboard getInstance() { return instance; }

    public static boolean isActive() {
        return JefConfig.feature != null
                && JefConfig.feature.scoreboard != null
                && JefConfig.feature.scoreboard.enabled;
    }

    @Override public Position getPosition()    { return JefConfig.feature.scoreboard.position; }
    @Override public float    getScale()       { return JefConfig.feature.scoreboard.scale; }
    @Override public boolean  showBackground() { return false; }
    @Override protected boolean extraGuard()   { return isActive(); }

    // ── Render hook ───────────────────────────────────────────────────────────

    @SubscribeEvent
    public void onRenderPost(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (OverlayUtils.shouldHide()) return;
        render(false);
    }

    // ── Lines ─────────────────────────────────────────────────────────────────

    @Override
    public List<String> getLines(boolean preview) {
        List<String> lines = new ArrayList<>();

        if (preview) {
            lines.add("\u00A76\u00A7l SKYBLOCK ");
            lines.add("\u00A7fLate Autumn 31st \u00A7b5:30am");
            lines.add(" \u00A77www.fakepixel.fun");
            lines.add("\u00A7fPurse: \u00A76822,022");
            lines.add("\u00A7fBank: \u00A761,204,802");
            lines.add("\u00A7fBits: \u00A7b6,100");
            lines.add("\u00A7eActive Quest");
            lines.add("\u00A7cKill 500 Zombies");
            lines.add("\u00A77(327/500) Progress");
            return lines;
        }

        if (!ScoreboardUtils.isOnSkyblock()) return lines;

        String title = ScoreboardUtils.getServerId();
        if (title == null || title.trim().isEmpty()) title = "SKYBLOCK";
        lines.add("\u00A76\u00A7l" + title.trim());

        List<String> raw = new ArrayList<>(ScoreboardUtils.getScoreboardLines());
        Collections.reverse(raw);

        if (raw.isEmpty()) return lines;

        boolean hasPurse = false, hasBank = false;
        for (String l : raw) {
            String c = stripColor(l).trim();
            if (c.startsWith("Purse:") || c.startsWith("Piggy:")) hasPurse = true;
            if (c.startsWith("Bank:")) hasBank = true;
        }

        for (String l : raw) {
            String c = stripColor(l).trim();
            if (c.startsWith("Bank:")) continue;
            lines.add(l);
            if (hasPurse && (c.startsWith("Purse:") || c.startsWith("Piggy:"))) {
                if (hasBank) {
                    for (String bl : raw) {
                        if (stripColor(bl).trim().startsWith("Bank:")) { lines.add(bl); break; }
                    }
                } else {
                    String bank = BankParser.getBank();
                    if (bank != null) lines.add("\u00A7fBank: \u00A76" + bank);
                }
            }
        }

        return lines;
    }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void render(boolean preview) {
        if (JefConfig.feature == null) return;
        if (!preview && (!extraGuard() || OverlayUtils.shouldHide())) return;

        List<String> lines = getLines(preview);
        if (lines.isEmpty()) return;

        Minecraft mc  = Minecraft.getMinecraft();
        float   scale = getScale();
        int     lh    = LINE_HEIGHT + LINE_GAP;
        int     ss    = SUPERSAMPLE;

        int maxW = 60;
        for (String line : lines)
            maxW = Math.max(maxW, mc.fontRendererObj.getStringWidth(stripColor(line)));

        int boxW = maxW + PAD_X * 2;
        int boxH = lines.size() * lh + PAD_Y * 2 - LINE_GAP;
        lastW = boxW;
        lastH = boxH;

        ScaledResolution sr  = new ScaledResolution(mc);
        Position         pos = getPosition();
        int x = pos.getAbsX(sr, (int)(boxW * scale));
        int y = pos.getAbsY(sr, (int)(boxH * scale));
        if (pos.isCenterX()) x -= (int)(boxW * scale / 2);
        if (pos.isCenterY()) y -= (int)(boxH * scale / 2);

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scale / ss, scale / ss, 1f);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        int bgAlpha = (int)(JefConfig.feature.scoreboard.opacity / 100f * 255f);
        int r = (int) JefConfig.feature.scoreboard.cornerRadius * ss;
        drawRoundedRect(0, 0, boxW * ss, boxH * ss, r, (bgAlpha << 24) | 0x101010);

        GL11.glScalef(ss, ss, 1f);

        String titleLine = lines.get(0);
        int titleW = mc.fontRendererObj.getStringWidth(stripColor(titleLine));
        int titleX = (boxW - titleW) / 2;
        mc.fontRendererObj.drawStringWithShadow(titleLine, titleX, PAD_Y, TITLE_COL);

        int textY = PAD_Y + lh;
        for (int i = 1; i < lines.size(); i++) {
            mc.fontRendererObj.drawStringWithShadow(lines.get(i), PAD_X, textY, 0xFFFFFF);
            textY += lh;
        }

        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    // ── Rounded rect ──────────────────────────────────────────────────────────

    private static void drawRoundedRect(int x, int y, int w, int h, int r, int color) {
        r = Math.min(r, Math.min(w, h) / 2);

        Gui.drawRect(x + r, y,       x + w - r, y + h,     color);
        Gui.drawRect(x,     y + r,   x + r,     y + h - r, color);
        Gui.drawRect(x + w - r, y + r, x + w,   y + h - r, color);

        for (int i = 0; i < r; i++) {
            int cut = (int) Math.round(r - Math.sqrt(Math.max(0.0, (double) r * r - (double)(r - i - 1) * (r - i - 1))));
            Gui.drawRect(x + i,         y + cut,   x + i + 1,   y + r,       color);
            Gui.drawRect(x + w - i - 1, y + cut,   x + w - i,   y + r,       color);
            Gui.drawRect(x + i,         y + h - r, x + i + 1,   y + h - cut, color);
            Gui.drawRect(x + w - i - 1, y + h - r, x + w - i,   y + h - cut, color);
        }
    }

    private static String stripColor(String s) {
        return s == null ? "" : s.replaceAll("\u00A7[0-9a-fklmnorA-FKLMNOR]", "");
    }
}