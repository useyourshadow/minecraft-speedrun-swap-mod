package com.example.examplemod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // Swap interval in minutes
    public static final ForgeConfigSpec.IntValue SWAP_INTERVAL_MINUTES = BUILDER
            .comment("Time between swaps in minutes")
            .defineInRange("swapIntervalMinutes", 1, 1, 60);

    // Auto-start timer
    public static final ForgeConfigSpec.BooleanValue AUTO_START_TIMER = BUILDER
            .comment("Automatically start the swap timer when 2 or more players join the queue")
            .define("autoStartTimer", true);

    // Max queue size
    public static final ForgeConfigSpec.IntValue MAX_QUEUE_SIZE = BUILDER
            .comment("Maximum number of players allowed in the swap queue. 0 = unlimited")
            .defineInRange("maxQueueSize", 0, 0, 1000);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    // Local static fields for easy access
    public static int swapIntervalMinutes;
    public static boolean autoStartTimer;
    public static int maxQueueSize;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        swapIntervalMinutes = SWAP_INTERVAL_MINUTES.get();
        autoStartTimer = AUTO_START_TIMER.get();
        maxQueueSize = MAX_QUEUE_SIZE.get();
    }
}