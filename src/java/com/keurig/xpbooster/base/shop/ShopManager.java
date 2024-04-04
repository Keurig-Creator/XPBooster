package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.Voucher;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.ConfigYml;
import com.keurig.xpbooster.util.ItemBuilder;
import com.keurig.xpbooster.util.Replacement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ShopManager {

    // Added support down the road for more shops when added faction support
    private final HashMap<String, ShopConfig> shops = new HashMap<>();
    private XPBoostPlugin plugin;
    private ConfigYml config;


    public void setupShopConfig() {
        shops.clear();

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
        ConfigurationSection itemsSection = shopSection.getConfigurationSection("items");
        if (itemsSection == null) {
            Chat.log("Items section not found in the Regular Boost Shop configuration.");
            return;
        }

        ShopConfig shopConfig = new ShopConfig(guiName, command);
        shopConfig.setFill(menuFill);
        shopConfig.setSize(size);
        shopConfig.setGuiName(guiName);

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            if (itemSection == null) {
                Chat.log("Item section not found for key: " + key);
                continue;
            }

            int slot = itemSection.getInt("slot", -1);

            String voucherType = itemSection.getString("voucher-type");
            boolean instantClaim = itemSection.getBoolean("instant-claim");
            Voucher voucher = plugin.getVoucherManager().getVoucher(voucherType);

            ItemBuilder itemBuilder = ItemBuilder.fromConfig(itemSection);

            Replacement replace = new Replacement();
            replace.addReplacement(Replacement.VOUCHER_REGEX, plugin.getVoucherManager().getVouchers().toString());
            replace.addReplacement(Replacement.MULTIPLIER_REGEX, String.valueOf(voucher.getMultiplier()));
            replace.addReplacement(Replacement.NAME_REGEX, voucher.getName());
            replace.addReplacement(Replacement.TIME_REGEX, voucher.getTime());
            replace.addReplacement(Replacement.PRICE_REGEX, String.valueOf(voucher.getPrice()));


            ShopItem shopItem = new ShopItem(slot, voucher, instantClaim);

            if (itemBuilder != null) {
                if (itemSection.getString("title") != null)
                    itemBuilder.setName(replace.getReplacement(itemSection.getString("title")));

                itemSection.getStringList("lore");
                itemBuilder.setLore(itemSection.getStringList("lore").stream().map(replace::getReplacement).collect(Collectors.toList()).stream().map(Chat::color).collect(Collectors.toList()));

                itemBuilder.setGlow(itemSection.getBoolean("glow"));


                itemBuilder.setLocalizedName(voucher.getName());
                shopItem.setItem(itemBuilder.toItemStack());

            } else {
                shopItem.setItem(null);
            }


            shopConfig.getItems().add(shopItem);
        }

        shops.put("regular_boost_shop", shopConfig);
    }

    public ShopConfig getShop() {
        return shops.get("regular_boost_shop");
    }

    public List<String> getShopLore(Voucher voucher) {
        return getConfig().getStringList("voucher-shop.lore").stream().map(voucher::getReplacement).collect(Collectors.toList());
    }

    public boolean addLoreToTop() {
        return getConfig().getBoolean("voucher-shop.to-top");
    }
}
