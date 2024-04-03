package com.keurig.xpbooster.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.api.XPBoostAPI;
import com.keurig.xpbooster.base.EXPBoost;
import com.keurig.xpbooster.base.InternalXPBoostHandler;
import com.keurig.xpbooster.base.Voucher;
import com.keurig.xpbooster.language.Language;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.NumUtil;
import com.keurig.xpbooster.util.Replacement;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;

@CommandAlias("xpbooster|xpboost|expboost|exp|ex")
@CommandPermission("xpbooster.admin")
public class XPBoostCommand extends BaseCommand {


    @Dependency
    private XPBoostPlugin plugin;

    @Default
    public void onCommand(CommandSender sender) {
        Chat.message(sender,
                "&8&l----- &6&lXPBooster &e&lCommands &8&l-----\n" +
                        " \n" + // empty Space
                        "&e/xpbooster &6info &7[player]\n" +
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

    @Subcommand("voucher")
    @Syntax("<player> <voucher> <multiplier> [time]")
    @CommandCompletion("@players")
    public void onVoucher(CommandSender sender, OfflinePlayer toSet, String v, String m, String[] args) {
        Replacement replace = Replacement.createReplacement(getName(), sender.getName(), toSet.getName());

        if (!NumUtil.isNumber(m, false)) {
            Chat.message(sender, Language.INVALID_NUMBER.toString());
            return;
        }

        double multiplier = Double.parseDouble(m);

        if (multiplier <= plugin.config.getDouble("minimum-multiplier")) {
            Chat.message(sender, Language.MINIMUM_MULTIPLIER.toString(replace));
            return;
        } else if (multiplier > plugin.config.getDouble("maximum-multiplier")) {
            Chat.message(sender, Language.MAXIMUM_MULTIPLIER.toString(replace));
            return;
        } else if (!plugin.config.getBoolean("full_range_multiplier") && !NumUtil.isWholeOrHalf(multiplier)) {
            Chat.message(sender, Language.FULL_RANGE_MULTIPLIER.toString(replace));
            return;
        }
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
            replace.addReplacement(Replacement.DURATION_REGEX, NumUtil.convertDateToStr(date));
        }

        Voucher voucher = plugin.getVoucherManager().getVoucher(v);
        if (voucher == null) {
            Chat.message(sender, Language.INVALID_VOUCHER.toString(replace));
            return;
        }


        toSet.getPlayer().getInventory().addItem(voucher.getItem());

//        sender.sendMessage(Language.GI.toString(replace));


    }

    @Subcommand("set")
    @Syntax("<player> <multiplier> [time]")
    @CommandCompletion("@players")
    public void onSet(CommandSender sender, OfflinePlayer toSet, String m, String[] args) {
        Replacement replace = Replacement.createReplacement(getName(), sender.getName(), toSet.getName());

        if (!NumUtil.isNumber(m, false)) {
            Chat.message(sender, Language.INVALID_NUMBER.toString());
            return;
        }

        double multiplier = Double.parseDouble(m);

        if (multiplier <= plugin.config.getDouble("minimum-multiplier")) {
            Chat.message(sender, Language.MINIMUM_MULTIPLIER.toString(replace));
            return;
        } else if (multiplier > plugin.config.getDouble("maximum-multiplier")) {
            Chat.message(sender, Language.MAXIMUM_MULTIPLIER.toString(replace));
            return;
        } else if (!plugin.config.getBoolean("full_range_multiplier") && !NumUtil.isWholeOrHalf(multiplier)) {
            Chat.message(sender, Language.FULL_RANGE_MULTIPLIER.toString(replace));
            return;
        }
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
            replace.addReplacement(Replacement.DURATION_REGEX, NumUtil.convertDateToStr(date));
        }


        EXPBoost expBoost = EXPBoost.builder()
                .uuid(toSet.getUniqueId())
                .multiplier(multiplier)
                .date(time).build();

        XPBoostAPI.setBoost(toSet.getUniqueId(), expBoost);

        sender.sendMessage(Language.SET_XPBOOST_MESSAGE.toString(replace));

    }

    @Subcommand("add")
    @Syntax("<player> [time]")
    @CommandCompletion("@players")
    public void onAdd(CommandSender sender, OfflinePlayer toSet, String timeDate) {
        Replacement replace = Replacement.createReplacement(getName(), sender.getName(), toSet.getName());
        replace.addReplacement(Replacement.DURATION_REGEX, "PERMANENT");

        if (!XPBoostAPI.hasBoost(toSet.getUniqueId())) {
            sender.sendMessage(Language.PLAYER_NO_BOOST.toString(replace));
            return;
        }

        EXPBoost boost = XPBoostAPI.getBoost(toSet.getUniqueId());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(boost.getDate());


        int extractNumber = NumUtil.extractNumber(timeDate);
        int calenderN = NumUtil.convertToCalendar(timeDate);

        if (extractNumber == -1 || calenderN == -1) {
            Chat.message(sender, Language.INVALID_DATE.toString(replace));
            return;
        }

        calendar.add(calenderN, extractNumber);
        replace.addReplacement(Replacement.DURATION_REGEX, NumUtil.convertDateToStr(timeDate));

        boost.setDate(calendar.getTime().getTime());
        plugin.getDataConfig().save();

        sender.sendMessage(Language.ADD_XPBOOST_MESSAGE.toString(replace));

    }

    @Subcommand("remove")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onRemove(CommandSender sender, OfflinePlayer toSet) {
        Replacement replace = Replacement.createReplacement(getName(), sender.getName(), toSet.getName());

        InternalXPBoostHandler boostHandler = plugin.getBoostHandler();

        if (XPBoostAPI.hasBoost(toSet.getUniqueId())) {
            XPBoostAPI.removeBoost(toSet.getUniqueId());
            sender.sendMessage(Language.REMOVE_XPBOOST_MESSAGE.toString(replace));
        } else {
            sender.sendMessage(Language.PLAYER_NO_BOOST.toString(replace));
        }
    }

    @Subcommand("info")
    public void onInfo(Player player) {

        Replacement replace = Replacement.createReplacement(getName(), player.getName());

        if (XPBoostAPI.hasBoost(player.getUniqueId())) {
            EXPBoost boost = XPBoostAPI.getBoost(player.getUniqueId());
            Chat.message(player, "&8&l----- &eActive &6Booster Info &8&l-----\n \n&7Multiplier &e" + boost.getMultiplier() + "\n&7Time remaining: &e" + boost.getRemainingTime() + "\n ");
        } else {
            Chat.message(player, Language.PLAYER_NO_BOOST.toString());
        }
    }

    @Subcommand("info")
    @CommandCompletion("@players")
    public void onInfo(CommandSender sender, OfflinePlayer target) {
        Replacement replace = Replacement.createReplacement(getName(), sender.getName(), target.getName());

        if (XPBoostAPI.hasBoost(target.getUniqueId())) {
            EXPBoost boost = XPBoostAPI.getBoost(target.getUniqueId());
            Chat.message(sender, "&8&l----- &eActive &6Booster Info &8&l-----\n \n&7Player &e" + target.getName() + "\n&7Multiplier &e" + boost.getMultiplier() + "\n&7Time remaining: &e" + boost.getRemainingTime() + "\n ");
        } else {
            Chat.message(sender, Language.TARGET_NO_BOOST.toString());
        }
    }

}
