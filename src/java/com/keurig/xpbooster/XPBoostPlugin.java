package com.keurig.xpbooster;

import co.aikar.commands.CommandReplacements;
import co.aikar.commands.PaperCommandManager;
import com.keurig.xpbooster.api.XPBoostAPI;
import com.keurig.xpbooster.base.data.EXPBoost;
import com.keurig.xpbooster.base.data.booster.Booster;
import com.keurig.xpbooster.base.data.shop.ShopProfile;
import com.keurig.xpbooster.base.handler.BoosterManager;
import com.keurig.xpbooster.base.handler.InternalXPBoostHandler;
import com.keurig.xpbooster.base.handler.ShopManager;
import com.keurig.xpbooster.base.menu.data.MenuManager;
import com.keurig.xpbooster.command.XPBoostCommand;
import com.keurig.xpbooster.command.XPBoostInfoCommand;
import com.keurig.xpbooster.command.XPBoostReloadCommand;
import com.keurig.xpbooster.command.XPBoostShopCommand;
import com.keurig.xpbooster.event.ExperienceChangeListener;
import com.keurig.xpbooster.event.InventoryMoveListener;
import com.keurig.xpbooster.event.PlayerClickEvent;
import com.keurig.xpbooster.language.Language;
import com.keurig.xpbooster.tasks.BoostEndTask;
import com.keurig.xpbooster.util.ConfigYml;
import com.keurig.xpbooster.util.JsonConfig;
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

    @Override
    public void onEnable() {
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
        CommandReplacements replacements = manager.getCommandReplacements();
        replacements.addReplacements("%shopcommand", shopManager.getShop().getCommand());

        manager.getCommandContexts().registerContext(ShopProfile.class, c -> shopManager.getShop());

        manager.getCommandContexts().registerContext(Booster.class, c -> boosterManager.getBooster(c.popFirstArg()));

        manager.getCommandCompletions().registerCompletion("boosters", c -> boosterManager.getAllBoosters().keySet());

        manager.registerCommand(new XPBoostCommand());
        manager.registerCommand(new XPBoostReloadCommand());
        manager.registerCommand(new XPBoostShopCommand());
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

        for (Language language : Language.values()) {
            if (lang.getString(language.name()) == null) {
                lang.set(language.name().toLowerCase(), language.value);
            }
        }

        lang.save();
    }

    @Override
    public void onDisable() {
        instance = null;
        dataConfig.getExpBoosts().clear();
        boosterManager.clear();
    }
}
