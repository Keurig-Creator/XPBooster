package com.keurig.xpbooster.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.menu.ActionItem;
import com.keurig.xpbooster.base.menu.CustomMenu;
import com.keurig.xpbooster.base.menu.GobackMenu;
import com.keurig.xpbooster.base.menu.TestMenu;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CommandAlias("%shopcommand")
public class XPBoostShopCommand extends BaseCommand {

    @Dependency
    private XPBoostPlugin plugin;

    @Default
    public void onCommand(Player player) {
        ActionItem item = new ActionItem(ItemBuilder.item(Material.PAPER).setName("&aGO TO OTHER MENU").toItemStack());
        item.addAction(e -> {

            if (!e.isShiftClick())
                return;

            if (e.getItem().getType().equals(Material.PAPER)) {
                ActionItem goback = new ActionItem(ItemBuilder.item(Material.REDSTONE_BLOCK).setName("&cGO BACK TO MAIN MENU").toItemStack());
                goback.addAction(e1 -> {
                    e1.goback();

                    Chat.log(e1.getMenu().getParent().getAction(0).getItem().getItemMeta().getDisplayName());
                });
                GobackMenu gobackMenu = new GobackMenu(e.getMenu(), goback);

                gobackMenu.open(e.getPlayer());
                e.setCurrentItem(ItemBuilder.item(Material.SADDLE).toItemStack());
            }

        });

        CustomMenu test = CustomMenu.builder()
                .title("&fTest &aMenu")
                .size(18)
                .parentMenu(null)
                .items(new ActionItem[]{item}) // Pass your items array here
                .build();

        CustomMenu othermenu = CustomMenu.builder()
                .title("&fTest &aMenu")
                .size(18)
                .parentMenu(test)
                .items(new ActionItem[]{item}) // Pass your items array here
                .build();

        TestMenu menu = new TestMenu("&fTest &aMenu", 18, null, item);
        menu.open(player);
    }

}
