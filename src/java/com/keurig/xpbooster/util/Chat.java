package com.keurig.xpbooster.util;

import com.keurig.xpbooster.XPBooster;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

public class Chat {

    public static TempHashSet<UUID> temp = new TempHashSet<>(1000);

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String color(String... message) {
        return ChatColor.translateAlternateColorCodes('&', Arrays.toString(message));
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
        XPBooster.getInstance().getLogger().log(Level.INFO, color(message));
    }

    public static void log(int number) {
        XPBooster.getInstance().getLogger().log(Level.INFO, String.valueOf(number));
    }

    public static void log(double number) {
        XPBooster.getInstance().getLogger().log(Level.INFO, String.valueOf(number));
    }

    public static void log(boolean value) {
        XPBooster.getInstance().getLogger().log(Level.INFO, String.valueOf(value));
    }

    public static void log(Object object) {
        XPBooster.getInstance().getLogger().log(Level.INFO, object.toString());
    }
}
