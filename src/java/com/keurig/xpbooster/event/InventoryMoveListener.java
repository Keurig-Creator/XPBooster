package com.keurig.xpbooster.event;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.api.XPBoostAPI;
import com.keurig.xpbooster.base.Voucher;
import com.keurig.xpbooster.base.VoucherManager;
import com.keurig.xpbooster.base.shop.ShopMenu;
import com.keurig.xpbooster.language.Language;
import com.keurig.xpbooster.util.Chat;
import lombok.AllArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@AllArgsConstructor
public class InventoryMoveListener implements Listener {

    private XPBoostPlugin plugin;

    @EventHandler
    public void onInventoryMove(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        ShopMenu shop = plugin.getShopManager().getShop();
        VoucherManager voucherManager = plugin.getVoucherManager();

        if (event.getView().getTitle().equals(shop.getName())) {
            String localizedName = event.getCurrentItem().getItemMeta().getLocalizedName();

            if (localizedName == null) {
                return;
            }

            Voucher voucher = voucherManager.getVoucher(localizedName);

            if (voucher != null) {
                if (shop.isInstantClaim(voucher)) {

                    if (XPBoostAPI.hasBoost(player.getUniqueId())) {
                        if (XPBoostAPI.getBoost(player.getUniqueId()).getMultiplier() != voucher.getMultiplier()) {
                            if (!event.isShiftClick()) {
                                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                                Chat.message(player, "&cYou already have a different type XP boost. To override and use new one hold shift and left click.");
                            } else {
                                XPBoostAPI.removeBoost(player.getUniqueId());
                            }
                        }
                        XPBoostAPI.addBoost(player.getUniqueId(), voucher);
                    } else {
                        XPBoostAPI.addBoost(player.getUniqueId(), voucher);
                    }
                    player.closeInventory();
                } else {
                    player.getInventory().addItem(voucher.getItem());
                    player.closeInventory();
                }

                player.sendMessage(Language.ADD_XPBOOST_MESSAGE.toString());
            }

            event.setCancelled(true);
        }
    }
}
