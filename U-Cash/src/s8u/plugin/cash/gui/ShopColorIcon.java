package s8u.plugin.cash.gui;

import org.bukkit.inventory.ItemStack;
import s8u.plugin.cash.api.CashAPI;
import s8u.plugin.cash.api.data.ColorDisplayNameData;
import s8u.plugin.cash.api.sql.BenefitType;
import s8u.plugin.cash.api.sql.Type;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.FakeIcon;
import su.plugin.core.bukkit.api.util.ItemUtil;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

public class ShopColorIcon extends FakeIcon {

  private String colorCode;

  public ShopColorIcon(String colorCode) {
    this.colorCode = colorCode;

    setItem(getItemStack());
  }

  @Override
  protected ItemStack updateItem(UPlayer up) {
    ItemStack item = new ItemBuilder(getItemStack())
        .displayName(getColor() + "§l&" + colorCode + " 색깔 닉네임 (90일)")
        .lore("§c가격: §f5,000 §e캐시")
        .lore("")
        .lore("§e닉네임 미리보기: §f" + getColor() + ChatColor.stripColor(up.getDisplayName()))
        .lore("")
        .lore("§f클릭 시 " + getColor() + "&" + colorCode + " 색깔§f을 구매합니다.")
        .build();

    ColorDisplayNameData data = CashAPI.getColorDisplayName(up.getPlayerKey(), getColor());
    if (data != null) {
      item = new ItemBuilder(item)
          .lore("")
          .lore("§a보유 중인 " + getColor() + "&" + colorCode + " 색깔 §a만료일: §f" + data.getFormattedExpireTime())
          .build();
    }

    return item;
  }

  @Override
  public void onIconClick(IconClickEvent e) {
    PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());

    if (!CashAPI.subCash(playerKey, 5000)) {
      Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
      return;
    }
    CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, 5000);

    ColorDisplayNameData data = CashAPI.extendColorDisplayName(playerKey, getColor(), 90, 0, 0, 0);
    CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.COLOR_DISPLAY_NAME.color(getColor()), 5000, data.getExpireTime());

    Core.msg(e.getPlayer(), "§f색깔닉네임 " + getColor() + "색깔§f을 구입했습니다.");

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