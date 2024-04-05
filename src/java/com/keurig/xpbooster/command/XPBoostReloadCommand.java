package com.keurig.xpbooster.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.language.Language;
import com.keurig.xpbooster.util.Chat;
import org.bukkit.command.CommandSender;

@CommandAlias("xpbooster|xpboost|expboost|exp|ex")
@CommandPermission("xpbooster.admin")
public class XPBoostReloadCommand extends BaseCommand {

    @Dependency
    private XPBoostPlugin plugin;

    @Subcommand("reload")
    public void onReload(CommandSender sender, String[] args) {
        plugin.loadLanguage();
        plugin.config.load();
        plugin.loadDataConfig();
        plugin.getBoosterManager().getConfig().load();
        plugin.getBoosterManager().setupVoucherConfig();
        plugin.getShopManager().getConfig().load();
        plugin.getShopManager().setupShopConfig();
        Chat.message(sender, Language.RELOAD_COMMAND.toString());
    }

}
