package com.keurigsweb.xpbooster;

import co.aikar.commands.CommandReplacements;
import co.aikar.commands.PaperCommandManager;
import com.keurigsweb.xpbooster.adapter.VaultPluginAdapter;
import com.keurigsweb.xpbooster.api.XPBoostAPI;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.Booster;
import com.keurigsweb.xpbooster.base.data.booster.global.HolidayBoost;
import com.keurigsweb.xpbooster.base.data.shop.ShopProfile;
import com.keurigsweb.xpbooster.base.handler.BoosterManager;
import com.keurigsweb.xpbooster.base.handler.InternalXPBoostHandler;
import com.keurigsweb.xpbooster.base.handler.ShopManager;
import com.keurigsweb.xpbooster.base.menu.data.MenuManager;
import com.keurigsweb.xpbooster.command.XPBoostCommand;
import com.keurigsweb.xpbooster.command.XPBoostInfoCommand;
import com.keurigsweb.xpbooster.command.XPBoostReloadCommand;
import com.keurigsweb.xpbooster.command.XPBoostShopCommand;
import com.keurigsweb.xpbooster.listener.PlayerExpChangeListener;
import com.keurigsweb.xpbooster.listener.PlayerInteractListener;
import com.keurigsweb.xpbooster.listener.PlayerJoinListener;
import com.keurigsweb.xpbooster.language.Language;
import com.keurigsweb.xpbooster.tasks.BoostEndTask;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.ConfigYml;
import com.keurigsweb.xpbooster.util.JsonConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
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
    private VaultPluginAdapter economyAdapter;

    private Metrics metrics;

    private @Getter CommandReplacements replacements;

    private @Getter HolidayBoost holidayBoost;

    public static HashMap<UUID, Double> permisionMultiplier = new HashMap<>();

    @Override
    public void onEnable() {
        int pluginId = 21541;
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
        holidayBoost = new HolidayBoost();

        manager = new PaperCommandManager(this);
        replacements = manager.getCommandReplacements();

        manager.getCommandContexts().registerContext(ShopProfile.class, c -> shopManager.getShop());
        manager.getCommandContexts().registerContext(Booster.class, c -> boosterManager.getBooster(c.popFirstArg()));
        manager.getCommandCompletions().registerCompletion("boosters", c -> boosterManager.getAllBoosters().keySet());

        manager.registerCommand(new XPBoostCommand());
        manager.registerCommand(new XPBoostReloadCommand());

        economyAdapter = new VaultPluginAdapter();
        if (economyAdapter.isEnabled()) {
            Chat.log("Economy hook is enabled");
            replacements.addReplacements("%shopcommand", shopManager.getShop().getCommand());
            manager.registerCommand(new XPBoostShopCommand());
        } else {
            Chat.error("Shop disabled vault and economy plugins are required to work");
        }

        manager.registerCommand(new XPBoostInfoCommand());


        Bukkit.getPluginManager().registerEvents(new PlayerExpChangeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MenuManager(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        new BoostEndTask(this).runTaskTimerAsynchronously(this, 0, 5);
    }

    /**
     * Load the language amd load the datacConfig user
     */
    public void loadDataConfig() {
        dataConfig.load();

        // Safety remove an expired user from the data
        for (Map.Entry<String, EXPBoost> entry : dataConfig.getExpBoosts().entrySet()) {
            EXPBoost expBoost = entry.getValue();

            if (expBoost.isExpired()) {
                XPBoostAPI.removeBoost(expBoost.getUuid());
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
