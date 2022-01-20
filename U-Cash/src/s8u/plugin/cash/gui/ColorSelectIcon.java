package s8u.plugin.cash.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import s8u.plugin.cash.api.CashAPI;
import s8u.plugin.cash.api.data.ColorDisplayNameData;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.FakeIcon;
import su.plugin.core.bukkit.api.util.ItemUtil;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.UPlayer;

public class ColorSelectIcon extends FakeIcon {

  private String colorCode;

  public ColorSelectIcon(String colorCode) {
    this.colorCode = colorCode;

    setItem(getItemStack());
  }

  @Override
  protected ItemStack updateItem(UPlayer up) {
    ColorDisplayNameData data = CashAPI.getColorDisplayName(up.getPlayerKey(), getColor());
    if (data == null && !colorCode.equals("f")) {
      return new ItemBuilder(Material.STONE)
          .displayName(getColor() + "§l&" + colorCode + " 색깔 닉네임")
          .lore("§e닉네임 미리보기: §f" + getColor() + ChatColor.stripColor(up.getDisplayName()))
          .lore("")
          .lore("§c구매하지 않은 색깔입니다.")
          .lore("§c상점에서 색깔을 구매하세요!")
          .build();
    } else {
      ItemStack item = new ItemBuilder(getItemStack())
          .displayName(getColor() + "§l&" + colorCode + " 색깔 닉네임")
          .lore("§e닉네임 미리보기: §f" + getColor() + ChatColor.stripColor(up.getDisplayName()))
          .lore("")
          .lore("§f클릭 시 " + getColor() + "색깔§f을 착용합니다.")
          .build();

      if (!colorCode.equals("f")) {
        item = new ItemBuilder(item)
            .lore("")
            .lore(getColor() + "&" + colorCode + " 색깔 §a만료일: §f" + data.getFormattedExpireTime())
            .build();
      }

      return item;
    }
  }

  @Override
  public void onIconClick(IconClickEvent e) {
    UPlayer up = Core.getUPlayerByPlatformPlayer(e.getPlayer());
    if (!colorCode.equals("f") && !CashAPI.hasColorDisplayName(up.getPlayerKey(), getColor())) {
      up.wmsg("구매하지 않은 색깔입니다.");
      return;
    }

    if (colorCode.equals("f")) {
      up.setDisplayName(ChatColor.stripColor(up.getDisplayName()));
    } else {
      up.setDisplayName(getColor() + ChatColor.stripColor(up.getDisplayName()));
    }

    Core.msg(e.getPlayer(), "§f닉네임을 " + up.getDisplayName() + "§f로 변경했습니다.");

    update(e.getPlayer());
  }

  public ChatColor getColor() {
    return ChatColor.getByChar(colorCode);
  }

  private ItemStack getItemStack() {
    switch (colorCode) {
      case "0": return ItemUtil.getItem("35:15");
      case "1": return ItemUtil.getItem("35:11");
      case "2": return ItemUtil.getItem("35:13");
      case "3": return ItemUtil.getItem("35:9");
      case "4": return ItemUtil.getItem("35:14");
      case "5": return ItemUtil.getItem("35:2");
      case "6": return ItemUtil.getItem("35:1");
      case "7": return ItemUtil.getItem("35:8");
      case "8": return ItemUtil.getItem("35:7");
      case "9": return ItemUtil.getItem("159:3");
      case "a": return ItemUtil.getItem("35:5");
      case "b": return ItemUtil.getItem("35:3");
      case "c": return ItemUtil.getItem("159:14");
      case "d": return ItemUtil.getItem("35:6");
      case "e": return ItemUtil.getItem("35:4");
      case "f": return ItemUtil.getItem("35:0");
    }

    return ItemUtil.getItemById(1);
  }

}