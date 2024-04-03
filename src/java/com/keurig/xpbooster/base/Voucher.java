package com.keurig.xpbooster.base;

import lombok.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class Voucher {

    private final String name;
    private ItemStack item;
    private boolean glowing;
    private List<String> lore = new ArrayList<>();
    private int multiplier;
    private String time;
    private int price;


}
