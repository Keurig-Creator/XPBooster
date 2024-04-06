package com.keurig.xpbooster.base;


import com.keurig.xpbooster.util.replacement.Replacable;
import com.keurig.xpbooster.util.replacement.Replacement;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class Booster implements Replacable {

    protected String id;
    protected String name;
    private int multiplier;
    private String time;
    private int price;
    private Voucher voucher;

    @Override
    public String getReplace(String input) {
        return getReplacement(input).getReplacement();
    }

    @Override
    public Replacement getReplacement(String input) {
        Replacement replace = new Replacement();
        replace.setInput(input);
        replace.addReplacement(Replacement.PRICE_REGEX, String.valueOf(price));
        return replace;
    }
}
