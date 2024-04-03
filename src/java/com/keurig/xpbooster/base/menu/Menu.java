package com.keurig.xpbooster.base.menu;

import com.keurig.xpbooster.util.Chat;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu implements InventoryHolder {

    protected Player player;
    protected Inventory inventory;
    protected final ActionItem[] items;
    protected final String title;
    protected final int size;

    @Getter
    protected final Menu parent;

    public Menu(String title, int size, Menu parent, ActionItem... items) {
        this.title = title;
        this.size = size;
        this.items = items;
        this.parent = parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public abstract void onClick(ItemClickEvent e);

    public abstract void setup(Inventory inventory);

    public abstract boolean cancelClicks();

    private final List<ItemClickEvent> actions = new ArrayList<>();

    public Inventory open(Player player) {
        this.player = player;

        inventory = Bukkit.createInventory(this, size, Chat.color(title));
        setup(inventory);
        setActionItems(inventory);

        player.closeInventory();
        player.openInventory(inventory);

        return inventory;
    }

    public void handle(ItemClickEvent e) {
        // Handle regular click
        onClick(e);

        // Handle action click
        if (e.getSlot() < 0 || e.getSlot() >= items.length || getAction(e.getSlot()) == null) {
            return;
        }

        getAction(e.getSlot()).onClick(e);

        if (e.isGoback() && parent != null) {
            getParent().open(e.getPlayer());
            e.setGoback(false);
        } else if (e.isUpdate()) {
            
            update(e.getPlayer());
            e.setUpdate(false);
        }

    }

    public void update(Player player) {
        if (isOpenMenu(player)) {
            setActionItems(player.getOpenInventory().getTopInventory());
            player.updateInventory();
        }
    }

    public void setActionItems(Inventory inventory) {
        for (int i = 0; i < items.length; i++) {
            if (i >= inventory.getSize()) {
                break;
            }

            if (getAction(i) != null) {
                inventory.setItem(i, getAction(i).getItem());
            }
        }
    }

    public boolean isOpenMenu(Player player) {
        if (player.getOpenInventory() == null || player.getOpenInventory().getType() != InventoryType.CHEST || (!(player.getOpenInventory().getTopInventory().getHolder() instanceof Menu))) {
            return false;
        }

        return equals((player.getOpenInventory().getTopInventory().getHolder() instanceof Menu));
    }

    public ActionItem getAction(int index) {
        if (items.length >= index) {
            return items[index];
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
