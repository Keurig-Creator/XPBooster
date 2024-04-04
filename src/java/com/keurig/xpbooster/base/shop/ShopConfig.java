package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.base.Voucher;
import com.keurig.xpbooster.base.menu.ShopMenu;
import com.keurig.xpbooster.base.menu.data.Menu;
import com.keurig.xpbooster.base.menu.data.MenuManager;
import com.keurig.xpbooster.base.menu.item.ActionItem;
import com.keurig.xpbooster.base.menu.item.ConfirmMenu;
import lombok.*;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class ShopConfig {

    private final String name;
    private final String command;
    private String guiName;
    private int size;
    private MenuFill fill;
    private Set<ShopItem> items = new HashSet<>();

    public Inventory setShopMenu(ShopMenu menu) {
        fill.fillInventory(menu.getInventory());
        for (ShopItem shopItem : items) {

            ActionItem action = new ActionItem(shopItem.getShopItem()).addAction(e -> {
                menu.getPlayerMenu().setData("voucher", shopItem.getVoucher());
                menu.getPlayerMenu().setData("shopItem", shopItem.getShopItem());

                MenuManager.openMenu(ConfirmMenu.class, e.getPlayer());


            });

            menu.addAction(action, nextSlot(menu, shopItem));
        }

        return menu.getInventory();
    }

    public boolean isInstantClaim(Voucher voucher) {
        for (ShopItem item : items) {
            if (item.getVoucher().equals(voucher)) {
                return item.isInstantClaim();
            }
        }
        return true;
    }

    public int nextSlot(Menu menu, ShopItem shopItem) {

        return shopItem.getSlot();
    }
}
