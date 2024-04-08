package com.keurigsweb.xpbooster.base.data.ui;

import com.cryptomorin.xseries.XMaterial;
import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.api.XPBoostAPI;
import com.keurigsweb.xpbooster.base.data.booster.ShopBooster;
import com.keurigsweb.xpbooster.base.data.shop.ShopProfile;
import com.keurigsweb.xpbooster.base.menu.MenuFill;
import com.keurigsweb.xpbooster.base.menu.data.Menu;
import com.keurigsweb.xpbooster.base.menu.data.MenuManager;
import com.keurigsweb.xpbooster.base.menu.item.ActionItem;
import com.keurigsweb.xpbooster.base.menu.item.ItemClickEvent;
import com.keurigsweb.xpbooster.util.InventoryUtil;
import com.keurigsweb.xpbooster.util.ItemBuilder;
import com.keurigsweb.xpbooster.util.NumUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
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
            ItemBuilder itemBuilder = ItemBuilder.item(shopBooster.getItem());

            if (XPBoostAPI.hasBoost(playerMenu.getPlayer().getUniqueId()) && XPBoostAPI.getMultiplier(playerMenu.getPlayer()) == shopBooster.getBooster().getMultiplier()) {

                itemBuilder.addLoreLine("");
                itemBuilder.addLoreLine("&aActivated");
            }
            ActionItem action = new ActionItem(itemBuilder.toItemStack()).addAction(e -> {

                // Check if the user should be given the voucher or a confirmation menu
                getPlayerMenu().setData("shop", shop);
                getPlayerMenu().setData("booster", shopBooster.getBooster());
                getPlayerMenu().setData("instantClaim", getShopProfile().isInstantClaim());

                MenuManager.openMenu(ConfirmMenu.class, e.getPlayer());
            });


            int nextSlot = getNextSlot(shopBooster.getSlot());
            addAction(action, nextSlot);
        }

        ItemBuilder itemBuilder = ItemBuilder.item(XMaterial.WRITABLE_BOOK.parseMaterial())
                .setName("&e&lYour Booster")
                .setGlow(true);

        if (XPBoostAPI.hasBoost(playerMenu.getPlayer().getUniqueId())) {
            String remainingTime = XPBoostAPI.getRemainingTime(playerMenu.getPlayer());
            double multiplier = XPBoostAPI.getMultiplier(playerMenu.getPlayer());

            if (remainingTime != null && multiplier != -1) {
                itemBuilder.addLoreLine("");
                itemBuilder.addLoreLine("&6Remaining Time: &e" + remainingTime);
                itemBuilder.addLoreLine("&6XP Multiplier: &e" + NumUtil.formatMultiplier(multiplier));
            }
        } else {
            itemBuilder.addLoreLine("&7You have no booster");
        }

        addAction(new ActionItem(itemBuilder.toItemStack()).addAction(e -> {
            String remainingTime = XPBoostAPI.getRemainingTime(playerMenu.getPlayer());

            if (remainingTime == null)
                return;
            itemBuilder.addLoreLine("&6Remaining Time: &e" + remainingTime, 1);
            e.setCurrentItem(itemBuilder.toItemStack());
        }), 4);
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
