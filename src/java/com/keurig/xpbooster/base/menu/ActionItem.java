package com.keurig.xpbooster.base.menu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ActionItem {


    private final Set<ItemAction> actions = new HashSet<>();
    private final ItemStack item;

    public ActionItem(ItemStack item) {
        this.item = item;
    }


    public void addAction(ItemAction itemAction) {
        actions.add(itemAction);
    }

    public void onClick(ItemClickEvent e) {
        for (ItemAction action : actions) {
            action.onClick(e);
        }
    }
}
