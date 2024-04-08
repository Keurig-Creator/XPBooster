package com.keurigsweb.xpbooster.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

    public static void fillInventory(Inventory inventory, ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType().isAir()) {
                inventory.setItem(i, itemStack);
            }
        }
    }

    // Fills the entire GUI with glass panes, leaving space for enchantments
    public static void fillBorder(Inventory inventory, ItemStack itemStack) {
        int size = inventory.getSize();
        int rows = size / 9; // Assuming standard inventory size

        // Fill top and bottom rows
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, itemStack);
            inventory.setItem(size - 9 + i, itemStack);
        }

        // Fill left and right columns
        for (int row = 1; row < rows - 1; row++) {
            inventory.setItem(row * 9, itemStack);
            inventory.setItem(row * 9 + 8, itemStack);
        }
    }
}
