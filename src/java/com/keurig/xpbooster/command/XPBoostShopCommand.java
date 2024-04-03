package com.keurig.xpbooster.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import com.keurig.xpbooster.XPBoostPlugin;
import org.bukkit.entity.Player;

@CommandAlias("%shopcommand")
public class XPBoostShopCommand extends BaseCommand {

    @Dependency
    private XPBoostPlugin plugin;

    @Default
    public void onCommand(Player player) {

        player.openInventory(plugin.getShopManager().getShop().getMenu());
        
    }

}
