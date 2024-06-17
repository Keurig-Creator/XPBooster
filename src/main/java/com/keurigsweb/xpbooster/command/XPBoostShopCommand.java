package com.keurigsweb.xpbooster.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.base.data.ui.ShopMenu;
import com.keurigsweb.xpbooster.base.menu.data.MenuManager;
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
