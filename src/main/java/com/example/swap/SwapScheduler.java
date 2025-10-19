package com.example.swap;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import com.example.playerdata.PlayerDataProvider;
import com.example.playerdata.PlayerData;
import com.example.queue.PlayerQueueManager;
import com.example.examplemod.Config;

import java.util.List;

@Mod.EventBusSubscriber
public class SwapScheduler {

    private static int tickCounter = 0;
    private static boolean timerActive = false;
    private static final int TICKS_PER_SECOND = 20;
    private static final int SECONDS_PER_SWAP = Config.swapIntervalMinutes * 60;
    private static final int TICKS_PER_SWAP = TICKS_PER_SECOND * SECONDS_PER_SWAP;


    public static void startTimer() {
        tickCounter = 0;
        timerActive = true;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (!timerActive || event.phase != TickEvent.Phase.END) return;

        tickCounter++;

        // Show countdown only to players in the queue
        int secondsLeft = Math.max(0, (TICKS_PER_SWAP - tickCounter) / TICKS_PER_SECOND);
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (PlayerQueueManager.queueSize() > 0) {
                player.displayClientMessage(Component.literal("§eSwap in: " + secondsLeft + "s"), true);
            }
        }

        if (tickCounter >= TICKS_PER_SWAP) {
            tickCounter = 0;
            swapPlayers();
        }
    }

    private static void swapPlayers() {
        ServerPlayer[] players = PlayerQueueManager.popTwoPlayers();
        if (players == null) return;

        ServerPlayer a = players[0];
        ServerPlayer b = players[1];

        // Swap positions
        Vec3 posA = a.position();
        Vec3 posB = b.position();
        a.teleportTo(b.serverLevel(), posB.x, posB.y, posB.z, b.getYRot(), b.getXRot());
        b.teleportTo(a.serverLevel(), posA.x, posA.y, posA.z, a.getYRot(), a.getXRot());

        // Swap inventories
        CompoundTag invA = a.getInventory().save(new CompoundTag());
        CompoundTag invB = b.getInventory().save(new CompoundTag());
        a.getInventory().load(invB);
        b.getInventory().load(invA);

        // Swap custom PlayerData
        a.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(dataA ->
                b.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(dataB -> {
                    CompoundTag tagA = new CompoundTag();
                    CompoundTag tagB = new CompoundTag();
                    dataA.saveNBTData(tagA);
                    dataB.saveNBTData(tagB);
                    dataA.loadNBTData(tagB);
                    dataB.loadNBTData(tagA);
                })
        );

        // Push players back to queue
        PlayerQueueManager.pushBack(a, b);
    }
}
