package com.keurigsweb.xpbooster;

import co.aikar.commands.CommandReplacements;
import co.aikar.commands.PaperCommandManager;
import com.keurigsweb.xpbooster.adapter.VaultEconomyAdapter;
import com.keurigsweb.xpbooster.api.XPBoostAPI;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.Booster;
import com.keurigsweb.xpbooster.base.data.shop.ShopProfile;
import com.keurigsweb.xpbooster.base.handler.BoosterManager;
import com.keurigsweb.xpbooster.base.handler.InternalXPBoostHandler;
import com.keurigsweb.xpbooster.base.handler.ShopManager;
import com.keurigsweb.xpbooster.base.menu.data.MenuManager;
import com.keurigsweb.xpbooster.command.XPBoostCommand;
import com.keurigsweb.xpbooster.command.XPBoostInfoCommand;
import com.keurigsweb.xpbooster.command.XPBoostReloadCommand;
import com.keurigsweb.xpbooster.command.XPBoostShopCommand;
import com.keurigsweb.xpbooster.event.ExperienceChangeListener;
import com.keurigsweb.xpbooster.event.InventoryMoveListener;
import com.keurigsweb.xpbooster.event.PlayerClickEvent;
import com.keurigsweb.xpbooster.language.Language;
import com.keurigsweb.xpbooster.tasks.BoostEndTask;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.ConfigYml;
import com.keurigsweb.xpbooster.util.JsonConfig;
import de.tr7zw.changeme.nbtapi.NBT;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class XPBoostPlugin extends JavaPlugin implements Listener {

    private @Getter
    static XPBoostPlugin instance;

    public ConfigYml config, lang;

    private @Getter JsonConfig dataConfig;

    private @Getter InternalXPBoostHandler boostHandler;

    @Getter
    private BoosterManager boosterManager;

    @Getter
    private ShopManager shopManager;

    private PaperCommandManager manager;

    @Getter
    private VaultEconomyAdapter economyAdapter;

    private Metrics metrics;

    private @Getter CommandReplacements replacements;

    @Override
    public void onEnable() {
        int pluginId = 21541; // <-- Replace with the id of your plugin!
        metrics = new Metrics(this, pluginId);

        instance = this;
        config = new ConfigYml("settings.yml", this);
        lang = new ConfigYml("lang.yml", this);
        dataConfig = new JsonConfig("user-data.json", this);
        boostHandler = new InternalXPBoostHandler(dataConfig);
        boosterManager = new BoosterManager(this, new ConfigYml("boosters.yml", this));
        shopManager = new ShopManager(this, new ConfigYml("shops.yml", this));

        boosterManager.setupVoucherConfig();
        shopManager.setupShopConfig();
        loadLanguage();
        loadDataConfig();

        manager = new PaperCommandManager(this);
        replacements = manager.getCommandReplacements();

        manager.getCommandContexts().registerContext(ShopProfile.class, c -> shopManager.getShop());
        manager.getCommandContexts().registerContext(Booster.class, c -> boosterManager.getBooster(c.popFirstArg()));
        manager.getCommandCompletions().registerCompletion("boosters", c -> boosterManager.getAllBoosters().keySet());

        manager.registerCommand(new XPBoostCommand());
        manager.registerCommand(new XPBoostReloadCommand());

        economyAdapter = new VaultEconomyAdapter();
        if (economyAdapter.isEnabled()) {
            Chat.log("Economy hook is enabled");
            replacements.addReplacements("%shopcommand", shopManager.getShop().getCommand());
            manager.registerCommand(new XPBoostShopCommand());
        } else {
            Chat.error("Shop disabled vault and economy plugins are required to work");
        }

        manager.registerCommand(new XPBoostInfoCommand());


        Bukkit.getPluginManager().registerEvents(new ExperienceChangeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryMoveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerClickEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new MenuManager(), this);

        new BoostEndTask(this).runTaskTimerAsynchronously(this, 0, 5);
    }

    /**
     * Load the language amd load the datacConfig user
     */
    public void loadDataConfig() {
        dataConfig.load();

        // Safety remove an expired user from the data
        Iterator<Map.Entry<UUID, EXPBoost>> iterator = dataConfig.getExpBoosts().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, EXPBoost> entry = iterator.next();
            EXPBoost expBoost = entry.getValue();

            if (expBoost.isExpired()) {
                XPBoostAPI.removeBoost(expBoost.getUuid());
                iterator.remove(); // Remove the current entry from the map
            }
        }
    }

    /**
     * Load the language from enum to file
     */
    public void loadLanguage() {
        lang.load();

        boolean save = false;
        for (Language language : Language.values()) {
            if (lang.getString(language.name().toLowerCase()) == null) {
                save = true;
                lang.set(language.name().toLowerCase(), language.value);
            }
        }

        if (save)
            lang.save();
    }

    @Override
    public void onDisable() {
        instance = null;
        dataConfig.getExpBoosts().clear();
        boosterManager.clear();
        metrics.shutdown();
    }

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }
}
