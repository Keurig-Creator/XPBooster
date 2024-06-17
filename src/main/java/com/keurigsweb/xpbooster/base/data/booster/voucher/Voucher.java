package com.keurigsweb.xpbooster.base.data.booster.voucher;

import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.base.data.booster.Booster;
import com.keurigsweb.xpbooster.base.data.booster.Item;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.ItemBuilder;
import com.keurigsweb.xpbooster.util.NumUtil;
import com.keurigsweb.xpbooster.util.replacement.Replacable;
import com.keurigsweb.xpbooster.util.replacement.Replacement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

@Builder
@Getter
@Setter
public class Voucher extends Item implements Replacable {
    protected Booster booster;

    @Override
    public String getReplace(String input) {
        return getReplacement(input).getReplacement();
    }

    public ItemStack getItem() {
        ItemBuilder itemBuilder = ItemBuilder.item(material);
        itemBuilder.setName(getReplace(title));
        itemBuilder.setLore(lore.stream().map(Chat::color).map(this::getReplace).collect(Collectors.toList()));
        itemBuilder.setGlow(glowing);
        itemBuilder.modify(nbt -> nbt.setString("boosterId", booster.getId()));

        return itemBuilder.toItemStack();
    }

    @Override
    public Replacement getReplacement(String input) {
        Replacement replace = new Replacement();
        replace.setInput(input);
        replace.addReplacement(Replacement.VOUCHER_REGEX, XPBoostPlugin.getInstance().getBoosterManager().getAllBoosters().toString());
        replace.addReplacement(Replacement.MULTIPLIER_REGEX, NumUtil.formatMultiplier(booster.getMultiplier()));
        replace.addReplacement(Replacement.NAME_REGEX, booster.getName());
        replace.addReplacement(Replacement.TIME_REGEX, booster.getTime());
        replace.addReplacement(Replacement.PRICE_REGEX, String.valueOf(booster.getPrice()));

        return replace;
    }
}
