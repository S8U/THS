package s8u.plugin.cash.gui;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import s8u.plugin.cash.api.CashAPI;
import s8u.plugin.cash.api.data.MoneyBoostData;
import s8u.plugin.cash.api.sql.BenefitType;
import s8u.plugin.cash.api.sql.Type;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.FakeIcon;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.bukkit.api.lib.VaultHandler;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

public class ShopMainGUI extends GUI {

  public ShopMainGUI() {
    super("U-Cash/ShopMain", "캐시 상점", 3);

    FakeIcon money5000 = new FakeIcon(new ItemStack(Material.EMERALD)) {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        return new ItemBuilder(Material.EMERALD)
            .displayName("§a§l게임머니 5,000원")
            .lore("§c가격: §f5,000 §e캐시")
            .lore("")
            .lore("§f게임머니를 구매하여 빠르게 등급을 업그레이드 할 수 있습니다.")
            .lore("")
            .lore("§f클릭 시 §e게임머니 5,000원§f을 구입합니다.")
            .lore("")
            .lore("§e보유 중인 게임머니: §f" + new DecimalFormat("#,###").format(VaultHandler.getMoney(up.getName())) + " §e원")
            .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());

        if (!CashAPI.subCash(playerKey, 5000)) {
          Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
          return;
        }
        CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, 5000);

