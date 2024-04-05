package com.keurig.xpbooster.util;

import com.keurig.xpbooster.XPBoostPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigYml extends YamlConfiguration {

    private File file;
    private final String name;
    private final XPBoostPlugin plugin;

    public ConfigYml(String name, XPBoostPlugin plugin) {
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

    @Override
    public String getString(String path) {
        return Chat.color(super.getString(path));
    }

    @Override
    public boolean getBoolean(String path) {
        return super.getBoolean(path, false);
    }

    @Override
    public int getInt(String path) {
        return super.getInt(path, 0);
    }

    @Override
    public double getDouble(String path) {
        return super.getDouble(path, 0.0);
    }

    @Override
    public List<String> getStringList(String path) {
        return super.getStringList(path).stream().map(Chat::color).collect(Collectors.toList());
    }

    public String[] getStringArray(String path) {
        return super.getStringList(path).stream().map(Chat::color).toArray(String[]::new);
    }
}
