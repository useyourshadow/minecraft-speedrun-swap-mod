package com.example.queue;

import net.minecraft.server.level.ServerPlayer;
import java.util.LinkedList;
import java.util.Queue;

public class PlayerQueueManager {
    private static final Queue<ServerPlayer> queue = new LinkedList<>();

    public static int queueSize() {
        return queue.size();
    }

    public static void push(ServerPlayer player) {
        queue.add(player);
    }

    public static ServerPlayer[] popTwoPlayers() {
        if (queue.size() < 2) return null;
        return new ServerPlayer[]{ queue.poll(), queue.poll() };
    }

    public static void pushBack(ServerPlayer a, ServerPlayer b) {
        queue.add(a);
        queue.add(b);
    }
}
