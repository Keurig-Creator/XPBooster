package com.keurig.xpbooster.base.menu;

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

import java.util.List;

@Getter
public abstract class Menu implements InventoryHolder {

    @Setter
    protected PlayerMenu playerMenu;
    protected Inventory inventory;
    protected List<ActionItem> actions;

    @Setter
    protected Menu parent;

    public Menu() {
//        this.title = title;
//        this.size = size;
//        this.items = items;
//        this.parent = parent;
    }


    public void addItems(ActionItem... actions) {
        this.actions = List.of(actions);
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
        if (e.getSlot() < 0 || e.getSlot() >= actions.size() || getAction(e.getSlot()) == null) {
            return;
        }

        getAction(e.getSlot()).onClick(e);

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

    public void setActionItems(Inventory inventory) {
        for (int i = 0; i < actions.size(); i++) {
            if (i >= inventory.getSize()) {
                break;
            }

            if (getAction(i) != null) {
                inventory.setItem(i, getAction(i).getItem());
            }
        }
    }

    public boolean isOpenMenu(HumanEntity player) {
        if (player.getOpenInventory() == null || player.getOpenInventory().getType() != InventoryType.CHEST || (!(player.getOpenInventory().getTopInventory().getHolder() instanceof Menu))) {
            return false;
        }

        return equals((player.getOpenInventory().getTopInventory().getHolder() instanceof Menu));
    }

    public ActionItem getAction(int index) {
        if (actions.size() >= index) {
            return actions.get(index);
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
