package com.keurig.xpbooster.base.shop;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
public class DefaultShopBooster extends ShopBooster {

    protected final Material type;
    protected @Setter
    @Getter String[] lore;


    public DefaultShopBooster(Material type, String title, boolean glow, String[] lore) {
        super(null);
        this.type = type;
        this.lore = lore;
        setSlot(-1);
    }
}
