package com.keurig.xpbooster.base.data.booster.voucher;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.data.booster.Booster;
import com.keurig.xpbooster.base.data.booster.Item;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.ItemBuilder;
import com.keurig.xpbooster.util.NumUtil;
import com.keurig.xpbooster.util.replacement.Replacable;
import com.keurig.xpbooster.util.replacement.Replacement;
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
        itemBuilder.setLocalizedName(booster.getId());

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
