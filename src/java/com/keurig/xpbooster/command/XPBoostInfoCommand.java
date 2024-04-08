package com.keurig.xpbooster.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.keurig.xpbooster.api.XPBoostAPI;
import com.keurig.xpbooster.base.data.EXPBoost;
import com.keurig.xpbooster.language.Language;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.replacement.Replacement;
import org.bukkit.entity.Player;

@CommandAlias("booster")
public class XPBoostInfoCommand extends BaseCommand {

    @Default
    public void onInfo(Player player) {

        Replacement replace = Replacement.createReplacement(getName(), player.getName());

        if (XPBoostAPI.hasBoost(player.getUniqueId())) {
            EXPBoost boost = XPBoostAPI.getBoost(player.getUniqueId());
            Chat.message(player, "&8&l----- &eActive &6Booster Info &8&l-----\n \n&7Multiplier &e" + boost.getMultiplier() + "\n&7Time remaining: &e" + boost.getRemainingTime() + "\n ");
        } else {
            Chat.message(player, Language.NO_ACTIVE_BOOSTER.toString());
        }
    }

}
