package com.keurigsweb.xpbooster.base.data.booster;


import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.base.data.booster.voucher.Voucher;
import com.keurigsweb.xpbooster.util.NumUtil;
import com.keurigsweb.xpbooster.util.replacement.Replacable;
import com.keurigsweb.xpbooster.util.replacement.Replacement;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class Booster implements Replacable {

    protected String id;
    protected String name;
    private double multiplier;
    private String time;
    private int price;
    private Voucher voucher;
    private boolean global;

    @Override
    public String getReplace(String input) {
        return getReplacement(input).getReplacement();
    }

    @Override
    public Replacement getReplacement(String input) {
        Replacement replace = new Replacement();
        replace.setInput(input);
        replace.addReplacement(Replacement.VOUCHER_REGEX, XPBoostPlugin.getInstance().getBoosterManager().getAllBoosters().toString());
        replace.addReplacement(Replacement.MULTIPLIER_REGEX, NumUtil.formatMultiplier(getMultiplier()));
        replace.addReplacement(Replacement.NAME_REGEX, getName());
        replace.addReplacement(Replacement.TIME_REGEX, getTime());
        replace.addReplacement(Replacement.PRICE_REGEX, String.valueOf(getPrice()));
        return replace;
    }
}
