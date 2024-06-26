package com.keurigsweb.xpbooster.util;

import com.keurigsweb.xpbooster.XPBoostPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Chat {

    public static TempHashSet<UUID> temp = new TempHashSet<>(1000);

    public static String color(String message) {
        try {
            return ChatColor.translateAlternateColorCodes('&', message);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    public static List<String> color(String... message) {
        return Arrays.stream(message)
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
    }


    public static void message(Player player, String... messages) {
        Arrays.stream(messages).forEach(s -> player.sendMessage(color(s)));
    }

    public static void message(Player player, double seconds, String... messages) {
        if (temp.contains(player.getUniqueId())) {
            return;
        }

        temp.add(player.getUniqueId());
        Arrays.stream(messages).forEach(s -> player.sendMessage(color(s)));
    }

    public static void message(CommandSender sender, String... messages) {
        Arrays.stream(messages).forEach(s -> sender.sendMessage(color(s)));
    }

    public static void log(String message) {
        XPBoostPlugin.getInstance().getLogger().log(Level.INFO, color(message));
    }

    public static void error(String message) {
        XPBoostPlugin.getInstance().getLogger().log(Level.SEVERE, color(message));
    }

    public static void log(int number) {
        XPBoostPlugin.getInstance().getLogger().log(Level.INFO, String.valueOf(number));
    }

    public static void log(double number) {
        XPBoostPlugin.getInstance().getLogger().log(Level.INFO, String.valueOf(number));
    }

    public static void log(boolean value) {
        XPBoostPlugin.getInstance().getLogger().log(Level.INFO, String.valueOf(value));
    }

    public static void log(Object object) {
        XPBoostPlugin.getInstance().getLogger().log(Level.INFO, object.toString());
    }
}
