package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.Booster;
import com.keurig.xpbooster.base.Voucher;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.ConfigYml;
import com.keurig.xpbooster.util.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class ShopManager {

    // Added support down the road for more shops when added faction support
    private final HashMap<String, ShopProfile> shopProfiles = new HashMap<>();
    private final XPBoostPlugin plugin;
    private final ConfigYml config;

    private DefaultShopBooster defaultShopItem;

    public void setupShopConfig() {
        shopProfiles.clear();

        ConfigurationSection defaultItems = config.getConfigurationSection("default-items");

        if (defaultItems == null) {
            throw new RuntimeException("default-items path not found");
        }

        defaultShopItem = new DefaultShopBooster(Material.valueOf(
                defaultItems.getString("material")),
                defaultItems.getString("title"),
                defaultItems.getBoolean("glow"),
                defaultItems.getStringList("lore")
                        .toArray(new String[0]));

        ConfigurationSection shopSection = config.getConfigurationSection("regular_boost_shop");

        if (shopSection == null) {
            Chat.log("Regular Boost Shop section not found in the configuration.");
            return;
        }

        String command = shopSection.getString("command");
        String guiName = Chat.color(shopSection.getString("gui-name"));
        int size = shopSection.getInt("size");

        // Fill section
        ConfigurationSection fillSection = shopSection.getConfigurationSection("fill");
        if (fillSection == null) {
            Chat.log("Fill section not found in the Regular Boost Shop configuration.");
            return;
        }

        MenuFill menuFill = MenuFill.create(fillSection.getString("type"), fillSection.getString("material"));

        // Items section
        ConfigurationSection itemsSection = shopSection.getConfigurationSection("boosters.items");
        if (itemsSection == null) {
            Chat.log("Items section not found in the Regular Boost Shop configuration.");
            return;
        }

        ShopProfile shopProfile = new ShopProfile(guiName, command);
        shopProfile.setFill(menuFill);
        shopProfile.setSize(size);
        shopProfile.setGuiName(guiName);

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            if (itemSection == null) {
                Chat.log("Item section not found for key: " + key);
                continue;
            }

            Booster booster = plugin.getBoosterManager().getBooster(key.toLowerCase());

            if (booster == null) {
                throw new RuntimeException("Booster " + key + " was not found");
            }

            ItemBuilder itemBuilder = ItemBuilder.fromConfig(itemSection);


//            Replacement replace = new Replacement();
//            replace.addReplacement(Replacement.VOUCHER_REGEX, plugin.getBoosterManager().getVouchers().toString());
//            replace.addReplacement(Replacement.MULTIPLIER_REGEX, String.valueOf(voucher.getMultiplier()));
//            replace.addReplacement(Replacement.NAME_REGEX, voucher.getName());
//            replace.addReplacement(Replacement.TIME_REGEX, voucher.getTime());
//            replace.addReplacement(Replacement.PRICE_REGEX, String.valueOf(voucher.getPrice()));


            ShopBooster shopBooster = new ShopBooster(XPBoostPlugin.getInstance().getBoosterManager().getBooster(key.toLowerCase()));


            if (itemBuilder != null) {
                if (itemSection.getString("title") != null)
                    itemBuilder.setName(itemSection.getString("title"));

                itemSection.getStringList("lore");
                itemBuilder.setLore(itemSection.getStringList("lore").stream().map(Chat::color).collect(Collectors.toList()));
                itemBuilder.setGlow(itemSection.getBoolean("glow"));
                itemBuilder.setLocalizedName(booster.getName());


                shopBooster.setIs(itemBuilder.toItemStack());

            } else {
                shopBooster.setIs(defaultShopItem.getIs());
            }

            shopBooster.setSlot(itemSection.getInt("slot", -1));
            shopProfile.getBoosters().add(shopBooster);
        }

        shopProfiles.put("regular_boost_shop", shopProfile);
    }

    public ShopProfile getShop() {
        return shopProfiles.get("regular_boost_shop");
    }

    public List<String> getShopLore(Voucher voucher) {
        return getConfig().getStringList("voucher-shop.lore").stream().map(Voucher::getReplacement).collect(Collectors.toList());
    }

    public boolean addLoreToTop() {
        return getConfig().getBoolean("voucher-shop.to-top");
    }
}
