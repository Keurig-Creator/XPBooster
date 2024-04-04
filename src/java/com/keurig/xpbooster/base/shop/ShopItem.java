package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.Voucher;
import com.keurig.xpbooster.util.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ShopItem {

    private final int slot;
    private @Setter ItemStack item;
    private final Voucher voucher;
    private final boolean instantClaim;

    /**
     * Get the shop item in the config either use whats in shops.yml or the voucher itself and add the lore
     *
     * @return
     */
    public ItemStack getShopItem() {

        if (item == null) {
            ItemBuilder itemBuilder = ItemBuilder.item(voucher.getItem().clone());

            itemBuilder.setName(voucher.getTitle());

            ShopManager shopManager = XPBoostPlugin.getInstance().getShopManager();

            List<String> shopLore = shopManager.getShopLore(voucher);

            if (shopManager.addLoreToTop()) {
                Collections.reverse(shopLore);
            }
            
            for (String s : shopLore) {
                if (shopManager.addLoreToTop()) {
                    itemBuilder.addLoreLineToTop(s);
                } else {
                    itemBuilder.addLoreLine(s);
                }
            }
            return itemBuilder.toItemStack();
        }
        return item;
    }
}
