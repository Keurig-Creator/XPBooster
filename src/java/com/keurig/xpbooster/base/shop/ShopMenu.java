package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.Voucher;
import com.keurig.xpbooster.util.ItemBuilder;
import com.keurig.xpbooster.util.Replacement;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class ShopMenu {

    private final String name;
    private final String command;
    private String guiName;
    private int size;
    private MenuFill fill;
    private Set<ShopItem> items = new HashSet<>();

    public Inventory getMenu() {
        Inventory inventory = Bukkit.createInventory(null, size, guiName);
        fill.fillInventory(inventory);
        for (ShopItem shopItem : items) {
            ItemStack item;

            Replacement replace = new Replacement();
            replace.addReplacement(Replacement.VOUCHER_REGEX, shopItem.getVoucher().toString());
            replace.addReplacement(Replacement.MULTIPLIER_REGEX, String.valueOf(shopItem.getVoucher().getMultiplier()));
            replace.addReplacement(Replacement.NAME_REGEX, shopItem.getVoucher().getName());
            replace.addReplacement(Replacement.PRICE_REGEX, String.valueOf(shopItem.getVoucher().getPrice()));


            if (shopItem.getItem() == null) {
                ItemBuilder iB = ItemBuilder.item(shopItem.getVoucher().getItem());

                List<String> shopLore = XPBoostPlugin.getInstance().getShopManager().getConfig().getStringList("voucher-shop-lore");
                Collections.reverse(shopLore);
                for (String s : shopLore) {
                    iB.addLoreLineToTop(replace.getReplacement(s));
                }

                item = iB.toItemStack();
            } else {
                item = shopItem.getItem();
            }

            if (shopItem.getSlot() == -1) {
                inventory.addItem(item);
            } else {
                inventory.setItem(shopItem.getSlot(), item);
            }
        }

        return inventory;
    }

    public boolean isInstantClaim(Voucher voucher) {
        for (ShopItem item : items) {
            if (item.getVoucher().equals(voucher)) {
                return item.isInstantClaim();
            }
        }
        return true;
    }
}
