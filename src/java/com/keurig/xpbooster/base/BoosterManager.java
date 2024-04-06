package com.keurig.xpbooster.base;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.ConfigYml;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

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
                ConfigurationSection voucherSec = sec.getConfigurationSection("voucher");

                if (voucherSec == null) {
                    throw new RuntimeException("Vouchers enabled in config but unable to find " + key + "'s voucher section in boosters.yml");
                }

                voucher = Voucher.builder()
                        .build();

                List<String> lore = voucherSec.getStringList("lore");
                voucher.setMaterial(Material.valueOf(voucherSec.getString("material")));
                voucher.setLore(lore);
                voucher.setGlowing(voucherSec.getBoolean("glow"));
                voucher.setTitle(Chat.color(voucherSec.getString("title")));
            }


            Booster.BoosterBuilder boosterBuilder = Booster.builder()
                    .id(key.toLowerCase())
                    .name(Chat.color(sec.getString("name")))
                    .multiplier(sec.getInt("multiplier"))
                    .time(sec.getString("time"))
                    .price(sec.getInt("price"));


            Booster booster = boosterBuilder.build();
            voucher.setBooster(booster);
            booster.setVoucher(voucher);

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
