package s8u.plugin.cash.gui;

import java.text.DecimalFormat;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import s8u.plugin.cash.api.CashAPI;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.FakeIcon;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.common.api.player.UPlayer;

public class ShopColorGUI extends GUI {

  public ShopColorGUI() {
    super("U-Cash/ShopColor", "색깔닉네임 상점", 5);

    int i = 0;
    for (; i <= 9; i++) {
      setIcon(i % 7 + 2, (int) i / 7 + 2, new ShopColorIcon(String.valueOf(i)));
    }

    i = 0;
    for (char c = 'a'; c <= 'e'; c++, i++) {
      setIcon(i < 4 ? i + 5 : i - 2, i < 4 ? 3 : 4, new ShopColorIcon(String.valueOf(c)));
    }

    //

    Icon deco = new Icon(new ItemBuilder("160:15")
        .displayName("")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };

    Icon sign = new Icon(new ItemBuilder(Material.SIGN)
        .displayName("§e§l색깔닉네임 상점 이용 안내")
        .lore("§f원하는 색깔을 클릭하여 색깔을 구입할 수 있습니다.")
        .lore("§f구입한 색깔은 '/색깔닉네임' 명령어로 착용할 수 있습니다.")
        .lore("")
        .lore("§f클릭 시 §e색깔닉네임 선택 페이지§f로 이동합니다.")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        CashAPI.getColorSelectGUI().open(e.getPlayer());
      }
    };

    FakeIcon currentCash = new FakeIcon(new ItemStack(Material.GOLD_INGOT)) {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        return new ItemBuilder(Material.GOLD_INGOT)
            .displayName("§e§l캐시 잔액")
            .lore("§f" + new DecimalFormat("#,###").format(CashAPI.getCash(up.getPlayerKey())) + " §e캐시")
            .build();
      }
    };

    for (i = 1; i <= 9; i++) {
      if (i == 5) continue;
      setIcon(i, 1, deco);
      setIcon(i, 5, deco);
    }

    for (i = 2; i <= 4; i++) {
      setIcon(1, i, deco);
      setIcon(9, i, deco);
    }
    setIcon(5, 1, sign);
    setIcon(5, 5, currentCash);


    updateAsynchronously();
  }

}