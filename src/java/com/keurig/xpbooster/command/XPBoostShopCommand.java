package com.keurig.xpbooster.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.data.ui.ShopMenu;
import com.keurig.xpbooster.base.menu.data.MenuManager;
import org.bukkit.entity.Player;

@CommandAlias("%shopcommand")
public class XPBoostShopCommand extends BaseCommand {

    @Dependency
    private XPBoostPlugin plugin;

    @Default
    public void onCommand(Player player) {
        MenuManager.openMenu(ShopMenu.class, player);
    }

}
