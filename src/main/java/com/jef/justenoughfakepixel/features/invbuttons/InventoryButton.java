package com.jef.justenoughfakepixel.features.invbuttons;

import com.google.gson.annotations.Expose;

public class InventoryButton {
    @Expose public int x;
    @Expose public int y;
    @Expose public boolean anchorRight;
    @Expose public boolean anchorBottom;
    @Expose public boolean playerInvOnly;
    @Expose public int backgroundIndex;
    @Expose public String icon;
    @Expose public String command;

    public InventoryButton() {
        this.command = "";
        this.icon = "";
    }

    public InventoryButton(int x, int y, boolean anchorRight, boolean anchorBottom,
                           boolean playerInvOnly, int backgroundIndex, String icon, String command) {
        this.x = x;
        this.y = y;
        this.anchorRight = anchorRight;
        this.anchorBottom = anchorBottom;
        this.playerInvOnly = playerInvOnly;
        this.backgroundIndex = backgroundIndex;
        this.icon = icon != null ? icon : "";
        this.command = command != null ? command : "";
    }

    public boolean isActive() {
        return command != null && !command.trim().isEmpty();
    }
}
