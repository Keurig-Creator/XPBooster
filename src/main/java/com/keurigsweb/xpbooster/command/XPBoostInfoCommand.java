package com.keurigsweb.xpbooster.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.api.XPBoostAPI;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.global.GlobalBoost;
import com.keurigsweb.xpbooster.base.data.booster.global.HolidayBoost;
import com.keurigsweb.xpbooster.language.Language;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.NumUtil;
import com.keurigsweb.xpbooster.util.replacement.Replacement;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CommandAlias("booster")
public class XPBoostInfoCommand extends BaseCommand {

    @Default
    public void onInfo(Player player) {
        Replacement replace = Replacement.createReplacement(getName(), player.getName());
        if (XPBoostAPI.hasBoost(player.getUniqueId()) || XPBoostAPI.getGlobalMultiplier() > 0 || HolidayBoost.GLOBAL_MULTIPLIER > 0 || XPBoostPlugin.permisionMultiplier.containsKey(player.getUniqueId())) {
            EXPBoost boost = XPBoostAPI.getBoost(player.getUniqueId());
            List<String> chat = new ArrayList<>();

            chat.add("&8&l----- &e&lBooster Info &8&l-----\n");
            chat.add("\n");

            if (boost != null) {
                chat.add("&e&l" + NumUtil.formatMultiplier(boost.getMultiplier()) + " &8Your Booster");
                chat.add("&8&l- &7Time &e" + boost.getRemainingTimeFormat());
                chat.add("\n");
            }

            for (GlobalBoost globalBoost : XPBoostAPI.getBoostHandler().getGlobalBoosts()) {
                chat.add("&e&l" + NumUtil.formatMultiplier(globalBoost.getMultiplier()) + " &8Global Booster");
                chat.add("&8&l- &7By &e" + Bukkit.getPlayer(UUID.fromString(globalBoost.getUuid())).getName());
                chat.add("&8&l- &7Time &e" + globalBoost.getRemainingTimeFormat());
                chat.add("\n");
            }

            double permissionMultiplier = XPBoostPlugin.permisionMultiplier.getOrDefault(player.getUniqueId(), 0.0);
            if (permissionMultiplier > 0) {
                chat.add("&e&l" + NumUtil.formatMultiplier(permissionMultiplier) + " &8Permission Boost");
                chat.add("\n");
            }

            if (HolidayBoost.GLOBAL_MULTIPLIER > 0) {
                chat.add("&e&l" + NumUtil.formatMultiplier(HolidayBoost.GLOBAL_MULTIPLIER) + " &8Event Booster");
                // Add relevant information for event booster
                chat.add("\n");
            }

            double totalMultiplier = XPBoostAPI.getTotalBoost(player);
            double highestMultiplier = Math.max(Math.max(Math.max(permissionMultiplier, boost != null ? boost.getMultiplier() : 0), XPBoostAPI.getGlobalMultiplier()), HolidayBoost.GLOBAL_MULTIPLIER);

            StringBuilder total = new StringBuilder();
            total.append("&f&lTOTAL BONUS &7").append(NumUtil.formatMultiplier(totalMultiplier));

            if (highestMultiplier == (boost != null ? boost.getMultiplier() : 0)) {
                total.append(" &8| &cHighest Value");
            }

            chat.add(2, total.toString());
            chat.add(3, "\n");

            chat = chat.stream().map(Chat::color).collect(Collectors.toList());
            Chat.message(player, chat.toArray(new String[0]));

        } else {
            Chat.message(player, Language.NO_ACTIVE_BOOSTER.toString());
        }
    }

    @Default
    @CommandCompletion("@players")
    public void onInfo(CommandSender sender, OfflinePlayer target) {
        Replacement replace = Replacement.createReplacement(getName(), sender.getName(), target.getName());

        if (XPBoostAPI.hasBoost(target.getUniqueId())) {
            EXPBoost boost = XPBoostAPI.getBoost(target.getUniqueId());
            Chat.message(sender, "&8&l----- &eActive &6Booster Info &8&l-----\n \n&7Player &e" + target.getName() + "\n&7Multiplier &e" + boost.getMultiplier() + "\n&7Time remaining: &e" + boost.getRemainingTimeFormat() + "\n ");
        } else {
            Chat.message(sender, Language.TARGET_NO_ACTIVE_BOOSTER.toString());
        }
    }

}
