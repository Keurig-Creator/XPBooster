package com.keurigsweb.xpbooster.base.menu.item;

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
    private int slot;

    public ActionItem(ItemStack item) {
        this.item = item;
    }


    public ActionItem addAction(ItemAction itemAction) {
        actions.add(itemAction);

        return this;
    }

    public void onClick(ItemClickEvent e) {
        for (ItemAction action : actions) {
            action.onClick(e);
        }
    }
}
