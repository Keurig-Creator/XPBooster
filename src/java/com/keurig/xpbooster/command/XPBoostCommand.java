package com.keurig.xpbooster.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.keurig.xpbooster.XPBooster;
import com.keurig.xpbooster.base.EXPBoost;
import com.keurig.xpbooster.base.XPBoostHandler;
import com.keurig.xpbooster.language.Language;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.NumUtil;
import com.keurig.xpbooster.util.Replacement;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;

@CommandAlias("xpboost|expboost|exp|ex")
public class XPBoostCommand extends BaseCommand {

    @Dependency
    private XPBooster plugin;

    @Default
    public void onCommand(CommandSender sender) {
        Chat.message(sender,
                "&8&l----- &6&lXPBooster &e&lCommands &8&l-----\n" +
                        " \n" + // empty Space
                        "&e/xpbooster &6info &7[player]" +
                        "&e/xpbooster &6set &7<player> <multiplier> &8[time]\n" +
                        "&e/xpbooster &6add &7<player> &8[time]\n" +
                        "&e/xpbooster &6remove &7<player>\n" +
                        "&e/xpbooster &6reload\n" +
                        "\n" + // First empty line
                        " \n" + // Second empty line (with space)
                        "&7<> &7- &fRequired Argument\n" +
                        "&8[] &7- &fOptional Argument\n"
        );
    }

    @Subcommand("set")
    @Syntax("<player> <multiplier> [time]")
    @CommandCompletion("@players")
    public void onSet(CommandSender sender, OfflinePlayer toSet, String m, String[] args) {
        Replacement replace = Replacement.createReplacement(getName(), sender.getName(), toSet.getName());

        Double multiplier = validateMultiplier(sender, m, replace);
        if (multiplier == null) return;

        replace.addReplacement(Replacement.DURATION_REGEX, "PERMANENT");
        replace.addReplacement(Replacement.MULTIPLIER_REGEX, String.valueOf(multiplier));

        long time = 0;
        if (args.length == 1) {
            Calendar calendar = Calendar.getInstance();

            String date = args[0];

            int extractNumber = NumUtil.extractNumber(date);
            int calenderN = NumUtil.convertToCalendar(date);

            if (extractNumber == -1 || calenderN == -1) {
                Chat.message(sender, Language.INVALID_DATE.toString(replace));
                return;
            }

            calendar.add(calenderN, extractNumber);
            time = calendar.getTime().getTime();

            Chat.log(NumUtil.convertDateToStr(date));
            replace.addReplacement(Replacement.DURATION_REGEX, NumUtil.convertDateToStr(date));
        }


        EXPBoost expBoost = EXPBoost.builder()
                .uuid(toSet.getUniqueId())
                .multiplier(multiplier)
                .date(time).build();

        plugin.getBoostHandler().addBoost(toSet.getPlayer().getUniqueId(), expBoost);

        sender.sendMessage(Language.SET_XPBOOST_MESSAGE.toString(replace));

    }

    @Subcommand("remove")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onRemove(CommandSender sender, OfflinePlayer toSet) {
        Replacement replace = Replacement.createReplacement(getName(), sender.getName(), toSet.getName());

        XPBoostHandler boostHandler = plugin.getBoostHandler();

        if (boostHandler.hasBoost(toSet.getUniqueId())) {
            boostHandler.removeBoost(toSet.getUniqueId());
            sender.sendMessage(Language.REMOVE_XPBOOST_MESSAGE.toString(replace));
        } else {
            sender.sendMessage(Language.PLAYER_NO_BOOST.toString(replace));
        }
    }

    @Subcommand("info")
    public void onInfo(Player player) {

        Replacement replace = Replacement.createReplacement(getName(), player.getName());

        if (plugin.getBoostHandler().hasBoost(player.getUniqueId())) {
            Chat.message(player, "&aYou have an active EXP BOOSTER!");
        } else {
            Chat.message(player, "&cYou have no active boosters");
        }
    }

    @Subcommand("info")
    @CommandCompletion("@players")
    public void onInfo(CommandSender sender, OfflinePlayer target) {
        Replacement replace = Replacement.createReplacement(getName(), sender.getName(), target.getName());

        if (plugin.getBoostHandler().hasBoost(target.getUniqueId())) {
            EXPBoost boost = plugin.getBoostHandler().getBoost(target.getUniqueId());
            Chat.message(sender, "&aTarget has an active booster: Multiplier: " + boost.getMultiplier() + ", Remaining Time: " + boost.getRemainingTime());
        } else {
            Chat.message(sender, "&cTarget has no active boosters");
        }
    }

    private Double validateMultiplier(CommandSender sender, String m, Replacement replace) {
        if (!NumUtil.isNumber(m, false)) {
            Chat.message(sender, Language.INVALID_NUMBER.toString());
            return null;
        }

        double multiplier = Double.parseDouble(m);

        if (multiplier <= plugin.config.getDouble("minimum_multiplier")) {
            Chat.message(sender, Language.MINIMUM_MULTIPLIER.toString(replace));
            return null;
        } else if (multiplier > plugin.config.getDouble("maximum_multiplier")) {
            Chat.message(sender, Language.MAXIMUM_MULTIPLIER.toString(replace));
            return null;
        } else if (!plugin.config.getBoolean("full_range_multiplier") && !NumUtil.isWholeOrHalf(multiplier)) {
            Chat.message(sender, Language.FULL_RANGE_MULTIPLIER.toString(replace));
            return null;
        }
        return multiplier;
    }

}
