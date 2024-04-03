package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.base.Voucher;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public class ShopItem {

    private final int slot;
    private @Setter ItemStack item;
    private final Voucher voucher;
    private final boolean instantClaim;
}
