package com.keurig.xpbooster.base;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.ConfigYml;
import com.keurig.xpbooster.util.ItemBuilder;
import com.keurig.xpbooster.util.Replacement;
import lombok.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class VoucherManager {

    private final Set<Voucher> vouchers = new HashSet<>();
    private XPBoostPlugin plugin;
    private ConfigYml config;

    public void setupVoucherConfig() {
        clear();

        ConfigurationSection boosters = config.getConfigurationSection("boosters");

        if (boosters == null) {
            return;
        }

        for (String key : boosters.getKeys(false)) {
            ConfigurationSection sec = boosters.getConfigurationSection(key);

            if (sec == null) {
                return;
            }

            Voucher voucher = new Voucher(key);

            voucher.setMultiplier(sec.getInt("multiplier"));
            voucher.setTime(sec.getString("time"));
            voucher.setPrice(sec.getInt("price"));

            Replacement replace = new Replacement();
            replace.addReplacement(Replacement.VOUCHER_REGEX, plugin.getVoucherManager().getVouchers().toString());
            replace.addReplacement(Replacement.MULTIPLIER_REGEX, String.valueOf(voucher.getMultiplier()));
            replace.addReplacement(Replacement.NAME_REGEX, voucher.getName());
            replace.addReplacement(Replacement.PRICE_REGEX, String.valueOf(voucher.getPrice()));
            replace.addReplacement(Replacement.TIME_REGEX, voucher.getTime());

            ItemBuilder is = ItemBuilder.fromString(sec.getString("material"));
            is.setName(replace.getReplacement(sec.getString("title")));
            is.setGlow(sec.getBoolean("glow"));
            is.setLore(sec.getStringList("lore").stream().map(replace::getReplacement).collect(Collectors.toList()).stream().map(Chat::color).collect(Collectors.toList()));
            is.setLocalizedName(voucher.getName());

            voucher.setItem(is.toItemStack());

            vouchers.add(voucher);
        }

    }

    /**
     * Get voucher by the name in boosters.yml
     *
     * @param name is the name in the config
     * @return voucher
     */
    public Voucher getVoucher(@NonNull String name) {

        for (Voucher voucher : vouchers) {
            if (Objects.equals(voucher.getName().toLowerCase(), name.toLowerCase())) {
                return voucher;
            }
        }

        return null;
    }

    public void clear() {
        vouchers.clear();
    }
}
