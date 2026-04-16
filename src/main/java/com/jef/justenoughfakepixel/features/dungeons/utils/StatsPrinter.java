package com.jef.justenoughfakepixel.features.dungeons.utils;

import com.jef.justenoughfakepixel.utils.chat.ChatUtils;
import com.jef.justenoughfakepixel.utils.time.TimeFormatter;
import net.minecraft.util.EnumChatFormatting;

public class StatsPrinter {

    private static final String C_FLOOR_NM = EnumChatFormatting.GREEN.toString();
    private static final String C_FLOOR_MM = EnumChatFormatting.RED.toString();
    private static final String C_CLEAR = EnumChatFormatting.GREEN.toString();
    private static final String C_BLOOD = EnumChatFormatting.RED.toString();
    private static final String C_ENTRY = EnumChatFormatting.GOLD.toString();
    private static final String C_BOSS = EnumChatFormatting.DARK_RED.toString();
    private static final String C_MAXOR = EnumChatFormatting.YELLOW.toString();
    private static final String C_STORM = EnumChatFormatting.AQUA.toString();
    private static final String C_GOLDOR = EnumChatFormatting.GOLD.toString();
    private static final String C_NECRON = EnumChatFormatting.DARK_PURPLE.toString();
    private static final String C_WITHER = EnumChatFormatting.DARK_GRAY.toString();
    private static final String C_SCARF = EnumChatFormatting.DARK_PURPLE.toString();
    private static final String C_PROFESSOR = EnumChatFormatting.BLUE.toString();
    private static final String C_TERRA = EnumChatFormatting.GOLD.toString();
    private static final String C_GIANTS = EnumChatFormatting.RED.toString();
    private static final String C_SADAN = EnumChatFormatting.DARK_RED.toString();
    private static final String C_VAL = EnumChatFormatting.WHITE.toString();

    private final DungeonTimers timers;
    private final DungeonEndStats endStats;
    private final PBTracker pbTracker;

    public StatsPrinter(DungeonTimers timers, DungeonEndStats endStats, PBTracker pbTracker) {
        this.timers = timers;
        this.endStats = endStats;
        this.pbTracker = pbTracker;
    }

