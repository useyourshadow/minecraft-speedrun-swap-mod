package com.example.examplemod;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraft.world.entity.player.Player;
import com.example.playerdata.PlayerDataStorage;
import com.example.queue.QueueCommands;
import com.example.queue.TimerCommand;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;


@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "playerswapmod";

    public ExampleMod() {
        // Register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Register Forge event bus
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Player> event) {
        event.addCapability(ResourceLocation.tryParse(MODID + ":player_data"), new PlayerDataStorage());
    }

    @SubscribeEvent
    public void serverStarting(ServerStartingEvent event) {
        QueueCommands.register(event.getServer().getCommands().getDispatcher());
        TimerCommand.register(event.getServer().getCommands().getDispatcher());
    }
}