        VaultHandler.giveMoney(e.getPlayer(), 5000);
        CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.MONEY, 5000, LocalDateTime.now());

        Core.msg(e.getPlayer(), "§a게임머니 5,000원§f을 구입했습니다.");

        update(e.getPlayer());
      }
    };

    FakeIcon money10000 = new FakeIcon(new ItemStack(Material.EMERALD)) {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        return new ItemBuilder(Material.EMERALD)
            .displayName("§a§l게임머니 60,000원")
            .lore("§c가격: §f50,000 §e캐시")
            .lore("")
            .lore("§f게임머니를 구매하여 빠르게 등급을 업그레이드 할 수 있습니다.")
            .lore("§f보너스 게임머니 10,000원을 포함하고 있습니다.")
            .lore("")
            .lore("§f클릭 시 §e게임머니 60,000원§f을 구입합니다.")
            .lore("")
            .lore("§e보유 중인 게임머니: §f" + new DecimalFormat("#,###").format(VaultHandler.getMoney(up.getName())) + " §e원")
            .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());

        if (!CashAPI.subCash(playerKey, 50000)) {
          Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
          return;
        }
        CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, 50000);

        VaultHandler.giveMoney(e.getPlayer(), 60000);
        CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.MONEY, 50000, LocalDateTime.now());

        Core.msg(e.getPlayer(), "§a게임머니 60,000원§f을 구입했습니다.");

        update(e.getPlayer());
      }
    };

    FakeIcon moneyBoost3 = new FakeIcon(new ItemStack(Material.EXP_BOTTLE)) {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        ItemStack item = new ItemBuilder(Material.EXP_BOTTLE)
            .displayName("§a§l게임머니 부스트 (3일)")
            .lore("§c가격: §f5,000 §e캐시")
            .lore("")
            .lore("§f게임 플레이로 얻는 모든 게임머니를 2배로 증가시켜줍니다.")
            .lore("§f빠르게 돈을 모아 등급 업그레이드에 도전해보세요!")
            .lore("")
            .lore("§f클릭 시 §e게임머니 부스트 (3일)§f을 구입합니다.")
            .build();

        MoneyBoostData data = CashAPI.getMoneyBoost(up.getPlayerKey());
        if (data != null) {
          item = new ItemBuilder(item)
              .lore("")
              .lore("§a보유 중인 게임머니 부스트 만료일: §f" + data.getFormattedExpireTime())
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

        MoneyBoostData data = CashAPI.extendMoneyBoost(playerKey, 3, 0, 0, 0);
        CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.MONEY_BOOST, 5000, data.getExpireTime());

        Core.msg(e.getPlayer(), "§a게임머니 부스트 3일§f을 구입했습니다.");

        update(e.getPlayer());
      }
    };

    FakeIcon moneyBoost7 = new FakeIcon(new ItemStack(Material.EXP_BOTTLE)) {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        ItemStack item = new ItemBuilder(Material.EXP_BOTTLE)
            .displayName("§a§l게임머니 부스트 (7일)")
            .lore("§c가격: §f10,000 §e캐시")
            .lore("")
            .lore("§f게임 플레이로 얻는 모든 게임머니를 2배로 증가시켜줍니다.")
            .lore("§f빠르게 돈을 모아 등급 업그레이드에 도전해보세요!")
            .lore("")
            .lore("§f클릭 시 §e게임머니 부스트 (7일)§f을 구입합니다.")
            .build();

        MoneyBoostData data = CashAPI.getMoneyBoost(up.getPlayerKey());
        if (data != null) {
          item = new ItemBuilder(item)
              .lore("")
              .lore("§a보유 중인 게임머니 부스트 만료일: §f" + data.getFormattedExpireTime())
              .build();
        }

        return item;
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());

        if (!CashAPI.subCash(playerKey, 10000)) {
          Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
          return;
        }
        CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, 10000);

        MoneyBoostData data = CashAPI.extendMoneyBoost(playerKey, 7, 0, 0, 0);
        CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.MONEY_BOOST, 10000, data.getExpireTime());

        Core.msg(e.getPlayer(), "§a게임머니 부스트 7일§f을 구입했습니다.");

        update(e.getPlayer());
      }
    };

    Icon displayName = new Icon(new ItemBuilder(Material.NAME_TAG)
        .displayName("§b§l커스텀 한글닉네임 (90일)")
        .lore("§c가격: §f5,000 §e캐시")
        .lore("")
        .lore("§f한글과 영어 또는 숫자가 포함된 닉네임으로 변경할 수 있습니다.")
        .lore("§f커스텀 색깔닉네임과 함께 사용할 수 있습니다.")
        .lore("")
        .lore("§c§l주의!")
        .lore("§f서버 운영에 혼란을 초래할 수 있는 닉네임,")
        .lore("§f부적절한 표현이 포함된 닉네임 사용 시 처벌받을 수 있습니다.")
        .lore("")
        .lore("§f클릭 시 §b한글닉네임 구매 페이지§f로 이동합니다.")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        if (!CashAPI.hasCash(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()), 5000)) {
          Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
          return;
        }

        CashAPI.getDisplayNameGUI().open(e.getPlayer());
      }
    };

    Icon colorDisplayName = new Icon(new ItemBuilder(Material.NAME_TAG)
        .displayName("§b§l커스텀 §c§l색§6§l깔§e§l닉§a§l네§b§l임 (90일)")
        .lore("§c가격: §f5,000 §e캐시")
        .lore("")
        .lore("§f닉네임에 색깔을 입힐 수 있습니다.")
        .lore("§f커스텀 한글닉네임과 함께 사용할 수 있습니다.")
        .lore("")
        .lore("§f클릭 시 §e색깔 선택 페이지§f로 이동합니다.")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        CashAPI.getShopColorGUI().open(e.getPlayer());
      }
    };

    Icon donation = new Icon(new ItemBuilder(Material.BREAD)
        .displayName("§c§l개발자에게 후원하기")
        .lore("§f서버를 위해 노력하는 개발진에게 후원합니다.")
        .lore("")
        .lore("§f클릭 시 §e후원 금액 선택 페이지§f로 이동합니다.")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        CashAPI.getShopDonationGUI().open(e.getPlayer());
      }
    };

    setIcon(2, 2, money5000);
    setIcon(3, 2, money10000);
    setIcon(4, 2, moneyBoost3);
    setIcon(5, 2, moneyBoost7);
    setIcon(6, 2, displayName);
    setIcon(7, 2, colorDisplayName);
    setIcon(8, 2, donation);

    //

    Icon deco = new Icon(new ItemBuilder("160:14")
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
        .lore("§f클릭 시 §e캐시 충전 사이트 링크§f를 확인합니다.")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        Core.nmsg(e.getPlayer(), "");
        Core.nmsg(e.getPlayer(), "§e캐시 약관 안내 링크 >> §fhttps://cafe.naver.com/minecraftleafserver/54344 §e[클릭]");
        Core.nmsg(e.getPlayer(), "§e캐시 상품 안내 링크 >> §fhttps://cafe.naver.com/minecraftleafserver/54345 §e[클릭]");
        Core.nmsg(e.getPlayer(), "");
        Core.nmsg(e.getPlayer(), "§e캐시 충전 사이트 링크 >> §fhttps://skhcs.com/leafserver §e[클릭]");
        Core.nmsg(e.getPlayer(), "");
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

}