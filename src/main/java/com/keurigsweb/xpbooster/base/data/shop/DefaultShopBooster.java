package com.keurigsweb.xpbooster.base.data.shop;

import com.keurigsweb.xpbooster.base.data.booster.ShopBooster;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

@Getter
public class DefaultShopBooster extends ShopBooster {

    public DefaultShopBooster(Material material, String title, boolean glow, String[] lore) {
        super(null);
        this.material = material;
        this.lore = List.of(lore);
        this.title = title;
        glowing = glow;
        slot = -1;
        setSlot(-1);
    }
}
