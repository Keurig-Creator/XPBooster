package com.keurig.xpbooster.base.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu implements InventoryHolder {


    private final Player player;
    public Inventory inventory;

    public Menu(Player player) {
        this.player = player;
    }

    public abstract void handle(InventoryClickEvent e);

    public abstract int getSlots();

    public abstract void setup(Inventory inventory);

    public abstract boolean cancelClicks();

    private List<ItemAction> actions = new ArrayList<>();

    public Inventory open() {
        inventory = Bukkit.createInventory(this, getSlots(), "test");
        setup(inventory);

        player.closeInventory();
        player.openInventory(inventory);

        return inventory;
    }

    public void addAction(ItemAction itemAction) {

        
        actions.add(itemAction);

    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
