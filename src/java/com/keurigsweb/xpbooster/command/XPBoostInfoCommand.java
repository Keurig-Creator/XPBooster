package com.keurigsweb.xpbooster.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import com.keurigsweb.xpbooster.api.XPBoostAPI;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.global.GlobalBoost;
import com.keurigsweb.xpbooster.base.data.booster.global.HolidayBoost;
import com.keurigsweb.xpbooster.language.Language;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.ConfigValue;
import com.keurigsweb.xpbooster.util.NumUtil;
import com.keurigsweb.xpbooster.util.replacement.Replacement;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CommandAlias("booster")
public class XPBoostInfoCommand extends BaseCommand {

    @Default
    public void onInfo(Player player) {
        Replacement replace = Replacement.createReplacement(getName(), player.getName());
        if (XPBoostAPI.hasBoost(player.getUniqueId()) || XPBoostAPI.getGlobalMultiplier() > 0 || HolidayBoost.GLOBAL_MULTIPLIER > 0) {
            EXPBoost boost = XPBoostAPI.getBoost(player.getUniqueId());
            List<String> chat = new ArrayList<>();

            chat.add("&8&l----- &e&lBooster Info &8&l-----\n");
            chat.add("\n");

            double globalMultiplier = 0;
            double holidayMultiplier = 0;
            double multiplier = 0;

            if (boost != null) {
                multiplier = boost.getMultiplier();
            }

            if (!XPBoostAPI.getBoostHandler().getGlobalBoosts().isEmpty()) {
                for (GlobalBoost globalBoost : XPBoostAPI.getBoostHandler().getGlobalBoosts()) {
                    if (ConfigValue.GLOBAL_STACKING) {
                        globalMultiplier += globalBoost.getMultiplier();
                    } else if (globalBoost.getMultiplier() > globalMultiplier) {
                        globalMultiplier = globalBoost.getMultiplier();
                    }
                }
            }

            Chat.log(HolidayBoost.GLOBAL_MULTIPLIER);
            if (HolidayBoost.GLOBAL_MULTIPLIER > 0) {
                holidayMultiplier = HolidayBoost.GLOBAL_MULTIPLIER;
            }

            if (multiplier > 0) {
                chat.add("&e&l" + NumUtil.formatMultiplier(multiplier) + " &8Your Booster");
                chat.add("&8&l- &7Time &e" + boost.getRemainingTime());
                chat.add("\n");
            }

            if (globalMultiplier > 0) {
                if (!XPBoostAPI.getBoostHandler().getGlobalBoosts().isEmpty()) {
                    for (GlobalBoost globalBoost : XPBoostAPI.getBoostHandler().getGlobalBoosts()) {
                        chat.add("&e&l" + NumUtil.formatMultiplier(globalBoost.getMultiplier()) + " &8Global Booster");
                        chat.add("&8&l- &7By &e" + Bukkit.getPlayer(UUID.fromString(globalBoost.getUuid())).getName());
                        chat.add("&8&l- &7Time &e" + globalBoost.getRemainingTime());
                        chat.add("\n");
                    }
                }
            }


            if (holidayMultiplier > 0) {
                chat.add("&e&l" + NumUtil.formatMultiplier(holidayMultiplier) + " &8Event Booster");
                // Add relevant information for event booster
                chat.add("\n");
            }


            StringBuilder total = new StringBuilder();
            double highestMultiplier = Math.max(Math.max(multiplier, globalMultiplier), holidayMultiplier);
            double totalMultiplier = Math.abs(multiplier + globalMultiplier + holidayMultiplier);
            if (!ConfigValue.GLOBAL_STACKING) {
                total.append("&f&lTOTAL BONUS &7").append(NumUtil.formatMultiplier(highestMultiplier));
            } else {
                Chat.log(multiplier);
                Chat.log(globalMultiplier);
                Chat.log(holidayMultiplier);
                total.append("&f&lTOTAL BONUS &7").append(NumUtil.formatMultiplier(totalMultiplier));
            }

            if (highestMultiplier == multiplier) {
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
            Chat.message(sender, "&8&l----- &eActive &6Booster Info &8&l-----\n \n&7Player &e" + target.getName() + "\n&7Multiplier &e" + boost.getMultiplier() + "\n&7Time remaining: &e" + boost.getRemainingTime() + "\n ");
        } else {
            Chat.message(sender, Language.TARGET_NO_ACTIVE_BOOSTER.toString());
        }
    }

}
