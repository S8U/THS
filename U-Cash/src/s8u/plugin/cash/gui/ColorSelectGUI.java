package s8u.plugin.cash.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.Icon;

public class ColorSelectGUI extends GUI {

  public ColorSelectGUI() {
    super("U-Cash/ColorSelect", "닉네임 색깔 선택", 5);

    int i = 0;
    for (; i <= 9; i++) {
      setIcon(i % 7 + 2, (int) i / 7 + 2, new ColorSelectIcon(String.valueOf(i)));
    }

    i = 0;
    for (char c = 'a'; c <= 'f'; c++, i++) {
      setIcon(i < 4 ? i + 5 : i - 2, i < 4 ? 3 : 4, new ColorSelectIcon(String.valueOf(c)));
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
        .displayName("§e§l색깔 닉네임 이용 안내")
        .lore("§f구입한 색깔을 클릭할 경우 닉네임이 해당 색깔로 변경됩니다.")
        .lore("§f커스텀 한글 닉네임과 혼용이 가능합니다.")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };

    for (i = 1; i <= 9; i++) {
      if (i == 5) continue;
      setIcon(i, 1, deco);
      setIcon(i, 5, deco);
    }
    setIcon(5, 5, deco);

    for (i = 2; i <= 4; i++) {
      setIcon(1, i, deco);
      setIcon(9, i, deco);
    }
    setIcon(5, 1, sign);


    updateAsynchronously();
  }

}
