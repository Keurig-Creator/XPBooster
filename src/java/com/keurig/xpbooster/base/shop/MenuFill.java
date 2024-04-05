package com.keurig.xpbooster.base.shop;

import com.keurig.xpbooster.util.ItemBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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
}