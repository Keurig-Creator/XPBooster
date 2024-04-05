package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.base.menu.data.Menu;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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
    private Set<ShopBooster> boosters = new HashSet<>();
    private boolean instantClaim;


    public boolean isInstantClaim() {
        return instantClaim;
    }

    public int nextSlot(Menu menu, ShopBooster shopBooster) {

        return shopBooster.getSlot();
    }
}
