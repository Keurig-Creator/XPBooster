package com.keurigsweb.xpbooster.base.handler;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.base.data.booster.Booster;
import com.keurigsweb.xpbooster.base.data.booster.BoosterSound;
import com.keurigsweb.xpbooster.base.data.booster.voucher.Voucher;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.ConfigYml;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Sound;
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
    private BoosterSound sound;

    private boolean vouchersEnabled;

    public void setupVoucherConfig() {
        clear();

        ConfigurationSection boosters = config.getConfigurationSection("boosters");

        if (boosters == null) {
            Chat.log("Booster Section was not found in boosters.yml");
            return;
        }

        vouchersEnabled = boosters.getBoolean("vouchers-enabled");


        String[] soundS = boosters.getString("special-claim-sound").split(":");

        // Initialize variables outside the try-catch block with default values
        Sound sound;

        try {
            sound = Sound.BLOCK_CONDUIT_DEACTIVATE;
        } catch (NoSuchFieldError e) {
            sound = XSound.BLOCK_NOTE_BLOCK_PLING.parseSound();
        }

        float volume = 1.0f;
        float pitch = 1.0f;

// Check if the array has at least 3 elements
        if (soundS.length >= 3) {
            try {
                volume = Float.parseFloat(soundS[1]);
                pitch = Float.parseFloat(soundS[2]);
            } catch (IllegalArgumentException e) {
                Chat.log("The format of sound is SOUND:VOLUME:PITCH in special-claim-sound boosters.yml");
            }
        }

        this.sound = new BoosterSound(sound, volume, pitch);

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
                voucher.setMaterial(XMaterial.matchXMaterial(voucherSec.getString("material")).get().parseMaterial());
                voucher.setLore(lore);
                voucher.setGlowing(voucherSec.getBoolean("glow"));
                voucher.setTitle(Chat.color(voucherSec.getString("title")));
            }


            Booster.BoosterBuilder boosterBuilder = Booster.builder()
                    .id(key.toLowerCase())
                    .name(Chat.color(sec.getString("name")))
                    .multiplier(sec.getDouble("multiplier"))
                    .time(sec.getString("time"))
                    .price(sec.getInt("price"));


            Booster booster = boosterBuilder.build();

            if (voucher != null) {
                voucher.setBooster(booster);
            }

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