    public void printEndStats() {
        String floor = timers.getCurrentFloor() == DungeonFloor.NONE ? "?" : timers.getCurrentFloor().name();
        String sep = EnumChatFormatting.DARK_GRAY + "————————————————————";
        String fc = timers.getCurrentFloor().isMasterMode() ? C_FLOOR_MM : C_FLOOR_NM;

        send(sep);
        send(fc + EnumChatFormatting.BOLD + floor + (timers.getCurrentFloor().isMasterMode() ? " MM" : "") +
                (timers.isRunFailed() ? EnumChatFormatting.RED + " FAILED" : EnumChatFormatting.RESET + " End Stats"));

        if (endStats.getBossName() != null) send(EnumChatFormatting.RED + "☠ " + endStats.getBossName());
        if (endStats.getScore() != null) {
            send(EnumChatFormatting.YELLOW + "Score: " + endStats.getScore() + " (" + endStats.getGrade() + ")" +
                    (endStats.isScorePB() ? EnumChatFormatting.LIGHT_PURPLE + " (PB!)" : ""));
        }

        for (String xp : endStats.getXp()) send(EnumChatFormatting.DARK_AQUA + xp);

        if (timers.getClearedTime() > 0) {
            send(C_CLEAR + "Clear took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getClearedTime()) + pbTracker.getPbTag(floor + "_clear"));
        }
        if (timers.getBloodTime() > 0) {
            send(C_BLOOD + "Blood Rush: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getBloodTime()) + pbTracker.getPbTag(floor + "_blood"));
        }
        if (timers.getBossTime() > 0) {
            send(C_ENTRY + "Boss Entry: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getBossTime()) + pbTracker.getPbTag(floor + "_entry"));
        }
        if (timers.getBossDeadTime() > 0 && timers.getBossTime() > 0) {
            send(C_BOSS + "Boss took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getBossDeadTime() - timers.getBossTime()) + pbTracker.getPbTag(floor + "_boss"));
        }

        printFloorPhases(floor);

        send(sep);
    }

    private void printFloorPhases(String floor) {
        if (timers.getCurrentFloor().isF2orM2()) {
            if (timers.getScarfP1End() > 0) {
                send(C_SCARF + "Scarf P1 took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getScarfP1End() - timers.getScarfP1Start()) + pbTracker.getPbTag(floor + "_p1"));
            }
            if (timers.getScarfP2End() > 0) {
                send(C_SCARF + "Scarf P2 took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getScarfP2End() - timers.getScarfP2Start()) + pbTracker.getPbTag(floor + "_p2"));
            }
        }

        if (timers.getCurrentFloor().isF3orM3()) {
            if (timers.getProfessorP1End() > 0) {
                send(C_PROFESSOR + "Professor P1 took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getProfessorP1End() - timers.getProfessorP1Start()) + pbTracker.getPbTag(floor + "_p1"));
            }
            if (timers.getProfessorP2End() > 0) {
                send(C_PROFESSOR + "Professor P2 took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getProfessorP2End() - timers.getProfessorP2Start()) + pbTracker.getPbTag(floor + "_p2"));
            }
            if (timers.getProfessorP3End() > 0) {
                send(C_PROFESSOR + "Professor P3 took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getProfessorP3End() - timers.getProfessorP3Start()) + pbTracker.getPbTag(floor + "_p3"));
            }
        }

        if (timers.getCurrentFloor().isF6orM6()) {
            if (timers.getTerraEnd() > 0) {
                send(C_TERRA + "Terracotta took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getTerraEnd() - timers.getTerraStart()) + pbTracker.getPbTag(floor + "_terra"));
            }
            if (timers.getGiantsEnd() > 0) {
                send(C_GIANTS + "Giants took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getGiantsEnd() - timers.getGiantsStart()) + pbTracker.getPbTag(floor + "_giants"));
            }
            if (timers.getSadanEnd() > 0) {
                send(C_SADAN + "Sadan took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getSadanEnd() - timers.getSadanStart()) + pbTracker.getPbTag(floor + "_sadan"));
            }
        }

        if (timers.getCurrentFloor().isF7orM7()) {
            if (timers.getMaxorEnd() > 0) {
                send(C_MAXOR + "Maxor took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getMaxorEnd() - timers.getMaxorStart()) + pbTracker.getPbTag(floor + "_p1"));
            }
            if (timers.getStormEnd() > 0) {
                send(C_STORM + "Storm took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getStormEnd() - timers.getStormStart()) + pbTracker.getPbTag(floor + "_p2"));
            }
            if (timers.getGoldorFight() > 0) {
                send(C_GOLDOR + "Terminals took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getGoldorFight() - timers.getTerminalStart()));
            }
            if (timers.getGoldorEnd() > 0) {
                send(C_GOLDOR + "Goldor took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getGoldorEnd() - timers.getGoldorFight()));
            }
            if (timers.getTerminalStart() > 0 && timers.getGoldorEnd() > 0) {
                send(C_GOLDOR + "P3 (Terminal+Goldor): " + C_VAL + TimeFormatter.formatDungeonTime(timers.getGoldorEnd() - timers.getTerminalStart()) + pbTracker.getPbTag(floor + "_p3"));
            }
            if (timers.getNecronEnd() > 0) {
                send(C_NECRON + "Necron took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getNecronEnd() - timers.getNecronStart()) + pbTracker.getPbTag(floor + "_p4"));
            }
            if (timers.getWitherEnd() > 0) {
                send(C_WITHER + "Wither King took: " + C_VAL + TimeFormatter.formatDungeonTime(timers.getWitherEnd() - timers.getWitherStart()) + pbTracker.getPbTag(floor + "_p5"));
            }
        }
    }

    private static void send(String msg) {
        ChatUtils.sendMessage(msg);
    }
}
