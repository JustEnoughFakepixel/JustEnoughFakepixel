package com.jef.justenoughfakepixel.core.features.misc;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.core.features.misc.*;

public class Misc {

    @Expose
    @Category(name = "Performance HUD", desc = "Settings for the performance HUD")
    public PerformanceHudConfig performanceHudConfig = new PerformanceHudConfig();

    @Expose
    @Category(name = "Search Bar", desc = "Search bar settings")
    public SearchBarConfig searchBarConfig = new SearchBarConfig();

    @Expose
    @Category(name = "Current Pet", desc = "Shows your active pet as a HUD overlay")
    public CurrentPetConfig currentPet = new CurrentPetConfig();

    @Expose
    @Category(name = "Item Pickup Log", desc = "Settings for the item pickup log")
    public ItemPickupLogConfig itemPickupLogConfig = new ItemPickupLogConfig();

    @Expose
    @Category(name = "Inventory Buttons", desc = "Clickable shortcut buttons on inventories")
    public InvButtonsConfig invButtons = new InvButtonsConfig();

    // ── standalone options ───────────────────────────────────────────────────
    @Expose
    @ConfigOption(name = "Item Stack Tips", desc = "Shows enchant levels on books and floor numbers on Catacombs passes")
    @ConfigEditorBoolean
    public boolean itemStackTips = true;

    @Expose
    @ConfigOption(name = "Party Finder Floor Tip", desc = "Shows floor label (F1-F7, M1-M7) on listings in the Party Finder")
    @ConfigEditorBoolean
    public boolean partyFinderFloorTip = true;

    @Expose
    @ConfigOption(name = "Skill XP Display", desc = "Hold SHIFT on a skill item to see XP remaining to max level")
    @ConfigEditorBoolean
    public boolean skillXpDisplay = true;

    @Expose
    @ConfigOption(name = "No Swap Animation", desc = "Removes the item lowering animation when switching hotbar slots")
    @ConfigEditorBoolean
    public boolean noItemSwitchAnimation = true;

    @Expose
    @ConfigOption(name = "Show Own Nametag", desc = "Shows your own nametag in third person")
    @ConfigEditorBoolean
    public boolean showOwnNametag = true;

    @Expose
    @ConfigOption(name = "Disable Entity Fire", desc = "Hides the fire overlay rendered on burning entities")
    @ConfigEditorBoolean
    public boolean disableEntityFire = true;

    @Expose
    @ConfigOption(name = "SkyBlock XP in Chat", desc = "Sends SkyBlock XP gains from the action bar into chat")
    @ConfigEditorBoolean
    public boolean skyblockXpInChat = false;

    // ── flat fields kept for runtime code compatibility ──────────────────────
    @Expose public boolean performanceHud = false;
    @Expose public boolean hudShowFps = true;
    @Expose public boolean hudShowTps = true;
    @Expose public boolean hudShowPing = true;
    @Expose public boolean hudShowCoords = false;
    @Expose public boolean hudShowRotation = false;
    @Expose public boolean hudVertical = true;
    @Expose public String hudBgColor = "0:136:0:0:0";
    @Expose public int hudCornerRadius = 4;
    @Expose public float hudScale = 1f;
    @Expose public Position hudPos = new Position(2, 2);
    @Expose public boolean searchBar = true;
    @Expose public String searchBarHighlightColor = "0:102:255:0:0";
    @Expose public Position searchBarPos = new Position(0, -20, true, true);
    @Expose public boolean showCurrentPet = true;
    @Expose public String currentPetBgColor = "0:0:0:0:0";
    @Expose public int currentPetCornerRadius = 4;
    @Expose public float currentPetScale = 1.5f;
    @Expose public Position currentPetPos = new Position(18, 14);
    @Expose public boolean itemPickupLog = true;
    @Expose public String itemPickupLogBgColor = "160:0:0:0:0";
    @Expose public int itemPickupLogCornerRadius = 4;
    @Expose public float itemPickupLogScale = 1f;
    @Expose public Position itemPickupLogPos = new Position(2, 60);
    @Expose public boolean enableInvButtons = true;
    @Expose public int invButtonClickType = 0;
    @Expose public int invButtonTooltipDelay = 600;
}
