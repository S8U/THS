package s8u.plugin.cash.gui;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import s8u.plugin.cash.api.CashAPI;
import s8u.plugin.cash.api.sql.BenefitType;
import s8u.plugin.cash.api.sql.Type;
import s8u.plugin.cash.lib.prefixer.PrefixerHandler;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.FakeIcon;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

public class ShopDonationGUI extends GUI {

  public ShopDonationGUI() {
    super("U-Cash/ShopDonation", "개발자 후원", 3);

    Icon ramen = new Icon(new ItemBuilder(Material.MUSHROOM_SOUP)
        .displayName("§e§l개발자에게 라면 한 개 사주기")
        .lore("§c가격: §f1,000 §e캐시")
        .lore("")
        .lore("§f서버 개발자에게 기부합니다.")
        .lore("§f이 상품으로 발생하는 수익은 개발진에게 돌아감으로써")
        .lore("§f개발진에게 서버 발전을 위해 힘을 실어줄 수 있습니다.")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());
        if (!CashAPI.subCash(playerKey, 1000)) {
          Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
          return;
        }
        CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, 1000);

        handleDonate(e.getPlayer(), playerKey);

        CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.DONATION, 1000, LocalDateTime.now());
      }
    };

    Icon coffee = new Icon(new ItemBuilder(Material.POTION)
        .displayName("§e§l개발자에게 커피 한 잔 사주기")
        .lore("§c가격: §f2,500 §e캐시")
        .lore("")
        .lore("§f서버 개발자에게 기부합니다.")
        .lore("§f이 상품으로 발생하는 수익은 개발진에게 돌아감으로써")
        .lore("§f개발진에게 서버 발전을 위해 힘을 실어줄 수 있습니다.")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());
        if (!CashAPI.subCash(playerKey, 2500)) {
          Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
          return;
        }
        CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, 2500);

        handleDonate(e.getPlayer(), playerKey);

        CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.DONATION, 2500, LocalDateTime.now());
      }
    };

    Icon meal = new Icon(new ItemBuilder(Material.COOKED_BEEF)
        .displayName("§e§l개발자에게 든든한 한 끼 사주기")
        .lore("§c가격: §f5,000 §e캐시")
        .lore("")
        .lore("§f서버 개발자에게 기부합니다.")
        .lore("§f이 상품으로 발생하는 수익은 개발진에게 돌아감으로써")
        .lore("§f개발진에게 서버 발전을 위해 힘을 실어줄 수 있습니다.")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());
        if (!CashAPI.subCash(playerKey, 5000)) {
          Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
          return;
        }
        CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, 5000);

        handleDonate(e.getPlayer(), playerKey);

        CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.DONATION, 5000, LocalDateTime.now());
      }
    };

    setIcon(3, 2, ramen);
    setIcon(5, 2, coffee);
    setIcon(7, 2, meal);

    //

    Icon deco = new Icon(new ItemBuilder("160:2")
        .displayName("")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };

    Icon sign = new Icon(new ItemBuilder(Material.SIGN)
        .displayName("§e§l캐시 상점 이용 안내")
        .lore("§f사이트를 통해 충전한 캐시로 사용할 수 있습니다.")
        .lore("")
        .lore("§f클릭 시 §e후원 사이트 링크§f를 확인합니다.")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
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

    for (int i = 1; i <= 9; i++) {
      if (i == 5) continue;
      setIcon(i, 1, deco);
      setIcon(i, 3, deco);
    }
    setIcon(1, 2, deco);
    setIcon(9, 2, deco);
    setIcon(5, 1, sign);
    setIcon(5, 3, currentCash);

    updateAsynchronously();
  }

  private void handleDonate(Player player, PlayerKey playerKey) {
    Core.nmsg(player, "");
    Core.nmsg(player, "§e§l개발자에게 기부해주셔서 감사합니다.");
    Core.nmsg(player, "§e§l개발자가 당신에게 고마워할겁니다! 서버 발전을 위해 더욱 힘쓰겠습니다.");
    Core.nmsg(player, "");

    String donatorPrefix = PrefixerHandler.giveDonatorPrefix(playerKey);
    if (donatorPrefix != null) {
      Core.nmsg(player, "");
      Core.nmsg(player, "§e§l<< EASTER EGG >>");
      Core.nmsg(player, donatorPrefix + " §f칭호를 획득했습니다.");
      Core.nmsg(player, "");
    }
  }

}