package com.keurigsweb.xpbooster.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Tasks {

    public static void runTaskLater(Plugin plugin, Runnable task, long delay) {
        Bukkit.getScheduler().runTaskLater(plugin, task, delay);
    }

}
