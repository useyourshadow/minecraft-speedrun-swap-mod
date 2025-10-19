package com.example.playerdata;


import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class PlayerDataProvider implements ICapabilityProvider {
    public static final Capability<PlayerData> PLAYER_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    private final PlayerData instance = new PlayerData();
    private final LazyOptional<PlayerData> optional = LazyOptional.of(() -> instance);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == PLAYER_DATA ? optional.cast() : LazyOptional.empty();
    }
}
