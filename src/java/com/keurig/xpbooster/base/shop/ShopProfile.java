package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.base.ShopBooster;
import com.keurig.xpbooster.base.menu.data.Menu;
import lombok.*;

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
}
