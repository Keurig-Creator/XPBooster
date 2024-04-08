package com.keurig.xpbooster.base.data.ui;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.data.booster.ShopBooster;
import com.keurig.xpbooster.base.data.shop.ShopProfile;
import com.keurig.xpbooster.base.menu.data.Menu;
import com.keurig.xpbooster.base.menu.data.MenuManager;
import com.keurig.xpbooster.base.menu.item.ActionItem;
import com.keurig.xpbooster.base.menu.item.ItemClickEvent;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.InventoryUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

@Getter
@Setter
public class ShopMenu extends Menu {

    private int size;
    private String title;

    private ShopProfile shopProfile;

    public ShopMenu() {
        shopProfile = XPBoostPlugin.getInstance().getShopManager().getShop();
    }

    @Override
    public int getSize() {
        return shopProfile.getSize();
    }

    @Override
    public String getTitle() {
        return shopProfile.getGuiName();
    }

    @Override
    public void onClick(ItemClickEvent e) {
    }

    @Override
    public void setup(Inventory inventory) {

        setShopMenu(inventory);
//        List<ActionItem> actionItems = new ArrayList<>();


//        for (ShopItem item : shopConfig.getItems()) {
//            actionItems.add(new ActionItem(item.getItem()).addAction(e -> {
//                e.getPlayerMenu().setData(item.getItem().getTranslationKey(), item.getVoucher());
//            }));
//        }

//        addItems(actionItems.toArray(new ActionItem[0]));
    }

    public void setShopMenu(Inventory inventory) {
        ShopProfile shop = XPBoostPlugin.getInstance().getShopManager().getShop();

        for (ShopBooster shopBooster : shop.getBoosters()) {
            ActionItem action = new ActionItem(shopBooster.getItem()).addAction(e -> {
                Chat.log(shop.isInstantClaim());

                // Check if the user should be given the voucher or a confirmation menu
                getPlayerMenu().setData("shop", shop);
                getPlayerMenu().setData("booster", shopBooster.getBooster());
                getPlayerMenu().setData("instantClaim", getShopProfile().isInstantClaim());

                MenuManager.openMenu(ConfirmMenu.class, e.getPlayer());
            });


            int nextSlot = getNextSlot(shopBooster.getSlot());
            addAction(action, nextSlot);
        }

        if (shop.getFill().getType().equals(MenuFill.FillType.FILL)) {
            InventoryUtil.fillInventory(inventory, shop.getFill().getMaterial());
        } else {
            InventoryUtil.fillBorder(inventory, shop.getFill().getMaterial());
        }
    }

    @Override
    public boolean cancelClicks() {
        return true;
    }
}
