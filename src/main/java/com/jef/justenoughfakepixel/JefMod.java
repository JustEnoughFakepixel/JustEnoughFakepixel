package com.jef.justenoughfakepixel;

import com.jef.justenoughfakepixel.config.JefConfig;
import com.jef.justenoughfakepixel.features.ItemStackUtils;
import com.jef.justenoughfakepixel.config.SimpleCommandFilter;
import com.jef.justenoughfakepixel.features.DamageSplashes;
import com.jef.justenoughfakepixel.features.BrewingStandHelper;
import com.jef.justenoughfakepixel.features.mining.FetchurHelper;
import com.jef.justenoughfakepixel.features.PartyCommands;
import com.jef.justenoughfakepixel.features.PerformanceHUD;
import com.jef.justenoughfakepixel.features.SkillXpDisplay;
import com.jef.justenoughfakepixel.features.dungeons.BloodMobDisplay;
import com.jef.justenoughfakepixel.features.dungeons.DungeonStats;
import com.jef.justenoughfakepixel.features.SearchBar;
import com.jef.justenoughfakepixel.features.SkyblockIdTooltip;
import com.jef.justenoughfakepixel.features.waypoints.WaypointCommand;
import com.jef.justenoughfakepixel.features.waypoints.WaypointRenderer;
import com.jef.justenoughfakepixel.features.waypoints.WaypointStorage;
import com.jef.justenoughfakepixel.repo.RepoHandler;
import net.minecraftforge.client.ClientCommandHandler;
import com.jef.justenoughfakepixel.repo.JefRepo;
import com.jef.justenoughfakepixel.features.VersionChecker;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Mod(
        modid = JefMod.MODID,
        name = JefMod.NAME,
        version = JefMod.VERSION,
        clientSideOnly = true
)
public class JefMod {

    public static final String MODID = "justenoughfakepixel";
    public static final String NAME  = "JustEnoughFakepixel";
    public static final String VERSION = "1.2.0";

    @SubscribeEvent
    public void onServerJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        RepoHandler.refresh(JefRepo.KEY_PLAYERSIZES);
        RepoHandler.refresh(JefRepo.KEY_UPDATE);
    }

    // Make config accessible to features
    public static JefConfig config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        JefConfig.init();
        JefRepo.init();

        // Point waypoint storage at the same config directory JEF uses
        WaypointStorage.getInstance().initFile(JefConfig.configDirectory);
    }

    @Mod.EventHandler
    public void clientInit(FMLInitializationEvent event) {
        JefConfig.register();
        WaypointStorage.getInstance().load();
        MinecraftForge.EVENT_BUS.register(this);


        // Register features
        MinecraftForge.EVENT_BUS.register(new SearchBar());
        MinecraftForge.EVENT_BUS.register(new DamageSplashes());
        MinecraftForge.EVENT_BUS.register(new BloodMobDisplay());
        MinecraftForge.EVENT_BUS.register(new DungeonStats());
        MinecraftForge.EVENT_BUS.register(new PartyCommands());
        MinecraftForge.EVENT_BUS.register(new FetchurHelper());
        MinecraftForge.EVENT_BUS.register(new PerformanceHUD());
        MinecraftForge.EVENT_BUS.register(new SkillXpDisplay());
        MinecraftForge.EVENT_BUS.register(new SkyblockIdTooltip());
        MinecraftForge.EVENT_BUS.register(new SimpleCommandFilter());
        MinecraftForge.EVENT_BUS.register(new WaypointRenderer());
        MinecraftForge.EVENT_BUS.register(new VersionChecker());
        MinecraftForge.EVENT_BUS.register(new BrewingStandHelper());
        MinecraftForge.EVENT_BUS.register(new ItemStackUtils());
        ClientCommandHandler.instance.registerCommand(new WaypointCommand());
    }
}