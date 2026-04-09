package com.jef.justenoughfakepixel.features.qol;

import com.jef.justenoughfakepixel.core.JefConfig;
import com.jef.justenoughfakepixel.core.config.editors.ChromaColour;
import com.jef.justenoughfakepixel.init.RegisterEvents;
import com.jef.justenoughfakepixel.utils.render.WorldRenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@RegisterEvents
public class BlockOverlay {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static AxisAlignedBB getSelectionAABB(BlockPos pos) {
        if (mc.theWorld == null) {
            return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        }
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        AxisAlignedBB aabb = block.getSelectedBoundingBox(mc.theWorld, pos);
        return aabb != null ? aabb : new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }

    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        if (!JefConfig.feature.qol.blockSelectionOverlay) return;
        if (event.target == null || event.target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        event.setCanceled(true);

        int argb = ChromaColour.specialToChromaRGB(JefConfig.feature.qol.blockSelectionColor);
        Color color = new Color((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF);

        BlockPos pos = event.target.getBlockPos();
        AxisAlignedBB aabb = getSelectionAABB(pos);

        if (JefConfig.feature.qol.blockSelectionMode == 0) {
            WorldRenderUtils.drawFilledBlock(aabb, color);
        } else {
            WorldRenderUtils.drawSelectionBox(aabb, color, JefConfig.feature.qol.blockSelectionThickness);
        }
    }
}