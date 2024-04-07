package com.keurig.xpbooster.base.menu.data;

import com.keurig.xpbooster.base.menu.item.ActionItem;
import com.keurig.xpbooster.base.menu.item.ItemClickEvent;
import com.keurig.xpbooster.util.Chat;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class Menu implements InventoryHolder {

    @Setter
    protected PlayerMenu playerMenu;
    protected Inventory inventory;
    protected HashMap<Integer, ActionItem> actions = new HashMap<>();

    @Setter
    protected Menu parent;

    public Menu() {
//        this.title = title;
//        this.size = size;
//        this.items = items;
//        this.parent = parent;
    }


    public void addAction(ActionItem action, int slot) {
        action.setSlot(slot);
        actions.put(slot, action);
        setActionItems(inventory);
    }

    public boolean hasParent() {
        return parent != null;
    }

    public abstract int getSize();

    public abstract String getTitle();

    public abstract void onClick(ItemClickEvent e);

    public abstract void setup(Inventory inventory);

    public abstract boolean cancelClicks();


    public void handle(ItemClickEvent e) {
        // Handle regular click
        onClick(e);

        // Handle action click
        if (e.getSlot() < 0 || actions.get(e.getSlot()) == null) {
            return;
        }

        if (!e.isLeftClick())
            return;


        actions.get(e.getSlot()).onClick(e);

        if (e.isGoback() && parent != null) {
//            getParent().open(e.getPlayer());
            e.setGoback(false);
        } else if (e.isUpdate()) {

            update(e.getPlayer());
            e.setUpdate(false);
        }

    }

    public void removeAction(ActionItem action) {
        actions.remove(action);
    }

    public Inventory apply() {
        inventory = Bukkit.createInventory(this, getSize(), Chat.color(getTitle()));

        return inventory;
    }

    public void update(Player player) {
        if (isOpenMenu(player)) {
            setActionItems(player.getOpenInventory().getTopInventory());
            player.updateInventory();
        }
    }

    public int getNextSlot(int slot) {
        if (slot != -1) {
            return slot;
        }

        ItemStack[] contents = inventory.getContents();
        int guiSize = contents.length; // Use the actual size of the inventory

        // Iterate over slots excluding the top, bottom, left, and right columns
        for (int index = 0; index < guiSize; index++) {
            // Skip the first and last 9 slots (top and bottom rows)
            if (index < 9 || index >= guiSize - 9) {
                continue;
            }

            // Calculate the column index
            int column = index % 9;

            // Skip the leftmost and rightmost columns
            if (column == 0 || column == 8) {
                continue;
            }

            if (contents[index] == null) {
                return index;
            }
        }

        return -1; // If no empty slot found in the GUI
    }

    public void setActionItems(Inventory inventory) {

        for (Map.Entry<Integer, ActionItem> entry : actions.entrySet()) {
            if (getAction(entry.getKey()) != null) {
                if (getAction(entry.getKey()).getSlot() == -1) {
                    inventory.addItem(getAction(entry.getKey()).getItem());
                } else {
                    inventory.setItem(getAction(entry.getKey()).getSlot(), getAction(entry.getKey()).getItem());
                }
            }
        }
    }

    public boolean isOpenMenu(HumanEntity player) {
        if (player.getOpenInventory() == null || player.getOpenInventory().getType() != InventoryType.CHEST || (!(player.getOpenInventory().getTopInventory().getHolder() instanceof Menu))) {
            return false;
        }

        return equals((player.getOpenInventory().getTopInventory().getHolder() instanceof Menu));
    }

    public ActionItem getActionSlot(int slot) {
        return actions.get(slot);
    }

    public ActionItem getAction(int index) {
        for (Map.Entry<Integer, ActionItem> entry : actions.entrySet()) {
            if (entry.getValue().getSlot() == index) {
                return entry.getValue();
            }
        }
        return null; // Or throw an exception if desired
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
