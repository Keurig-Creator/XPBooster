package com.keurig.xpbooster.base.menu.item;

import com.keurig.xpbooster.base.menu.Menu;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class ItemClickEvent {

    private ItemStack item;
    private final InventoryView view;
    private final Menu menu;
    private final Player player;
    private final InventoryClickEvent e;
    private final ClickType clickType;
    private final int slot;
    private boolean update;
    private boolean goback;

    public ItemClickEvent(ItemStack item, InventoryView view, Menu menu, Player player, InventoryClickEvent e, ClickType clickType, int slot) {
        this.item = item;
        this.view = view;
        this.menu = menu;
        this.player = player;
        this.e = e;
        this.clickType = clickType;
        this.slot = slot;
    }

    public Inventory getInventory() {
        return view.getTopInventory();
    }

    public boolean isLeftClick() {
        return clickType.isLeftClick();
    }

    public boolean isRightClick() {
        return clickType.isRightClick();
    }

    public boolean isShiftClick() {
        return clickType.isShiftClick();
    }

    public void setCurrentItem(ItemStack item) {
        setUpdate(true);
        this.item = item;
    }

    public void goback() {
        goback = true;
    }

}
