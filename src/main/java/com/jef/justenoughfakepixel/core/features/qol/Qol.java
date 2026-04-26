package com.jef.justenoughfakepixel.core.features.qol;

import com.google.gson.annotations.Expose;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigAnnotations.*;
import com.jef.justenoughfakepixel.core.config.utils.Position;
import com.jef.justenoughfakepixel.core.features.qol.*;

public class Qol {

    @Expose
    @Category(name = "Damage Splashes", desc = "Settings for damage number nametags")
    public DamageSplashesConfig damageSplashes = new DamageSplashesConfig();

    @Expose
    @Category(name = "Enchant Parser", desc = "Settings for enchants and layout")
    public EnchantParserConfig enchantParser = new EnchantParserConfig();

    @Expose
    @Category(name = "Gyro Wand Ring", desc = "Settings for the Gyrokinetic Wand AoE ring")
    public GyroWandConfig gyroWandConfig = new GyroWandConfig();

    @Expose
    @Category(name = "Item Cooldown Overlay", desc = "Settings for the item ability cooldown overlay")
    public ItemCooldownConfig itemCooldown = new ItemCooldownConfig();

    @Expose
    @Category(name = "Ability Timer Overlay", desc = "Settings for the item ability active-duration overlay")
    public AbilityTimerConfig abilityTimer = new AbilityTimerConfig();

    @Expose
    @Category(name = "Invincibility Overlay", desc = "Settings for the post-save invincibility timer overlay")
    public InvincibilityConfig invincibility = new InvincibilityConfig();

    @Expose
    @Category(name = "Block Selection Overlay", desc = "Customize the block selection highlight")
    public BlockSelectionConfig blockSelection = new BlockSelectionConfig();

    // ── standalone options (no accordion) ───────────────────────────────────
    @Expose
    @ConfigOption(name = "Roman Numerals", desc = "Converts Roman numerals to integers in tooltips and tab list")
    @ConfigEditorBoolean
    public boolean romanNumerals = true;

    @Expose
    @ConfigOption(name = "Prevent Cursor Reset", desc = "Prevents the mouse cursor from resetting when opening GUIs")
    @ConfigEditorBoolean
    public boolean preventCursorReset = true;

    @Expose
    @ConfigOption(name = "Skyblock ID", desc = "Shows the skyblock item ID at the bottom of item tooltips")
    @ConfigEditorBoolean
    public boolean showSkyblockId = true;

    @Expose
    @ConfigOption(name = "Disable Enchant Glint", desc = "Removes the enchantment glint effect")
    @ConfigEditorBoolean
    public boolean disableEnchantGlint = false;

    @Expose
    @ConfigOption(name = "Brewing helper", desc = "Highlights brewing stands when done brewing")
    @ConfigEditorBoolean
    public boolean colorBrewingStands = true;

    @Expose
    @ConfigOption(name = "Missing Enchants", desc = "Hold SHIFT on an enchanted item to see missing enchants")
    @ConfigEditorBoolean
    public boolean missingEnchants = true;

    @Expose
    @ConfigOption(name = "Confirm Disconnect", desc = "Makes you click twice to disconnect")
    @ConfigEditorBoolean
    public boolean confirmDisconnect = true;

    // ── flat fields kept for runtime code compatibility ──────────────────────
    @Expose public boolean hideCritSplashes = false;
    @Expose public boolean hideNonCritSplashes = false;
    @Expose public boolean enchantHighlight = true;
    @Expose public int enchantLayout = 0;
    @Expose public boolean enchantChroma = true;
    @Expose public int enchantChromaSpeed = 1000;
    @Expose public int enchantChromaMode = 1;
    @Expose public float enchantChromaSize = 120f;
    @Expose public String enchantPoorColor = "0:170:170:170:170";
    @Expose public String enchantGoodColor = "0:255:85:255:85";
    @Expose public String enchantGreatColor = "0:255:85:85:255";
    @Expose public String enchantPerfectColor = "0:255:255:85:255";
    @Expose public String enchantUltimateColor = "0:255:255:85:255";
    @Expose public boolean gyroWand = true;
    @Expose public float gyroWandThickness = 2f;
    @Expose public boolean itemCooldownOverlay = true;
    @Expose public String itemCooldownBgColor = "0:136:0:0:0";
    @Expose public int itemCooldownCornerRadius = 4;
    @Expose public float itemCooldownScale = 1f;
    @Expose public boolean itemCooldownShowWhenEmpty = false;
    @Expose public Position itemCooldownPos = new Position(-4, 4, true, false);
    @Expose public boolean itemAbilityTimerOverlay = true;
    @Expose public String itemAbilityTimerBgColor = "0:136:0:0:0";
    @Expose public int itemAbilityTimerCornerRadius = 4;
    @Expose public float itemAbilityTimerScale = 1f;
    @Expose public boolean itemAbilityTimerShowWhenEmpty = false;
    @Expose public Position itemAbilityTimerPos = new Position(-4, 40, true, false);
    @Expose public boolean itemInvincibilityOverlay = true;
    @Expose public String itemInvincibilityBgColor = "0:136:0:0:0";
    @Expose public int itemInvincibilityCornerRadius = 4;
    @Expose public float itemInvincibilityScale = 1f;
    @Expose public boolean itemInvincibilityShowWhenEmpty = false;
    @Expose public Position itemInvincibilityPos = new Position(-4, 76, true, false);
    @Expose public boolean blockSelectionOverlay = false;
    @Expose public int blockSelectionMode = 1;
    @Expose public float blockSelectionThickness = 2f;
    @Expose public String blockSelectionColor = "180:255:255:255:255";
}
