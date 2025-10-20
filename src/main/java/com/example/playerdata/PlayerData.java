package com.example.playerdata;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    public float health;
    public int food;
    public int air;
    public int fireTicks;
    public boolean isFlying;
    public int xpTotal;
    public int xpLevel;
    public float xpProgress;
    public List<MobEffectInstance> effects;
    public List<ItemStack> inventory;
    public List<ItemStack> armor;
    public ItemStack offhand;

    public PlayerData(ServerPlayer p) {
        health = p.getHealth();
        food = p.getFoodData().getFoodLevel();
        air = p.getAirSupply();
        fireTicks = p.getRemainingFireTicks();
        isFlying = p.getAbilities().flying;
        xpTotal = p.totalExperience;
        xpLevel = p.experienceLevel;
        xpProgress = p.experienceProgress;

        effects = new ArrayList<>(p.getActiveEffects());
        inventory = new ArrayList<>();
        armor = new ArrayList<>();
        for (ItemStack stack : p.getInventory().items) inventory.add(stack.copy());
        for (ItemStack stack : p.getInventory().armor) armor.add(stack.copy());
        offhand = p.getInventory().offhand.get(0).copy();
    }

    public void restore(ServerPlayer p) {
        // Restore inventory
        p.getInventory().clearContent();
        for (int i = 0; i < inventory.size(); i++) p.getInventory().items.set(i, inventory.get(i).copy());
        for (int i = 0; i < armor.size(); i++) p.getInventory().armor.set(i, armor.get(i).copy());
        p.getInventory().offhand.set(0, offhand.copy());
        p.inventoryMenu.broadcastChanges();

        // Restore core stats
        p.setHealth(health);
        p.getFoodData().setFoodLevel(food);
        p.setAirSupply(air);
        p.setRemainingFireTicks(fireTicks);
        p.getAbilities().flying = isFlying;

        // Restore XP
        p.totalExperience = xpTotal;
        p.experienceLevel = xpLevel;
        p.experienceProgress = xpProgress;

        // Restore effects
        p.removeAllEffects();
        for (MobEffectInstance effect : effects) {
            p.addEffect(new MobEffectInstance(effect));
        }
    }
}
