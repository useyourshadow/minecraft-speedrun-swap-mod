package com.example.queue;

import net.minecraft.server.level.ServerPlayer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

public class PlayerQueueManager {
    private static final Queue<ServerPlayer> queue = new LinkedList<>();

    public static int queueSize() {
        return queue.size();
    }

    public static void push(ServerPlayer player) {
        if (!queue.contains(player)) {
            queue.add(player);
        }
    }
    public static boolean leaveQueue(ServerPlayer player) {
        return queue.remove(player);
    }
    public static ServerPlayer popFirstPlayer() {
        if (queue.isEmpty()) return null;
        return queue.poll();
    }


    public static ServerPlayer[] popTwoPlayers() {
        if (queue.size() < 2) return null;
        return new ServerPlayer[]{ queue.poll(), queue.poll() };
    }

    public static void pushBack(ServerPlayer a, ServerPlayer b) {
        queue.add(a);
        queue.add(b);
    }
    public static List<ServerPlayer> getQueue() {
        return new ArrayList<>(queue);
    }
    public static ServerPlayer[] peekTwoPlayers() {
        if (queue.size() < 2) return null;
        ServerPlayer[] players = new ServerPlayer[2];
        int i = 0;
        for (ServerPlayer p : queue) {
            players[i++] = p;
            if (i == 2) break;
        }
        return players;
    }
}
