package com.keurig.xpbooster.adapter;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomyAdapter implements EconomyAdapter {
    private Economy economy;

    public VaultEconomyAdapter() {
        // Attempt to hook into Vault's economy implementation
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
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
        if (economy != null) {
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