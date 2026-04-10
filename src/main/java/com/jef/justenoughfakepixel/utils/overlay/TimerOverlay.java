package com.jef.justenoughfakepixel.utils.overlay;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.utils.time.TimeFormatter;
import com.jef.justenoughfakepixel.utils.render.ItemRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TimerOverlay extends Overlay {

    protected static final int ICON_GAP = 2;

    public TimerOverlay() {
        super(90, 14);
    }

    protected abstract String getHeaderText();

    protected abstract List<String> getActiveTimers();

    protected abstract ItemStack findItemStack(String id);

    protected abstract long getRemainingMs(String id);

    protected abstract boolean shouldShowWhenEmpty();

    protected abstract String getPreviewItemName();

    @Override
    public List<String> getLines(boolean preview) {
        List<String> lines = new ArrayList<>();

        if (preview) {
            lines.add(getHeaderText());
            lines.add(getPreviewItemName() + " §f30.0s");
            return lines;
        }

        List<String> active = getActiveTimers();
        if (active.isEmpty() && !shouldShowWhenEmpty()) {
            return Collections.emptyList();
        }

        lines.add(getHeaderText());

        for (String id : active) {
            ItemStack stack = findItemStack(id);
            if (stack == null) continue;
            lines.add(stack.getDisplayName() + " §f" + TimeFormatter.formatTime(getRemainingMs(id)));
        }

        return lines;
    }

    @Override
    public void render(boolean preview) {
        if (JefConfig.feature == null || !isEnabled()) return;

        List<String> lines = getLines(preview);
        if (lines == null || lines.isEmpty()) return;

        Minecraft mc = Minecraft.getMinecraft();
        float scale = getScale();
        int iconSize = LINE_HEIGHT;

        int textW = 0;
        for (String line : lines) {
            textW = Math.max(textW, mc.fontRendererObj.getStringWidth(line));
        }
        int w = textW + PADDING * 2 + iconSize + ICON_GAP;
        int h = lines.size() * LINE_HEIGHT + PADDING * 2;
        lastW = w;
        lastH = h;

        ScaledResolution sr = new ScaledResolution(mc);
        Position pos = getPosition();
        int x = pos.getAbsX(sr, (int) (w * scale));
        int y = pos.getAbsY(sr, (int) (h * scale));
        if (pos.isCenterX()) x -= (int) (w * scale / 2);
        if (pos.isCenterY()) y -= (int) (h * scale / 2);

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scale, scale, 1f);

        int bgColor = getBgColor();
        if ((bgColor >>> 24) != 0) {
            drawRoundedRect(-PADDING, -PADDING, w, h, getCornerRadius(), bgColor);
        }

        List<ItemStack> stacks = new ArrayList<>();
        if (!preview) {
            for (String id : getActiveTimers()) {
                ItemStack s = findItemStack(id);
                if (s != null) stacks.add(s);
            }
        }

        int dy = 0;
        for (int i = 0; i < lines.size(); i++) {
            // Skip icon for header
            if (i > 0 && (i - 1) < stacks.size()) {
                ItemRenderUtils.renderItemIcon(mc, stacks.get(i - 1), 0, dy, iconSize);
            }
            mc.fontRendererObj.drawStringWithShadow(lines.get(i), iconSize + ICON_GAP, dy, 0xFFFFFF);
            dy += LINE_HEIGHT;
        }

        GL11.glPopMatrix();
    }
}
