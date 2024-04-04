package com.keurig.xpbooster.base.menu;

import com.keurig.xpbooster.base.menu.item.ActionItem;
import com.keurig.xpbooster.base.menu.item.ItemAction;
import com.keurig.xpbooster.base.menu.item.ItemClickEvent;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.ItemBuilder;
import lombok.Builder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class OtherMenu extends Menu {

    @Builder
    public OtherMenu() {

    }

    @Override
    public int getSize() {
        return 18;
    }

    @Override
    public String getTitle() {
        return "&f&lOther Menu";
    }

    @Override
    public void onClick(ItemClickEvent e) {
        // Implement your custom onClick behavior here
    }

    @Override
    public void setup(Inventory inventory) {
        // Implement setup logic if needed

        ActionItem actionItem = new ActionItem(ItemBuilder.item(Material.PAPER).toItemStack());
        actionItem.addAction(new ItemAction() {
            @Override
            public void onClick(ItemClickEvent e) {
                Chat.log(e.getMenu().playerMenu.getHistory().size());
                e.getMenu().playerMenu.getLast().actions.clear();
                MenuManager.openMenu(e.getMenu().playerMenu.getLast(), playerMenu.getPlayer());
            }
        });

        addItems(actionItem);
        setActionItems(inventory);
    }

    @Override
    public boolean cancelClicks() {
        return false;
    }
}