package com.keurig.xpbooster.base;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.util.Replacement;
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
    private String title;
    private ItemStack item;
    private boolean glowing;
    private List<String> lore = new ArrayList<>();
    private int multiplier;
    private String time;
    private int price;

    public String getReplacement(String input) {
        Replacement replace = new Replacement();
        replace.addReplacement(Replacement.VOUCHER_REGEX, XPBoostPlugin.getInstance().getVoucherManager().getVouchers().toString());
        replace.addReplacement(Replacement.MULTIPLIER_REGEX, String.valueOf(getMultiplier()));
        replace.addReplacement(Replacement.NAME_REGEX, getName());
        replace.addReplacement(Replacement.PRICE_REGEX, String.valueOf(getPrice()));
        replace.addReplacement(Replacement.TIME_REGEX, getTime());

        return replace.getReplacement(input);
    }

}
