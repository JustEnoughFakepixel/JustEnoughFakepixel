package com.jef.justenoughfakepixel.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jef.justenoughfakepixel.core.config.gui.config.ConfigEditor;
import com.jef.justenoughfakepixel.core.config.gui.GuiScreenElementWrapper;
import com.jef.justenoughfakepixel.core.config.command.JefCommand;
import com.jef.justenoughfakepixel.core.config.editors.GuiPositionEditor;
import java.util.function.IntSupplier;

import com.jef.justenoughfakepixel.features.diana.DianaLootOverlay;
import com.jef.justenoughfakepixel.features.diana.DianaEventOverlay;
import com.jef.justenoughfakepixel.features.dungeons.DungeonStats;
import com.jef.justenoughfakepixel.features.misc.PerformanceHUD;
import com.jef.justenoughfakepixel.features.misc.SearchBar;
import com.jef.justenoughfakepixel.features.waypoints.WaypointGroupGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


public class JefConfig {

    public static Config feature;
    public static File configDirectory = new File("config/JustEnoughFakepixel");
    private static File configFile;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static final KeyBinding openGuiKey = new KeyBinding(
            "Open JEF GUI",
            Keyboard.KEY_P,
            "JustEnoughFakepixel"
    );

    public static GuiScreen screenToOpen = null;
    private static int screenTicks = 0;
    private static boolean waypointManagerKeyWasDown = false;

    private static boolean registered = false;

    public static void register() {
        if (registered) return;

        init();
        MinecraftForge.EVENT_BUS.register(new JefConfig());
        ClientRegistry.registerKeyBinding(openGuiKey);
        ClientCommandHandler.instance.registerCommand(new JefCommand());

        registered = true;
    }

    public static void init() {
        if (!configDirectory.exists()) {
            configDirectory.mkdirs();
        }

        configFile = new File(configDirectory, "config.json");
        loadConfig();
    }

    private static void loadConfig() {
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Files.newInputStream(configFile.toPath()), StandardCharsets.UTF_8))) {

                feature = GSON.fromJson(reader, Config.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (feature == null) {
            feature = new Config();
            saveConfig();
        }
    }

    public static void saveConfig() {
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(Files.newOutputStream(configFile.toPath()), StandardCharsets.UTF_8))) {

                writer.write(GSON.toJson(feature));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openGui() {
        screenToOpen = new GuiScreenElementWrapper(new ConfigEditor(feature));
    }

    public static void openCategory(String categoryName) {
        screenToOpen = new GuiScreenElementWrapper(new ConfigEditor(feature, categoryName));
    }

    public static void openWaypointGroupGui() {
        screenToOpen = new GuiScreenElementWrapper(new WaypointGroupGui());
    }

    public static void openStatsEditor() {
        if (feature == null) return;
        screenToOpen = new GuiPositionEditor(
                feature.dungeons.statsPos,
                (IntSupplier) DungeonStats::getOverlayWidth,
                (IntSupplier) DungeonStats::getOverlayHeight,
                () -> DungeonStats.renderOverlay(true),
                JefConfig::saveConfig,
                JefConfig::saveConfig
        ).withOverlayScale(feature.dungeons.statsScale);
    }

    public static void openHudEditor() {
        if (feature == null) return;
        screenToOpen = new GuiPositionEditor(
                feature.misc.hudPos,
                (IntSupplier) PerformanceHUD::getOverlayWidth,
                (IntSupplier) PerformanceHUD::getOverlayHeight,
                () -> PerformanceHUD.renderOverlay(true),
                JefConfig::saveConfig,
                JefConfig::saveConfig
        ).withOverlayScale(feature.misc.hudScale);
    }
    public static void openSearchBarEditor() {
        if (feature == null) return;
        screenToOpen = new GuiPositionEditor(
                feature.misc.searchBarPos,
                (IntSupplier) SearchBar::getOverlayWidth,
                (IntSupplier) SearchBar::getOverlayHeight,
                () -> SearchBar.renderOverlay(true),
                JefConfig::saveConfig,
                JefConfig::saveConfig
        );
    }

    public static void openDianaEventEditor() {
        if (feature == null) return;
        screenToOpen = new GuiPositionEditor(
                feature.diana.eventOverlayPos,
                (IntSupplier) DianaEventOverlay::getOverlayWidth,
                (IntSupplier) DianaEventOverlay::getOverlayHeight,
                () -> DianaEventOverlay.renderOverlay(true),
                JefConfig::saveConfig,
                JefConfig::saveConfig
        ).withOverlayScale(feature.diana.overlayScale);
    }

    public static void openDianaLootEditor() {
        if (feature == null) return;
        screenToOpen = new GuiPositionEditor(
                feature.diana.lootOverlayPos,
                (IntSupplier) DianaLootOverlay::getOverlayWidth,
                (IntSupplier) DianaLootOverlay::getOverlayHeight,
                () -> DianaLootOverlay.renderOverlay(true),
                JefConfig::saveConfig,
                JefConfig::saveConfig
        ).withOverlayScale(feature.diana.overlayScale);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;

        if (screenToOpen != null) {
            screenTicks++;
            if (screenTicks == 5) {
                Minecraft.getMinecraft().displayGuiScreen(screenToOpen);
                screenTicks = 0;
                screenToOpen = null;
            }
        }

        if (openGuiKey.isPressed() && Minecraft.getMinecraft().currentScreen == null) {
            openGui();
        }

        boolean managerKeyDown = feature != null
                && feature.waypoints.waypointManagerKey != org.lwjgl.input.Keyboard.KEY_NONE
                && org.lwjgl.input.Keyboard.isKeyDown(feature.waypoints.waypointManagerKey);
        if (managerKeyDown && !waypointManagerKeyWasDown && Minecraft.getMinecraft().currentScreen == null) {
            openWaypointGroupGui();
        }
        waypointManagerKeyWasDown = managerKeyDown;
    }
}