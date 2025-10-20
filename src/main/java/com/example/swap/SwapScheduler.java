package com.example.swap;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import com.example.queue.PlayerQueueManager;
import com.example.playerdata.PlayerData;

import java.util.*;

@Mod.EventBusSubscriber
public class SwapScheduler {

    private static final int BASE_SECONDS_PER_SWAP = 60;
    private static final int TICKS_PER_SECOND = 20;

    private static boolean timerActive = false;
    private static boolean randomSwapActive = false;
    private static int swapTimerTicks = BASE_SECONDS_PER_SWAP * TICKS_PER_SECOND;

    // Start speedrun swap (shared countdown)
    public static void startTimer() {
        if (timerActive) return;
        timerActive = true;
        randomSwapActive = false;
        swapTimerTicks = BASE_SECONDS_PER_SWAP * TICKS_PER_SECOND;
    }

    // Start random swap mode (uses same countdown)
    public static void startRandomSwap() {
        if (timerActive) return;
        randomSwapActive = true;
        timerActive = false;
        swapTimerTicks = BASE_SECONDS_PER_SWAP * TICKS_PER_SECOND;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (!timerActive && !randomSwapActive) return;
        if (event.phase != TickEvent.Phase.END) return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        // decrement shared countdown
        swapTimerTicks--;
        int secondsLeft = Math.max(0, swapTimerTicks / TICKS_PER_SECOND);

        // show countdown to all players above name using scoreboard
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.displayClientMessage(Component.literal("§eNext swap in: " + secondsLeft + "s"), true);
        }

        // trigger swap when timer reaches 0
        if (swapTimerTicks <= 0) {
            if (randomSwapActive) randomSwapAll();
            else if (timerActive) swapPlayers();

            // reset timer
            swapTimerTicks = BASE_SECONDS_PER_SWAP * TICKS_PER_SECOND;
        }
    }

    // Random swap: everyone is swapped, no one swaps with themselves
    public static void randomSwapAll() {
        List<ServerPlayer> queue = PlayerQueueManager.getQueue();
        int n = queue.size();
        if (n < 2) return;

        List<ServerPlayer> shuffled;
        boolean valid;

        do {
            shuffled = new ArrayList<>(queue);
            Collections.shuffle(shuffled);

            // check that no one stays in the same position
            valid = true;
            for (int i = 0; i < n; i++) {
                if (queue.get(i) == shuffled.get(i)) {
                    valid = false;
                    break;
                }
            }
        } while (!valid);

        Set<ServerPlayer> swapped = new HashSet<>();
        for (int i = 0; i < n; i++) {
            ServerPlayer original = queue.get(i);
            ServerPlayer target = shuffled.get(i);

            if (swapped.contains(original)) continue;

            PlayerData snapOriginal = new PlayerData(original);
            PlayerData snapTarget = new PlayerData(target);

            var posOriginal = original.position();
            var posTarget = target.position();
            original.teleportTo(target.serverLevel(), posTarget.x, posTarget.y, posTarget.z, target.getYRot(), target.getXRot());
            target.teleportTo(original.serverLevel(), posOriginal.x, posOriginal.y, posOriginal.z, original.getYRot(), original.getXRot());

            snapTarget.restore(original);
            snapOriginal.restore(target);

            swapped.add(original);
            swapped.add(target);

            original.displayClientMessage(Component.literal("§aYou have been randomly swapped!"), false);
            target.displayClientMessage(Component.literal("§aYou have been randomly swapped!"), false);
        }
    }



    // Regular swap: swap first two players in queue
    private static void swapPlayers() {
        List<ServerPlayer> queue = PlayerQueueManager.getQueue();
        if (queue.size() < 2) return;

        ServerPlayer first = queue.get(0);
        ServerPlayer second = queue.get(1);

        PlayerData snapFirst = new PlayerData(first);
        PlayerData snapSecond = new PlayerData(second);

        var posFirst = first.position();
        var posSecond = second.position();
        first.teleportTo(second.serverLevel(), posSecond.x, posSecond.y, posSecond.z, second.getYRot(), second.getXRot());
        second.teleportTo(first.serverLevel(), posFirst.x, posFirst.y, posFirst.z, first.getYRot(), first.getXRot());

        snapSecond.restore(first);
        snapFirst.restore(second);

        first.displayClientMessage(Component.literal("§aYou have been swapped!"), false);
        second.displayClientMessage(Component.literal("§aYou have been swapped!"), false);

        PlayerQueueManager.leaveQueue(first);
        PlayerQueueManager.push(first);
    }
}
