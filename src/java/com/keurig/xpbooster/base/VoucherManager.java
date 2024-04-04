package com.keurig.xpbooster.base;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.ConfigYml;
import com.keurig.xpbooster.util.ItemBuilder;
import lombok.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
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
            voucher.setTitle(voucher.getReplacement(sec.getString("title")));
            voucher.setGlowing(sec.getBoolean("glow"));

            List<String> lore = sec.getStringList("lore").stream().map(voucher::getReplacement).map(Chat::color).collect(Collectors.toList());

            ItemBuilder is = ItemBuilder.fromString(sec.getString("material"));
            is.setName(voucher.getTitle());
            is.setGlow(voucher.isGlowing());
            is.setLore(lore);
            is.setLocalizedName(voucher.getName());

            voucher.setLore(lore);

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

    public void claimVoucher(Player player, Voucher voucher) {

    }

    public void clear() {
        vouchers.clear();
    }


}
