package com.jef.justenoughfakepixel.features.qol;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.TimeFormatter;
import com.jef.justenoughfakepixel.utils.overlay.Overlay;
import com.jef.justenoughfakepixel.utils.render.ItemRenderUtils;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RegisterEvents
public class ItemCooldownOverlay extends Overlay {

    private static final int ICON_GAP = 2;

    @Getter
    private static ItemCooldownOverlay instance;

    public ItemCooldownOverlay() {
        super(90, 14);
        instance = this;
    }

    @Override
    public Position getPosition() {
        return JefConfig.feature.qol.itemCooldownPos;
    }

    @Override
    public float getScale() {
        return JefConfig.feature.qol.itemCooldownScale;
    }

    @Override
    public int getBgColor() {
        return ChromaColour.specialToChromaRGB(JefConfig.feature.qol.itemCooldownBgColor);
    }

    @Override
    public int getCornerRadius() {
        return JefConfig.feature.qol.itemCooldownCornerRadius;
    }

    @Override
    protected boolean isEnabled() {
        return JefConfig.feature != null && JefConfig.feature.qol.itemCooldownOverlay;
    }

    @Override
    public List<String> getLines(boolean preview) {
        if (preview) return Collections.singletonList("§5Example Item §f30.0s");

        List<String> active = ItemCooldowns.getActiveCooldowns();
        if (active.isEmpty()) return Collections.emptyList();

        List<String> lines = new ArrayList<>(active.size());
        for (String id : active) {
            ItemStack stack = ItemCooldowns.findItemStack(id);
            if (stack == null) continue;
            lines.add(stack.getDisplayName() + " §f" + TimeFormatter.formatTime(ItemCooldowns.getRemainingMs(id)));
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
        for (String line : lines) textW = Math.max(textW, mc.fontRendererObj.getStringWidth(line));
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
        if ((bgColor >>> 24) != 0) drawRoundedRect(-PADDING, -PADDING, w, h, getCornerRadius(), bgColor);

        List<ItemStack> stacks = new ArrayList<>();
        if (!preview) {
            for (String id : ItemCooldowns.getActiveCooldowns()) {
                ItemStack s = ItemCooldowns.findItemStack(id);
                if (s != null) stacks.add(s);
            }
        }

        int dy = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (i < stacks.size()) ItemRenderUtils.renderItemIcon(mc, stacks.get(i), 0, dy, iconSize);
            mc.fontRendererObj.drawStringWithShadow(lines.get(i), iconSize + ICON_GAP, dy, 0xFFFFFF);
            dy += LINE_HEIGHT;
        }

        GL11.glPopMatrix();
    }
}