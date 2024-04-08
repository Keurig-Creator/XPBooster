package com.keurig.xpbooster.base.data.booster;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.ItemBuilder;
import com.keurig.xpbooster.util.NumUtil;
import com.keurig.xpbooster.util.replacement.Replacable;
import com.keurig.xpbooster.util.replacement.Replacement;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

@Getter
public class ShopBooster extends Item implements Replacable {

    @Getter
    protected @Setter int slot;
    protected final Booster booster;

    public ShopBooster(Booster booster) {
        this.booster = booster;
    }

    /**
     * Get the shop item in the config either use whats in shops.yml or the voucher itself and add the lore
     *
     * @return
     */
    public ItemStack getItem() {
        ItemBuilder itemBuilder = ItemBuilder.item(material);
        itemBuilder.setName(getReplace(title));
        itemBuilder.setLore(lore.stream().map(Chat::color).map(this::getReplace).collect(Collectors.toList()));
        itemBuilder.setGlow(glowing);
        itemBuilder.setLocalizedName(booster.getId());

        return itemBuilder.toItemStack();
    }

    @Override
    public String getReplace(String input) {
        return getReplacement(input).getReplacement();
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
