package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.base.Booster;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public class ShopBooster {

    protected @Setter int slot;
    protected @Setter ItemStack is;
    protected final Booster booster;

    public int getSlot() {

        return slot;
    }

    /**
     * Get the shop item in the config either use whats in shops.yml or the voucher itself and add the lore
     *
     * @return
     */
    public ItemStack getShopItem() {
        return is;
    }

    public int getEmptySlot(Inventory inventory) {
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null || items[i].getType().equals(Material.AIR)) {
                return i;
            }
        }

        return -1;
    }
}
