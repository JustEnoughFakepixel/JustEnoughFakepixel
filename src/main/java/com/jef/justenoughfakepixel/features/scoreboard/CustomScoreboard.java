package com.jef.justenoughfakepixel.features.scoreboard;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.features.mining.FetchurHelper;
import com.jef.justenoughfakepixel.utils.JefOverlay;
import com.jef.justenoughfakepixel.utils.ScoreboardUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.regex.Pattern;

public class CustomScoreboard extends JefOverlay {

    private static final int PAD_X = 4;
    private static final int PAD_Y = 4;
    private static final int LINE_GAP = 1;
    private static final int SUPERSAMPLE = 2;

    private static final int TITLE_COL = 0xFFFFAA00;

    private static final int LINE_SERVER = 0;
    private static final int LINE_SEASON = 1;
    private static final int LINE_TIME = 2;
    private static final int LINE_LOCATION = 3;
    private static final int LINE_PURSE = 4;
    private static final int LINE_BANK = 5;
    private static final int LINE_BITS = 6;
    private static final int LINE_POWER = 7;
    private static final int LINE_FETCHUR = 8;

    private static final String LOC_SYMBOL_NORMAL = "\u23E3";
    private static final String LOC_SYMBOL_RIFT = "\u0444";

    private static final Pattern SEASON_PATTERN =
            Pattern.compile("\\s*(?:(?:Late|Early) )?(?:Spring|Summer|Autumn|Winter) \\d+.*");
    private static final Pattern TIME_PATTERN =
            Pattern.compile("\\s*\\d+:\\d+(?:am|pm).*");
    private static final Pattern SERVER_PATTERN =
            Pattern.compile("\\s*\\d{2}/\\d{2}/\\d{2}.*");
    private static final Pattern PURSE_PATTERN =
            Pattern.compile("(?:Piggy|Purse): [\\d,.]+");
    private static final Pattern BITS_PATTERN =
            Pattern.compile("Bits: [\\d,.]+");
    private static final Pattern BANK_PATTERN =
            Pattern.compile("Bank: .+");
    private static final Pattern WEBSITE_PATTERN =
            Pattern.compile(".*fakepixel.*");

    private static CustomScoreboard instance;

    public CustomScoreboard() {
        super(130, 90);
        instance = this;
    }

    public static CustomScoreboard getInstance() {
        return instance;
    }

