package com.keurigsweb.xpbooster.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.api.XPBoostAPI;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.Booster;
import com.keurigsweb.xpbooster.base.data.booster.global.GlobalBoost;
import com.keurigsweb.xpbooster.base.handler.InternalXPBoostHandler;
import com.keurigsweb.xpbooster.language.Language;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.NumUtil;
import com.keurigsweb.xpbooster.util.TimeUtil;
import com.keurigsweb.xpbooster.util.replacement.Replacement;
import org.bukkit.Bukkit;
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

        String voucher = plugin.getBoosterManager().isVouchersEnabled() ? "&e/xpbooster &6voucher &7<player> <voucher>\n" : "";
        Chat.message(sender,
                "&8&l----- &6&lXPBooster &e&lCommands &8&l-----\n" +
                        "&6Server Info &e" + Bukkit.getServer().getVersion() + "\n",
                "&6Version &e" + plugin.getDescription().getVersion() + "\n",
                " \n" + // empty Space
                        "&e/xpbooster &6info &7[player]\n" +
                        voucher +
                        "&e/xpbooster &6set &7<player> <multiplier> &8[time]\n" +
                        "&e/xpbooster &6add &7<player> &8[time]\n" +
                        "&e/xpbooster &6remove &7<player>\n" +
                        "&e/xpbooster &6reload\n" +
                        " \n" + // First empty line
                        "&e/booster &8| &6booster info\n",
                "&e/" + plugin.getReplacements().replace("%shopcommand").split("\\|")[0] + " &8| &6shop command\n",
                " \n" + // Second empty line (with space)
                        "&7<> &7- &fRequired Argument\n" +
                        "&8[] &7- &fOptional Argument\n"
        );
    }

    @Subcommand("global")
    @Syntax("<player> <multiplier> [time]")
    @CommandCompletion("@players")
    public void onGlobal(CommandSender sender, OfflinePlayer toSet, String m, String[] args) {
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

        if (args.length == 1) {
            replace.addReplacement(Replacement.DURATION_REGEX, args[0]);
        }

        XPBoostAPI.addGlobalBoost(toSet.getPlayer(), multiplier, args[0]);

        String message = Language.GLOBAL_MESSAGE.toString(replace);
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    @Subcommand("resetglobal")
    public void onResetGlobal(CommandSender sender) {
        Replacement replace = Replacement.createReplacement(getName(), sender.getName());
        XPBoostAPI.removeGlobalBoost(XPBoostAPI.getGlobalMultiplier());

        String message = Language.GLOBAL_RESET_MESSAGE.toString(replace);
        sender.sendMessage(message);
    }

    @Subcommand("voucher")
    @Syntax("<player> <voucher> <multiplier> [time]")
    @CommandCompletion("@players @boosters")
    public void onVoucher(CommandSender sender, OnlinePlayer player, Booster booster) {
        if (!plugin.getBoosterManager().isVouchersEnabled()) {
            Chat.message(sender, "&cVouchers are disabled in the config");
            return;
        }

        Replacement replace = Replacement.createReplacement(getName(), sender.getName(), player.getPlayer().getName());
        replace.addReplacement(Replacement.DURATION_REGEX, booster.getTime());
        replace.addReplacement(Replacement.MULTIPLIER_REGEX, NumUtil.formatMultiplier(booster.getMultiplier()));
        replace.addReplacement(Replacement.MULTIPLIER_REGEX, booster.getName());


        long time = 0;
        Calendar calendar = Calendar.getInstance();

        int extractNumber = NumUtil.extractNumber(booster.getTime());
        int calenderN = NumUtil.convertToCalendar(booster.getTime());

        if (extractNumber == -1 || calenderN == -1) {
            Chat.message(sender, Language.INVALID_DATE_FORMAT.toString(replace));
            return;
        }

        calendar.add(calenderN, extractNumber);
        replace.addReplacement(Replacement.DURATION_REGEX, NumUtil.convertDateToStr(booster.getTime()));

        player.getPlayer().getInventory().addItem(booster.getVoucher().getItem());
        Replacement replacement = booster.getReplacement(Language.GIVE_VOUCHER_MESSAGE.toString());
        replacement.addReplacement(Replacement.TARGET_REGEX, player.getPlayer().getName());
        replacement.addReplacement(Replacement.NAME_REGEX, booster.getId().toUpperCase());
        sender.sendMessage(replacement.getReplacement());

        if (sender != player.getPlayer()) {
            player.getPlayer().sendMessage(replacement.getReplacement(Language.VOUCHER_RECEIVE_MESSAGE.toString()));
        }
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
                Chat.message(sender, Language.INVALID_DATE_FORMAT.toString(replace));
                return;
            }

            calendar.add(calenderN, extractNumber);
            time = calendar.getTime().getTime();
            replace.addReplacement(Replacement.DURATION_REGEX, NumUtil.convertDateToStr(date));
        }


        EXPBoost expBoost = EXPBoost.builder()
                .uuid(toSet.getUniqueId())
                .multiplier(multiplier)
                .time(time).build();

        XPBoostAPI.setBoost(toSet.getUniqueId(), expBoost);

        sender.sendMessage(Language.SET_BOOST_MESSAGE.toString(replace));

    }

    @Subcommand("add")
    @Syntax("<player> [time]")
    @CommandCompletion("@players")
    public void onAdd(CommandSender sender, OfflinePlayer toSet, String timeDate) {
        Replacement replace = Replacement.createReplacement(getName(), sender.getName(), toSet.getName());
        replace.addReplacement(Replacement.DURATION_REGEX, "PERMANENT");

        if (!XPBoostAPI.hasBoost(toSet.getUniqueId())) {
            sender.sendMessage(Language.NO_ACTIVE_BOOSTER.toString(replace));
            return;
        }

        EXPBoost boost = XPBoostAPI.getBoost(toSet.getUniqueId());
        XPBoostAPI.addBoost(toSet.getUniqueId(), boost.getMultiplier(), timeDate);
        replace.addReplacement(Replacement.DURATION_REGEX, timeDate);

        sender.sendMessage(Language.ADD_BOOST_MESSAGE.toString(replace));

    }

    @Subcommand("remove")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onRemove(CommandSender sender, OfflinePlayer toSet) {
        Replacement replace = Replacement.createReplacement(getName(), sender.getName(), toSet.getName());

        InternalXPBoostHandler boostHandler = plugin.getBoostHandler();

        if (XPBoostAPI.hasBoost(toSet.getUniqueId())) {
            XPBoostAPI.removeBoost(toSet.getUniqueId());
            sender.sendMessage(Language.REMOVE_BOOST_MESSAGE.toString(replace));
        } else {
            sender.sendMessage(Language.NO_ACTIVE_BOOSTER.toString(replace));
        }
    }

    @Subcommand("info")
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
