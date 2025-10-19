package com.example.playerdata;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.ContainerHelper;
import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    private int health;
    private int oxygen;
    private int hunger;
    private boolean switched;
    private boolean proned;
    private List<ItemStack> inventory = new ArrayList<>();

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health;}

    public int getOxygen() { return oxygen; }
    public void setOxygen(int oxygen){this.oxygen = oxygen;}

    public int getHunger(){return hunger;}
    public void setHunger(int hunger){this.hunger = hunger;}

    public boolean isSwitched(){return switched;}
    public void setSwitched(boolean switched){this.switched = switched;}

    public boolean isProned(){return proned;}
    public void setProned(boolean proned){this.proned = proned;}

    public List<ItemStack> getInventory(){return inventory;}
    public void setInventory(List<ItemStack> inventory){this.inventory = inventory;}

    // Minecraft saves data in NBT tags so we use this to serialize
    public void saveNBTData(CompoundTag tag){
        tag.putInt("Health",health);
        tag.putInt("Oxygen",oxygen);
        tag.putInt("Hunger",hunger);
        tag.putBoolean("Switched",switched);
        tag.putBoolean("Proned",proned);

        ListTag listTag = new ListTag();
        for (ItemStack stack : inventory){
            CompoundTag compoundTag = new CompoundTag();
            stack.save(stackTag);
            listTag.add(stackTag);
        }
        tag.put("Inventory",listTag);
    }

    public void loadNBTData(CompoundTag tag){
        health = tag.getInt("Health");
        oxygen = tag.getInt("Oxygen");
        hunger = tag.getInt("Hunger");
        switched = tag.getBoolean("Switched");
        proned = tag.getBoolean("Proned");

        inventory.clear();
        ListTag listTag = tag.getList("Inventory",10);
        for (int i= 0; i<listTag.size(); i++){
            CompoundTag stackTag = listTag.getCompound(i);
            inventory.add(ItemStack.of(stackTag));
        }
    }
}
