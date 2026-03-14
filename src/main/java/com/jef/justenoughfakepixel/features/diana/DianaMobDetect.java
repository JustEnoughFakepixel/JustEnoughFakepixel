package com.jef.justenoughfakepixel.features.diana;

import com.jef.justenoughfakepixel.utils.ScoreboardUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.*;
import java.util.regex.Pattern;

public class DianaMobDetect {

    private static final long   NAME_CHECK_TIMEOUT_MS = 1_000L;
    private static final long   LOOTSHARE_WINDOW_MS   = 2_000L;
    private static final String DIANA_MARKER          = "\u00a72\u2435"; // §2✿

    private final Map<Integer, Long>             unconfirmed = new HashMap<>();
    private final Map<Integer, EntityArmorStand> tracked     = new HashMap<>();
    private final Set<Integer>                   trackedInqs = new HashSet<>();

    // Timestamp of the last inq stand disappearance, for lootshare attribution
    private static volatile long lastInqDisappearMs = -1L;

    private static DianaMobDetect INSTANCE;

    public DianaMobDetect() { INSTANCE = this; }

    private final Minecraft mc = Minecraft.getMinecraft();

    private static boolean isActive() {
        return DianaStats.hasSpadeInHotbar()
                && ScoreboardUtils.getCurrentLocation() == ScoreboardUtils.Location.HUB;
    }

    /**
     * Returns true if an inq stand disappeared within the lootshare window
     * Called by DianaTracker when a lootshare chat message arrives
     */
    public static boolean wasInqKilledByOther() {
        return lastInqDisappearMs > 0
                && System.currentTimeMillis() - lastInqDisappearMs <= LOOTSHARE_WINDOW_MS;
    }

    /** Returns the raw formatted name of the closest tracked inq stand, or null if none active */
    public static String getClosestInqName() {
        if (INSTANCE == null) return null;
        EntityArmorStand closest = null;
        double minDist = Double.MAX_VALUE;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return null;
        for (Map.Entry<Integer, EntityArmorStand> e : INSTANCE.tracked.entrySet()) {
            if (!INSTANCE.trackedInqs.contains(e.getKey())) continue;
            EntityArmorStand stand = e.getValue();
            if (stand.isDead) continue;
            double d = mc.thePlayer.getDistanceSqToEntity(stand);
            if (d < minDist) { minDist = d; closest = stand; }
        }
        return closest != null ? closest.getDisplayName().getFormattedText() : null;
    }

    /** Called by DianaTracker after confirming a lootshare kill, to reset the flag. */
    public static void clearInqDisappear() {
        lastInqDisappearMs = -1L;
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (!isActive()) return;
        if (event.entity instanceof EntityArmorStand)
            unconfirmed.put(event.entity.getEntityId(), System.currentTimeMillis());
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!isActive()) {
            unconfirmed.clear();
            tracked.clear();
            trackedInqs.clear();
            return;
        }

        long now = System.currentTimeMillis();
        promoteUnconfirmed(now);
        checkTracked();
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        unconfirmed.clear();
        tracked.clear();
        trackedInqs.clear();
    }


    private void promoteUnconfirmed(long now) {
        Iterator<Map.Entry<Integer, Long>> it = unconfirmed.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Long> entry = it.next();
            int id = entry.getKey();
            Entity entity = mc.theWorld.getEntityByID(id);

            if (!(entity instanceof EntityArmorStand) || entity.isDead) { it.remove(); continue; }

            String name = entity.getDisplayName().getFormattedText();
            if (name.contains(DIANA_MARKER)) {
                tracked.put(id, (EntityArmorStand) entity);
                if (net.minecraft.util.StringUtils.stripControlCodes(name).contains("Minos Inquisitor"))
                    trackedInqs.add(id);
                it.remove();
            } else if (now - entry.getValue() > NAME_CHECK_TIMEOUT_MS) {
                it.remove();
            }
        }
    }

    private void checkTracked() {
        Iterator<Map.Entry<Integer, EntityArmorStand>> it = tracked.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, EntityArmorStand> entry = it.next();
            int id = entry.getKey();
            EntityArmorStand stand = entry.getValue();

            if (!stand.isDead && mc.theWorld.getEntityByID(id) != null) {
                if (!trackedInqs.contains(id)) {
                    String clean = net.minecraft.util.StringUtils.stripControlCodes(
                            stand.getDisplayName().getFormattedText());
                    if (clean.contains("Minos Inquisitor")) trackedInqs.add(id);
                }
                continue;
            }

            if (trackedInqs.contains(id)) {
                DianaStats.getInstance().onInqDeath();
                lastInqDisappearMs = System.currentTimeMillis();
            }

            it.remove();
            trackedInqs.remove(id);
        }
    }
}