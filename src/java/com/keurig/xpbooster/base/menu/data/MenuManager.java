package com.keurig.xpbooster.base.menu.data;

import com.keurig.xpbooster.base.menu.item.ItemClickEvent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MenuManager implements Listener {

    private static final Map<HumanEntity, Menu> openedMenus = Collections.synchronizedMap(new HashMap<>());


    public static <T extends Menu> Inventory openMenu(Class<T> menuClass, PlayerMenu playerMenu) {
        try {
            T menu = menuClass.getDeclaredConstructor().newInstance(); // Instantiate the menu using reflection

            return openMenu(menu, playerMenu);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends Menu> Inventory openMenu(Class<T> menuClass, Player player) {
        try {
            T menu = menuClass.getDeclaredConstructor().newInstance(); // Instantiate the menu using reflection

            return openMenu(menu, player);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Inventory openMenu(Menu menu, PlayerMenu playerMenu) {
        Inventory inventory = menu.apply();
        Player player = playerMenu.getPlayer();

        if (openedMenus.containsKey(player)) {
            menu.setPlayerMenu(openedMenus.get(player).playerMenu);
            menu.getPlayerMenu().addHistory(menu);
        } else {
            playerMenu.addHistory(menu);
            menu.setPlayerMenu(playerMenu);
        }

        menu.setup(inventory);
        menu.setActionItems(inventory);

        player.closeInventory();
        player.openInventory(menu.getInventory());

        openedMenus.put(player, menu);

        return inventory;
    }

    public static Inventory openMenu(Menu menu, Player player) {
        Inventory inventory = menu.apply();

        if (openedMenus.containsKey(player)) {
            menu.setPlayerMenu(openedMenus.get(player).playerMenu);
            menu.getPlayerMenu().addHistory(menu);
        } else {
            PlayerMenu playerMenu = new PlayerMenu(player);
            playerMenu.addHistory(menu);
            menu.setPlayerMenu(playerMenu);
        }

        menu.setup(inventory);
        menu.setActionItems(inventory);

        player.closeInventory();
        player.openInventory(menu.getInventory());

        openedMenus.put(player, menu);

        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        e.getInventory();
        if (e.getInventory().getType() != InventoryType.CHEST || !(e.getWhoClicked() instanceof Player) || !(e.getInventory().getHolder() instanceof Menu menu)) {
            return;
        }

        ItemClickEvent action = new ItemClickEvent(
                e.getCurrentItem(),
                e.getView(),
                menu.playerMenu,
                ((Player) e.getWhoClicked()).getPlayer(),
                e,
                e.getClick(),
                e.getSlot()
        );
        menu.handle(action);
        e.setCurrentItem(action.getCurrentItem());

        if (menu.cancelClicks()) {
            e.setResult(Event.Result.DENY);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        e.getInventory();
        if (e.getInventory().getType() != InventoryType.CHEST || !(e.getInventory().getHolder() instanceof Menu menu)) {
            return;
        }

        if (!menu.isOpenMenu(e.getPlayer())) {
            openedMenus.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        HumanEntity player = event.getPlayer();
        openedMenus.remove(player);
    }
}
