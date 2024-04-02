package com.keurig.xpbooster.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import com.keurig.xpbooster.XPBooster;
import com.keurig.xpbooster.language.Language;
import com.keurig.xpbooster.util.Chat;
import org.bukkit.command.CommandSender;

@CommandAlias("xpboost|expboost|exp|ex")
public class XPBoostReloadCommand extends BaseCommand {

    @Dependency
    private XPBooster plugin;

    @Subcommand("reload")
    public void onReload(CommandSender sender, String[] args) {
        plugin.lang.load();
        plugin.loadLanguage();
        plugin.config.load();
        plugin.loadDataConfig();
        Chat.message(sender, Language.RELOAD_COMMAND.toString());
    }

}
