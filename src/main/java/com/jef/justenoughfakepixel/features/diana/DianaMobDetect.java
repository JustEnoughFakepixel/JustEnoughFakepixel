package com.jef.justenoughfakepixel.features.diana;

import com.jef.justenoughfakepixel.utils.ScoreboardUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;

public class DianaMobDetect {

    private static final long   NAME_CHECK_TIMEOUT_MS = 1_000L;
    private static final String DIANA_MARKER          = "\u00a72\u2435";  // §2✿
    private static final Pattern HP_REGEX             = Pattern.compile("([0-9]+(?:\\.[0-9]+)?[MK]?)\u00a7f/");

    private final Map<Integer, Long>              unconfirmed = new HashMap<>();
    private final Map<Integer, EntityArmorStand>  tracked     = new HashMap<>();
    private final Set<Integer>                    defeated    = new HashSet<>();

    private final Minecraft mc = Minecraft.getMinecraft();

    private static boolean isActive() {
        return DianaStats.hasSpadeInHotbar()
                && ScoreboardUtils.getCurrentLocation() == ScoreboardUtils.Location.HUB;
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (!isActive()) return;
        if (event.entity instanceof EntityArmorStand) {
            unconfirmed.put(event.entity.getEntityId(), System.currentTimeMillis());
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!isActive()) {
            unconfirmed.clear();
            tracked.clear();
            defeated.clear();
            return;
        }

        long now = System.currentTimeMillis();

        // Promote unconfirmed armor stands that have the §2✿ Diana marker
        Iterator<Map.Entry<Integer, Long>> unconfirmedIt = unconfirmed.entrySet().iterator();
        while (unconfirmedIt.hasNext()) {
            Map.Entry<Integer, Long> entry = unconfirmedIt.next();
            int id = entry.getKey();
            Entity entity = mc.theWorld.getEntityByID(id);

            if (!(entity instanceof EntityArmorStand) || entity.isDead) {
                unconfirmedIt.remove();
                continue;
            }

            String name = entity.getDisplayName().getFormattedText();
            if (name.contains(DIANA_MARKER)) {
                tracked.put(id, (EntityArmorStand) entity);
                unconfirmedIt.remove();
            } else if (now - entry.getValue() > NAME_CHECK_TIMEOUT_MS) {
                unconfirmedIt.remove();
            }
        }

        // Check tracked mobs for HP == 0 (death)
        Iterator<Map.Entry<Integer, EntityArmorStand>> trackedIt = tracked.entrySet().iterator();
        while (trackedIt.hasNext()) {
            Map.Entry<Integer, EntityArmorStand> entry = trackedIt.next();
            int id = entry.getKey();
            EntityArmorStand stand = entry.getValue();

            if (stand.isDead || mc.theWorld.getEntityByID(id) == null) {
                trackedIt.remove();
                defeated.remove(id);
                continue;
            }

            if (defeated.contains(id)) continue;

            String name = stand.getDisplayName().getFormattedText();
            if (!name.contains(DIANA_MARKER)) continue;

            Double hp = parseHp(name);
            if (hp != null && hp <= 0.0) {
                defeated.add(id);
                onDianaMobDeath(name, stand);
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        unconfirmed.clear();
        tracked.clear();
        defeated.clear();
    }

    private void onDianaMobDeath(String name, EntityArmorStand entity) {
        String clean = net.minecraft.util.StringUtils.stripControlCodes(name);
        if (clean.contains("Minos Inquisitor")) {
            DianaStats.getInstance().onInqDeath();
        }
    }

    private static Double parseHp(String formattedName) {
        Matcher m = HP_REGEX.matcher(formattedName);
        if (!m.find()) return null;
        String raw = m.group(1);
        try {
            if (raw.endsWith("M")) return Double.parseDouble(raw.substring(0, raw.length() - 1)) * 1_000_000;
            if (raw.endsWith("K")) return Double.parseDouble(raw.substring(0, raw.length() - 1)) * 1_000;
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}