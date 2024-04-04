package com.keurig.xpbooster.base.menu;

import lombok.Builder;
import org.bukkit.inventory.Inventory;

public class CustomMenu extends Menu {

    @Builder
    public CustomMenu(String title, int size, Menu parentMenu, ActionItem[] items) {
        super(title, size, parentMenu, items);
    }

    @Override
    public void onClick(ItemClickEvent e) {
        // Implement your custom onClick behavior here
    }

    @Override
    public void setup(Inventory inventory) {
        // Implement setup logic if needed
    }

    @Override
    public boolean cancelClicks() {
        return false;
    }
}