package com.keurigsweb.xpbooster.base.menu.item;

import com.keurigsweb.xpbooster.base.menu.data.Menu;
import com.keurigsweb.xpbooster.base.menu.data.PlayerMenu;
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

    private ItemStack currentItem;
    private final InventoryView view;
    private final Menu currentMenu;
    private final PlayerMenu playerMenu;
    private final Player player;
    private final InventoryClickEvent e;
    private final ClickType clickType;
    private final int slot;
    private boolean update;
    private boolean goback;

    public ItemClickEvent(ItemStack currentItem, InventoryView view, PlayerMenu playerMenu, Player player, InventoryClickEvent e, ClickType clickType, int slot) {
        this.currentItem = currentItem;
        this.view = view;
        this.playerMenu = playerMenu;
        this.player = player;
        this.e = e;
        this.clickType = clickType;
        this.slot = slot;

        currentMenu = playerMenu.getLast();
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
        currentItem = item;
    }

    public void goback() {
        goback = true;
    }

}
