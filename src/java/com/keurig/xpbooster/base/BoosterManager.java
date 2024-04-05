package com.keurig.xpbooster.base;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.ConfigYml;
import com.keurig.xpbooster.util.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class BoosterManager {

    private final HashMap<String, Booster> allBoosters = new HashMap<>();
    private final XPBoostPlugin plugin;
    private final ConfigYml config;

    private boolean vouchersEnabled;

    public void setupVoucherConfig() {
        clear();

        ConfigurationSection boosters = config.getConfigurationSection("boosters");

        if (boosters == null) {
            Chat.log("Booster Section was not found in boosters.yml");
            return;
        }

        vouchersEnabled = boosters.getBoolean("vouchers-enabled");

        for (String key : boosters.getKeys(false)) {
            ConfigurationSection sec = boosters.getConfigurationSection(key);

            if (sec == null) {
                continue;
            }

            Voucher voucher = null;

            if (vouchersEnabled) {
                Chat.log("voucher enabled");
                ConfigurationSection voucherSec = sec.getConfigurationSection("voucher");

                if (voucherSec == null) {
                    throw new RuntimeException("Vouchers enabled in config but unable to find " + key + "'s voucher section in boosters.yml");
                }

                voucher = Voucher.builder()
                        .title(voucherSec.getString("title"))
                        .glowing(voucherSec.getBoolean("glow"))
                        .build();

                List<String> lore = voucherSec.getStringList("lore").stream().map(Voucher::getReplacement).map(Chat::color).collect(Collectors.toList());

                ItemBuilder is = ItemBuilder.fromString(voucherSec.getString("material"));
                is.setName(voucher.title);
                is.setGlow(voucher.isGlowing());
                is.setLore(lore);
                is.setLocalizedName(key.toLowerCase());

                voucher.setItem(is.toItemStack());
            }

            Booster booster = Booster.builder()
                    .id(key.toLowerCase())
                    .name(sec.getString("title"))
                    .multiplier(sec.getInt("multiplier"))
                    .time(sec.getString("time"))
                    .voucher(voucher).build();

            allBoosters.put(key.toLowerCase(), booster);
        }

    }

    public Booster getBooster(String name) {
        return allBoosters.getOrDefault(name.toLowerCase(), null);
    }


    public void claimVoucher(Player player, Voucher voucher) {

    }

    public void clear() {
        allBoosters.clear();
    }


}
