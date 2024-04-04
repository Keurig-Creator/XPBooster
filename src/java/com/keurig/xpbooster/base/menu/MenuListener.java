package com.keurig.xpbooster.base.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        e.getInventory();
        if (e.getInventory().getType() != InventoryType.CHEST || !(e.getWhoClicked() instanceof Player) || !(e.getInventory().getHolder() instanceof Menu)) {
            return;
        }

        Menu menu = (Menu) e.getInventory().getHolder();

        ItemClickEvent action = new ItemClickEvent(
                e.getCurrentItem(),
                e.getView(),
                menu,
                ((Player) e.getWhoClicked()).getPlayer(),
                e,
                e.getClick(),
                e.getSlot()
        );
        menu.handle(action);
        e.setCurrentItem(action.getItem());

        if (menu.cancelClicks()) {
            e.setResult(Event.Result.DENY);
            e.setCancelled(true);
        }
    }

}