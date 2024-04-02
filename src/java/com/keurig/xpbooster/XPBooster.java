package com.keurig.xpbooster;

import co.aikar.commands.PaperCommandManager;
import com.keurig.xpbooster.base.EXPBoost;
import com.keurig.xpbooster.base.XPBoostHandler;
import com.keurig.xpbooster.command.XPBoostCommand;
import com.keurig.xpbooster.command.XPBoostReloadCommand;
import com.keurig.xpbooster.language.Language;
import com.keurig.xpbooster.listener.ExperienceChangeListener;
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

public final class XPBooster extends JavaPlugin implements Listener {

    private @Getter
    static XPBooster instance;

    public ConfigYml config, lang;

    private @Getter JsonConfig dataConfig;

    private @Getter XPBoostHandler boostHandler;

    @Override
    public void onEnable() {
        instance = this;
        config = new ConfigYml("config.yml", this);
        lang = new ConfigYml("lang.yml", this);
        dataConfig = new JsonConfig("user-data.json", this);
        boostHandler = new XPBoostHandler(dataConfig);

        loadLanguage();
        loadDataConfig();

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new XPBoostCommand());
        manager.registerCommand(new XPBoostReloadCommand());

        Bukkit.getPluginManager().registerEvents(new ExperienceChangeListener(this), this);

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
                boostHandler.removeBoost(expBoost.getUuid());
                iterator.remove(); // Remove the current entry from the map
            }
        }
    }

    /**
     * Load the language from enum to file
     */
    public void loadLanguage() {
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
    }
}