    public static boolean isActive() {
        return JefConfig.feature != null &&
                JefConfig.feature.scoreboard != null &&
                JefConfig.feature.scoreboard.enabled;
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.scoreboard.position;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.scoreboard.scale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.scoreboard.scoreboardBg);
    }

    @Override
    public int getCornerRadius() {
        return (int) JefConfig.feature.scoreboard.cornerRadius;
    }

    @Override
    protected boolean extraGuard() {
        return isActive();
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) return;
        render(false);
    }

    private static List<Integer> getLineOrder() {
        List<?> raw = JefConfig.feature.scoreboard.scoreboardLines;
        List<Integer> result = new ArrayList<>();
        if (raw == null) return result;

        for (Object o : raw)
            if (o instanceof Number)
                result.add(((Number) o).intValue());

        return result;
    }

    @Override
    public List<String> getLines(boolean preview) {

        List<String> raw = new ArrayList<>(ScoreboardUtils.getScoreboardLines());
        if (raw.isEmpty()) return new ArrayList<>();

        Collections.reverse(raw);

        String serverRaw = null, seasonRaw = null, timeRaw = null;
        String locationRaw = null, purseRaw = null, bankRaw = null;
        String bitsRaw = null, websiteRaw = null;

        List<String> claimed = new ArrayList<>();

        for (String l : raw) {

            String c = stripColor(l).trim();
            if (c.isEmpty()) continue;

            if (locationRaw == null && (l.contains(LOC_SYMBOL_NORMAL) || l.contains(LOC_SYMBOL_RIFT))) {
                locationRaw = l;
                claimed.add(l);
                continue;
            }

            if (serverRaw == null && SERVER_PATTERN.matcher(c).matches()) {
                serverRaw = l;
                claimed.add(l);
                continue;
            }

            if (seasonRaw == null && SEASON_PATTERN.matcher(c).matches()) {
                seasonRaw = l;
                claimed.add(l);
                continue;
            }

            if (timeRaw == null && TIME_PATTERN.matcher(c).matches()) {
                timeRaw = l;
                claimed.add(l);
                continue;
            }

            if (purseRaw == null && PURSE_PATTERN.matcher(c).find()) {
                purseRaw = l;
                claimed.add(l);
                continue;
            }

            if (bankRaw == null && BANK_PATTERN.matcher(c).find()) {
                bankRaw = l;
                claimed.add(l);
                continue;
            }

            if (bitsRaw == null && BITS_PATTERN.matcher(c).find()) {
                bitsRaw = l;
                claimed.add(l);
                continue;
            }

            if (websiteRaw == null && WEBSITE_PATTERN.matcher(c).matches()) {
                websiteRaw = l;
                claimed.add(l);
            }
        }

        List<String> lines = new ArrayList<>();

        String title = ScoreboardUtils.getServerId();
        if (title == null || title.isEmpty()) title = "SKYBLOCK";

        lines.add("\u00A76\u00A7l" + title);

        for (int id : getLineOrder()) {

            switch (id) {

                case LINE_SERVER:
                    if (serverRaw != null) lines.add(serverRaw);
                    break;

                case LINE_SEASON:
                    if (seasonRaw != null) lines.add(seasonRaw);
                    break;

                case LINE_TIME:
                    if (timeRaw != null) lines.add(timeRaw);
                    break;

                case LINE_LOCATION:
                    if (locationRaw != null) lines.add(locationRaw);
                    break;

                case LINE_PURSE:
                    if (purseRaw != null) lines.add(purseRaw);
                    break;

                case LINE_BITS:
                    if (bitsRaw != null) lines.add(bitsRaw);
                    break;

                case LINE_POWER:
                    String power = MaxwellPowerSync.getPower();
                    if (power != null && ScoreboardUtils.isOnSkyblock()) {
                        lines.add("\u00A7fPower: \u00A7d" + power);
                    }
                    break;

                case LINE_BANK:
                    if (bankRaw != null)
                        lines.add(bankRaw);
                    break;

                case LINE_FETCHUR:
                    if (ScoreboardUtils.isOnSkyblock())
                        lines.add("§fFetchur: §e" + FetchurHelper.getTodaysItem());
                    break;
            }
        }

        for (String l : raw)
            if (!claimed.contains(l) && !stripColor(l).trim().isEmpty())
                lines.add(l);

        if (websiteRaw != null)
            lines.add(websiteRaw);

        return lines;
    }

    @Override
    public void render(boolean preview) {

        if (!preview && (!extraGuard())) return;

        List<String> lines = getLines(preview);
        if (lines.isEmpty()) return;

        Minecraft mc = Minecraft.getMinecraft();
        float scale = getScale();
        int lh = LINE_HEIGHT + LINE_GAP;
        int ss = SUPERSAMPLE;

        int maxW = 60;

        for (String line : lines)
            maxW = Math.max(maxW, mc.fontRendererObj.getStringWidth(line));

        int boxW = maxW + PAD_X * 2;
        int boxH = lines.size() * lh + PAD_Y * 2 - LINE_GAP;

        lastW = boxW;
        lastH = boxH;

        ScaledResolution sr = new ScaledResolution(mc);
        Position pos = getPosition();

        int x = pos.getAbsX(sr, (int)(boxW * scale));
        int y = pos.getAbsY(sr, (int)(boxH * scale));

        if (pos.isCenterX()) x -= (int)(boxW * scale / 2);
        if (pos.isCenterY()) y -= (int)(boxH * scale / 2);

        GL11.glPushMatrix();

        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scale / ss, scale / ss, 1f);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        int r = getCornerRadius() * ss;

        int bgColor = getBgColor();

        if ((bgColor >>> 24) != 0)
            drawRoundedRect(0,0,boxW*ss,boxH*ss,r,bgColor);

        GL11.glScalef(ss, ss, 1f);

        String title = lines.get(0);

        int titleX = (boxW - mc.fontRendererObj.getStringWidth(title)) / 2;

        mc.fontRendererObj.drawStringWithShadow(title, titleX, PAD_Y, TITLE_COL);

        int textY = PAD_Y + lh;

        for (int i = 1; i < lines.size(); i++) {
            mc.fontRendererObj.drawStringWithShadow(lines.get(i), PAD_X, textY, 0xFFFFFF);
            textY += lh;
        }

        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    private static String stripColor(String s) {
        return s == null ? "" : s.replaceAll("\u00A7[0-9a-fklmnorA-FKLMNOR]", "");
    }
}