package com.keurigsweb.xpbooster.base.data.shop;

import com.keurigsweb.xpbooster.base.data.booster.ShopBooster;
import com.keurigsweb.xpbooster.base.menu.MenuFill;
import com.keurigsweb.xpbooster.base.menu.data.Menu;
import com.keurigsweb.xpbooster.util.ItemBuilder;
import lombok.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class ShopProfile {

    private final String id;
    private final String command;
    private String guiName;
    private int size;
    private MenuFill fill;
    private List<ShopBooster> boosters = new ArrayList<>();
    @Getter
    private boolean instantClaim;


    public int nextSlot(Menu menu, ShopBooster shopBooster) {

        return shopBooster.getSlot();
    }

    public ItemStack fillItem() {
        return ItemBuilder.item(fill.getMaterial()).setName("&8 ").toItemStack();
    }
}
