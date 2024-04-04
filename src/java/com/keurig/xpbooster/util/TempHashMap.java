package com.keurig.xpbooster.util;

import com.keurig.xpbooster.XPBoostPlugin;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class TempHashMap<K, V> extends HashMap<K, V> {
    private final long ttl; // Time-to-live for entries in milliseconds

    public TempHashMap(long ttl) {
        this.ttl = ttl;
    }

    // Override put method to schedule removal after ttl
    @Override
    public V put(K key, V value) {
        V oldValue = super.put(key, value);
        scheduleRemoval(key);
        return oldValue;
    }

    // Schedule removal of key after ttl
    private void scheduleRemoval(final K key) {
        Bukkit.getScheduler().runTaskLater(XPBoostPlugin.getInstance(), () -> {
            remove(key);
        }, ttl);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                remove(key);
            }
        }, ttl);
    }

}