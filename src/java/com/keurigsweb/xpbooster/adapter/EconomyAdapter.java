package com.keurigsweb.xpbooster.adapter;

import org.bukkit.entity.Player;

public interface EconomyAdapter {
    boolean deposit(Player player, double amount);

    boolean withdraw(Player player, double amount);

    double getBalance(Player player);
}
