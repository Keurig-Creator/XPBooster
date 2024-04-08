package com.keurig.xpbooster.base.handler;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.data.booster.Booster;
import com.keurig.xpbooster.base.data.booster.ShopBooster;
import com.keurig.xpbooster.base.data.booster.voucher.Voucher;
import com.keurig.xpbooster.base.data.shop.DefaultShopBooster;
import com.keurig.xpbooster.base.data.shop.ShopProfile;
import com.keurig.xpbooster.base.data.ui.MenuFill;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.ConfigYml;
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

        ConfigurationSection defaultItem = config.getConfigurationSection("default-item");

        if (defaultItem == null) {
            throw new RuntimeException("default-items path not found");
        }

        defaultShopItem = new DefaultShopBooster(Material.valueOf(
                defaultItem.getString("material")),
                defaultItem.getString("title"),
                defaultItem.getBoolean("glow"),
                defaultItem.getStringList("lore").toArray(new String[0]));

        ConfigurationSection shopSection = config.getConfigurationSection("regular_boost_shop");

        if (shopSection == null) {
            Chat.log("Regular Boost Shop section not found in the configuration.");
            return;
        }

        String command = shopSection.getString("command");
        String guiName = Chat.color(shopSection.getString("gui-name"));
        boolean instantClaim = shopSection.getBoolean("instant-claim");
        int size = shopSection.getInt("size");

        // Fill section
        ConfigurationSection fillSection = shopSection.getConfigurationSection("fill");
        if (fillSection == null) {
            Chat.log("Fill section not found in the Regular Boost Shop configuration.");
            return;
        }

        MenuFill menuFill = MenuFill.create(fillSection.getString("type"), fillSection.getString("material"));

        ShopProfile shopProfile = new ShopProfile(guiName, command);
        shopProfile.setFill(menuFill);
        shopProfile.setSize(size);
        shopProfile.setGuiName(guiName);
        shopProfile.setInstantClaim(instantClaim);

        if (!plugin.getBoosterManager().isVouchersEnabled())
            shopProfile.setInstantClaim(true);

        Chat.log(shopProfile.isInstantClaim());

        // Items section if not use default-item
        ConfigurationSection itemsSection = shopSection.getConfigurationSection("boosters.items");
        if (itemsSection == null) {
            for (String key : shopSection.getStringList("boosters")) {
                Booster booster = plugin.getBoosterManager().getBooster(key.toLowerCase());

                ShopBooster shopBooster = new ShopBooster(booster);

                Chat.log(defaultShopItem.getTitle());
                shopBooster.setTitle(defaultShopItem.getTitle());
                shopBooster.setLore(defaultShopItem.getLore().stream().map(Chat::color).collect(Collectors.toList()));
                shopBooster.setGlowing(defaultShopItem.isGlowing());
                shopBooster.setMaterial(defaultShopItem.getMaterial());
                shopBooster.setSlot(defaultShopItem.getSlot());
                shopProfile.getBoosters().add(shopBooster);
            }
        } else {
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

                ShopBooster shopBooster = new ShopBooster(booster);

                if (itemSection.getString("title") != null)
                    shopBooster.setTitle(itemSection.getString("title"));
                shopBooster.setLore(itemSection.getStringList("lore").stream().map(Chat::color).collect(Collectors.toList()));
                shopBooster.setGlowing(itemSection.getBoolean("glow"));
                shopBooster.setMaterial(Material.valueOf(itemSection.getString("material")));
                shopBooster.setSlot(itemSection.getInt("slot", -1));
                shopProfile.getBoosters().add(shopBooster);
            }
        }

        shopProfiles.put("regular_boost_shop", shopProfile);
    }

    public ShopProfile getShop() {
        return shopProfiles.get("regular_boost_shop");
    }

    public List<String> getShopLore(Voucher voucher) {
        return getConfig().getStringList("voucher-shop.lore").stream().map(voucher::getReplace).collect(Collectors.toList());
    }

    public boolean addLoreToTop() {
        return getConfig().getBoolean("voucher-shop.to-top");
    }
}
