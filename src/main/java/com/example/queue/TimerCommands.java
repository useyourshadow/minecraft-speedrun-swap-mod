package com.example.queue;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import com.example.swap.SwapScheduler;

public class TimerCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("starttimer").executes(ctx -> {
            SwapScheduler.startTimer();
            ctx.getSource().sendSuccess(() -> "Swap timer started!", true);
            return 1;
        }));
    }
}

public class TimerCommands {
}
