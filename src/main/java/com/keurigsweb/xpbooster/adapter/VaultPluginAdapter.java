package com.keurigsweb.xpbooster.adapter;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPluginAdapter implements VaultAdapter {
    private Economy economy;
    private @Getter boolean enabled;

    public VaultPluginAdapter() {
        // Attempt to hook into Vault's economy implementation
        try {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

            if (rsp != null) {
                enabled = true;
                economy = rsp.getProvider();
            }
        } catch (NoClassDefFoundError e) {
            enabled = false;
        }
    }

    @Override
    public boolean deposit(Player player, double amount) {
        if (economy != null) {
            return economy.depositPlayer(player, amount).transactionSuccess();
        }
        return false;
    }

    @Override
    public boolean withdraw(Player player, double amount) {
        if (economy != null && economy.has(player, amount)) {
            return economy.withdrawPlayer(player, amount).transactionSuccess();
        }
        return false;
    }

    @Override
    public double getBalance(Player player) {
        if (economy != null) {
            return economy.getBalance(player);
        }
        return 0.0;
    }
}