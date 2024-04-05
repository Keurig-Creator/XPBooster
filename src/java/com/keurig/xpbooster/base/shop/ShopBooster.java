package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.base.Booster;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
public class ShopBooster {

    @Getter
    protected @Setter int slot;
    protected @Setter ItemStack is;
    protected final Booster booster;

    public ShopBooster(Booster booster) {
        this.booster = booster;
    }


    /**
     * Get the shop item in the config either use whats in shops.yml or the voucher itself and add the lore
     *
     * @return
     */
    public ItemStack getShopItem() {
        return is;
    }
}
