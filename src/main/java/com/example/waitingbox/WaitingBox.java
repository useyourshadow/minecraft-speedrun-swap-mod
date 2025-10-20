package com.example.waitingbox;

import com.example.queue.PlayerQueueManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class WaitingBox {

    public static final int BASE_X = 256;
    public static final int BASE_Y = 256;
    public static final int BASE_Z = 256;
    public static final int SIZE = 20;

    private static final Map<UUID, PlayerSnapshot> savedPlayers = new HashMap<>();

    // Build the waiting box
    public static void create(Level world) {
        BlockPos start = new BlockPos(BASE_X, BASE_Y, BASE_Z);

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                for (int z = 0; z < SIZE; z++) {
                    BlockPos pos = start.offset(x, y, z);

                    if (x == 0 || x == SIZE - 1 || y == 0 || y == SIZE - 1 || z == 0 || z == SIZE - 1) {
                        world.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
                    } else if (y == SIZE / 2 && (x == 1 || x == SIZE - 2) && (z == 1 || z == SIZE - 2)) {
                        world.setBlock(pos, Blocks.TORCH.defaultBlockState(), 3);
                    } else {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
    public static void teleportToBox(ServerPlayer player) {
        savedPlayers.put(player.getUUID(), new PlayerSnapshot(player));
        player.teleportTo(
                player.serverLevel(),
                BASE_X + SIZE / 2.0,
                BASE_Y + 1,
                BASE_Z + SIZE / 2.0,
                player.getYRot(),
                player.getXRot()
        );

        // Set invulnerable and modify abilities
        player.getAbilities().invulnerable = true;
        player.getAbilities().mayBuild = false;
        player.getAbilities().flying = false; // optional
        syncAbilities(player);

        // Max food and saturation
        player.getFoodData().setFoodLevel(20);
    }

    public static void teleportQueueToBox() {
        List<ServerPlayer> queueCopy = PlayerQueueManager.getQueue();

        boolean first = true;
        for (ServerPlayer player : queueCopy) {
            if (first) {
                first = false; // skip the first player
                continue;
            }
            teleportToBox(player);
        }
    }

    // Return player to normal state
    public static void returnFromBox(ServerPlayer player) {
        PlayerSnapshot snapshot = savedPlayers.remove(player.getUUID());
        if (snapshot != null) {
            snapshot.restore(player);
        }
    }

    // Sync abilities to client
    private static void syncAbilities(ServerPlayer player) {
        player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
    }

    // Snapshot class for player state
    private static class PlayerSnapshot {
        boolean invulnerable;
        boolean mayBuild;
        boolean flying;
        int foodLevel;
        float saturation;

        public PlayerSnapshot(ServerPlayer player) {
            this.invulnerable = player.getAbilities().invulnerable;
            this.mayBuild = player.getAbilities().mayBuild;
            this.flying = player.getAbilities().flying;
            this.foodLevel = player.getFoodData().getFoodLevel();
        }

        public void restore(ServerPlayer player) {
            player.getAbilities().invulnerable = invulnerable;
            player.getAbilities().mayBuild = mayBuild;
            player.getAbilities().flying = flying;
            syncAbilities(player);

            player.getFoodData().setFoodLevel(foodLevel);
        }
    }
}
