package com.keurig.xpbooster.base.menu;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.menu.data.Menu;
import com.keurig.xpbooster.base.menu.item.ItemClickEvent;
import com.keurig.xpbooster.base.shop.ShopConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

@Getter
@Setter
public class ShopMenu extends Menu {

    private int size;
    private String title;

    private ShopConfig shopConfig;

    public ShopMenu() {
        shopConfig = XPBoostPlugin.getInstance().getShopManager().getShop();
    }

    @Override
    public int getSize() {
        return shopConfig.getSize();
    }

    @Override
    public String getTitle() {
        return shopConfig.getGuiName();
    }

    @Override
    public void onClick(ItemClickEvent e) {
    }

    @Override
    public void setup(Inventory inventory) {

        shopConfig.setShopMenu(this);
//        List<ActionItem> actionItems = new ArrayList<>();


//        for (ShopItem item : shopConfig.getItems()) {
//            actionItems.add(new ActionItem(item.getItem()).addAction(e -> {
//                e.getPlayerMenu().setData(item.getItem().getTranslationKey(), item.getVoucher());
//            }));
//        }

//        addItems(actionItems.toArray(new ActionItem[0]));
    }

    @Override
    public boolean cancelClicks() {
        return true;
    }
}
