package com.example.queue;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import com.example.swap.SwapScheduler;
import com.example.queue.PlayerQueueManager;
import com.example.waitingbox.WaitingBox;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class TimerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        // Start Speedrun Swap
        dispatcher.register(Commands.literal("startspeedrunswap").executes(ctx -> {
            moveToWaitingBox();
            SwapScheduler.startTimer();
            ctx.getSource().sendSuccess(() -> Component.literal("Speedrun swap timer started!"), true);
            return 1;
        }));

        // Start Random Swap
        dispatcher.register(Commands.literal("startrandomswap").executes(ctx -> {
            SwapScheduler.startRandomSwap();
            ctx.getSource().sendSuccess(() -> Component.literal("Random swap timer started!"), true);
            return 1;
        }));
    }
       private static void moveToWaitingBox() {
        if (PlayerQueueManager.getQueue().isEmpty()) return;
        ServerPlayer first = PlayerQueueManager.getQueue().get(0);
        WaitingBox.create(first.level());
        if (PlayerQueueManager.queueSize() < 2) return;
        WaitingBox.teleportQueueToBox();
    }
}
