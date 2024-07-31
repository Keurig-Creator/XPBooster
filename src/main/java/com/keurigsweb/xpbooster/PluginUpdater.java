package com.keurigsweb.xpbooster;

import com.keurigsweb.xpbooster.util.Chat;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PluginUpdater {

    private JavaPlugin plugin;
    private int resourceId; // Spigot resource ID
    @Getter
    private static boolean update;
    public static String CURRENT_VERSION;
    public static String LATEST_VERSION;

    public PluginUpdater(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void checkForUpdates() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Chat.log("test");
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openConnection();
                    connection.setRequestMethod("GET");
                    String latestVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

                    // Compare versions
                    String currentVersion = plugin.getDescription().getVersion();

                    if (!latestVersion.equals(currentVersion)) {
                        Chat.log("A new version is available: " + latestVersion);
                        update = true;
                    }
                    LATEST_VERSION = latestVersion;
                    CURRENT_VERSION = currentVersion;
                } catch (IOException ignored) {
                }
            }
        }.runTaskAsynchronously(plugin);
    }

}