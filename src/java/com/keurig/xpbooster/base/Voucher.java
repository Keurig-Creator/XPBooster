package com.keurig.xpbooster.base;

import com.keurig.xpbooster.util.Replacement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Builder
@Getter
@Setter
public class Voucher {
    protected ItemStack item;
    protected String title;
    protected boolean glowing;

    public void setTitle(String title) {
        this.title = getReplacement(title);
    }

    public static String getReplacement(String input) {
        Replacement replace = new Replacement();
//        replace.addReplacement(Replacement.VOUCHER_REGEX, XPBoostPlugin.getInstance().getBoosterManager().getVouchers().toString());
//        replace.addReplacement(Replacement.MULTIPLIER_REGEX, String.valueOf(getMultiplier()));
//        replace.addReplacement(Replacement.NAME_REGEX, getName());
//        replace.addReplacement(Replacement.PRICE_REGEX, String.valueOf(getPrice()));
//        replace.addReplacement(Replacement.TIME_REGEX, getTime());

        return replace.getReplacement(input);
    }

}
