package com.example.queue;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class QueueCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("joinqueue").executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            PlayerQueueManager.joinQueue(player);
            ctx.getSource().sendSuccess(() -> "You joined the swap queue!", true);
            return 1;
        }));
        dispatcher.register(Commands.literal("leavequeue").executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            PlayerQueueManager.leaveQueue(player);
            ctx.getSource().sendSuccess(() -> "You left the swap queue!", true);
            return 1;
        }));
    }
}
