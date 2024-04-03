package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.util.ItemBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MenuFill {

    private FillType type;
    private ItemStack material;

    @AllArgsConstructor
    @Getter
    public enum FillType {
        FILL("FILL"),
        BORDER("BORDER");

        private final String value;
    }

    @Builder(builderMethodName = "builder", buildMethodName = "buildInternal")
    public static MenuFill create(String fillName, String materialName) {
        FillType type = FillType.valueOf(fillName);
        ItemBuilder material = ItemBuilder.fromString(materialName);
        return new MenuFill(type, material.toItemStack());
    }

    public void fillInventory(Inventory inventory) {
        switch (type) {
            case FILL:
                for (int i = 0; i < inventory.getSize(); i++) {
                    if (inventory.getItem(i) == null || inventory.getItem(i).getType().isAir()) {
                        inventory.setItem(i, material);
                    }
                }
                break;

            case BORDER:
                fillBorder(inventory);
                break;
        }
    }

    // Fills the entire GUI with glass panes, leaving space for enchantments
    private void fillBorder(Inventory inventory) {
        int size = inventory.getSize();
        int rows = size / 9; // Assuming standard inventory size

        // Fill top and bottom rows
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, material);
            inventory.setItem(size - 9 + i, material);
        }

        // Fill left and right columns
        for (int row = 1; row < rows - 1; row++) {
            inventory.setItem(row * 9, material);
            inventory.setItem(row * 9 + 8, material);
        }
    }
}