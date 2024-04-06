package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.base.ShopBooster;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

@Getter
public class DefaultShopBooster extends ShopBooster {

    public DefaultShopBooster(Material material, String title, boolean glow, String[] lore) {
        super(null);
        this.material = material;
        this.lore = List.of(lore);
        setSlot(-1);
    }
}
