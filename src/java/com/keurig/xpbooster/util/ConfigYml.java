package com.keurig.xpbooster.util;

import com.keurig.xpbooster.XPBooster;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigYml extends YamlConfiguration {

    private File file;
    private final String name;
    private final XPBooster plugin;

    public ConfigYml(String name, XPBooster plugin) {
        file = new File(plugin.getDataFolder(), name);
        this.name = name;
        this.plugin = plugin;

        if (!file.exists()) {
            plugin.saveResource(name, false);
        }

        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        file = new File(plugin.getDataFolder(), name);

        if (!file.exists()) {
            plugin.saveResource(name, false);
        }

        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
