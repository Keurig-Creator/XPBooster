package com.keurig.xpbooster.event;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.api.XPBoostAPI;
import com.keurig.xpbooster.base.EXPBoost;
import com.keurig.xpbooster.base.Voucher;
import com.keurig.xpbooster.base.VoucherManager;
import com.keurig.xpbooster.base.shop.ShopMenu;
import com.keurig.xpbooster.util.NumUtil;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Calendar;

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

                    Calendar calendar = Calendar.getInstance();

                    int extractNumber = NumUtil.extractNumber(voucher.getTime());
                    int calenderN = NumUtil.convertToCalendar(voucher.getTime());

                    calendar.add(calenderN, extractNumber);

                    EXPBoost expBoost = EXPBoost.builder()
                            .uuid(player.getUniqueId())
                            .multiplier(voucher.getMultiplier())
                            .date(calendar.getTime().getTime()).build();

                    XPBoostAPI.addBoost(player.getUniqueId(), expBoost);
                } else {
                    player.getInventory().addItem(voucher.getItem());
                    player.closeInventory();
                }
            }

            event.setCancelled(true);
        }
    }
}
