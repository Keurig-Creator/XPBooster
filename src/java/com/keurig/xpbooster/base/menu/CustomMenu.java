package com.keurig.xpbooster.base.menu;

import com.keurig.xpbooster.base.menu.item.ActionItem;
import com.keurig.xpbooster.base.menu.item.ItemClickEvent;
import com.keurig.xpbooster.util.ItemBuilder;
import lombok.Builder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class CustomMenu extends Menu {

    @Builder
    public CustomMenu() {

    }

    @Override
    public int getSize() {
        return 18;
    }

    @Override
    public String getTitle() {
        return "&cSick Menu";
    }

    @Override
    public void onClick(ItemClickEvent e) {
        // Implement your custom onClick behavior here

    }

    @Override
    public void setup(Inventory inventory) {
        // Implement setup logic if needed

        ActionItem actionItem = new ActionItem(ItemBuilder.item(Material.PAPER).toItemStack());
        actionItem.addAction(e -> MenuManager.openMenu(OtherMenu.class, e.getPlayer()));

        addItems(actionItem);
    }

    @Override
    public boolean cancelClicks() {
        return false;
    }
}