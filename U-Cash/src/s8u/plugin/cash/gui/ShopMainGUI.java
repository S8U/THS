package s8u.plugin.cash.gui;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import s8u.plugin.cash.api.CashAPI;
import s8u.plugin.cash.api.sql.BenefitType;
import s8u.plugin.cash.api.sql.Type;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.FakeIcon;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.prefixer.api.PrefixerAPI;
import su.plugin.prefixer.api.object.PrefixPlayer;

public class ShopMainGUI extends GUI {

  private static final String VIP_PREFIX = "§d[VIP]";
  private static final String VIP_PLUS_PREFIX = "§d[VIP+]";
  private static final String VVIP_PREFIX = "§4[VVIP]";
  private static final String VVIP_PLUS_PREFIX = "§4[VVIP+]";
  private static final String LEAF_PREFIX = "§a[Leaf]";


  public ShopMainGUI() {
    super("U-Cash/ShopMain", "캐시 상점", 3);

    FakeIcon vipIcon = new FakeIcon(new ItemStack(Material.EMERALD)) {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        return new ItemBuilder(Material.EMERALD)
            .displayName(VIP_PREFIX + " §f칭호")
            .lore("§c가격: §f10,000 §e캐시")
            .lore("")
            .lore("§f게임머니 획득량이 1.1배로 증가합니다.")
            .lore(VIP_PREFIX + " §f칭호를 포함하고 있습니다.")
            .lore("")
            .lore("§f클릭 시 " + VIP_PREFIX + " §f칭호를 구입합니다.")
            .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());

        PrefixPlayer prefixPlayer = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
        if (prefixPlayer.hasPrefix(VIP_PREFIX)) {
          Core.wmsg(e.getPlayer(), "이미 구매한 칭호입니다.");
          return;
        } else if (prefixPlayer.hasPrefix(VIP_PLUS_PREFIX)
            || prefixPlayer.hasPrefix(VVIP_PREFIX)
            || prefixPlayer.hasPrefix(VVIP_PLUS_PREFIX)
            || prefixPlayer.hasPrefix(LEAF_PREFIX)) {
          Core.wmsg(e.getPlayer(), "이미 더 좋은 혜택의 칭호를 가지고 있습니다.");
          return;
        }

        if (!CashAPI.subCash(playerKey, 10000)) {
          Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
          return;
        }

        CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, 10000);
        CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.VIP_PREFIX, 10000, LocalDateTime.now());

        PrefixerAPI.addPrefix(playerKey, VIP_PREFIX);

        Core.msg(e.getPlayer(), VIP_PREFIX + " §f칭호를 구입했습니다.");
        Core.msg(e.getPlayer(), "'/칭호 목록' 명령어를 사용하고 원하는 칭호를 클릭하여 칭호를 착용하거나 해제할 수 있습니다.");

        update(e.getPlayer());
      }
    };

    FakeIcon vipPlusIcon = new FakeIcon(new ItemStack(Material.EMERALD)) {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        ItemBuilder builder = new ItemBuilder(Material.EMERALD)
            .displayName(VIP_PLUS_PREFIX + " §f칭호");

        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(up.getPlatformSender());

        PrefixPlayer prefixPlayer = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
        if (prefixPlayer.hasPrefix(VIP_PREFIX)) {
          builder.lore("§c가격: §f40,000 §e캐시");
        } else {
          builder.lore("§c가격: §f50,000 §e캐시");
        }

        return builder.lore("")
            .lore("§f게임머니 획득량이 1.2배로 증가합니다.")
            .lore(VIP_PLUS_PREFIX + " §f칭호를 포함하고 있습니다.")
            .lore("")
            .lore("§f클릭 시 " + VIP_PLUS_PREFIX + " §f칭호를 구입합니다.")
            .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());

        PrefixPlayer prefixPlayer = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
        if (prefixPlayer.hasPrefix(VIP_PLUS_PREFIX)) {
          Core.wmsg(e.getPlayer(), "이미 구매한 칭호입니다.");
          return;
        } else if (prefixPlayer.hasPrefix(VVIP_PREFIX)
            || prefixPlayer.hasPrefix(VVIP_PLUS_PREFIX)
            || prefixPlayer.hasPrefix(LEAF_PREFIX)) {
          Core.wmsg(e.getPlayer(), "이미 더 좋은 혜택의 칭호를 가지고 있습니다.");
          return;
        }

        int price = 50000;

        if (prefixPlayer.hasPrefix(VIP_PREFIX)) {
          price -= 10000;
        }

        if (!CashAPI.subCash(playerKey, price)) {
          Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
          return;
        }

        CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, price);
        CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.VIP_PLUS_PREFIX, price, LocalDateTime.now());

        PrefixerAPI.addPrefix(playerKey, VIP_PLUS_PREFIX);

        Core.msg(e.getPlayer(), VIP_PLUS_PREFIX + " §f칭호를 구입했습니다.");
        Core.msg(e.getPlayer(), "'/칭호 목록' 명령어를 사용하고 원하는 칭호를 클릭하여 칭호를 착용하거나 해제할 수 있습니다.");

        update(e.getPlayer());
      }
    };

    FakeIcon vvipIcon = new FakeIcon(new ItemStack(Material.EXP_BOTTLE)) {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        ItemBuilder builder = new ItemBuilder(Material.EMERALD)
            .displayName(VVIP_PREFIX + " §f칭호");

        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(up.getPlatformSender());

        PrefixPlayer prefixPlayer = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
        if (prefixPlayer.hasPrefix(VIP_PLUS_PREFIX)) {
          builder.lore("§c가격: §f50,000 §e캐시");
        } else if (prefixPlayer.hasPrefix(VIP_PREFIX)) {
          builder.lore("§c가격: §f90,000 §e캐시");
        } else {
          builder.lore("§c가격: §f100,000 §e캐시");
        }

        return builder.lore("")
            .lore("§f게임머니 획득량이 1.3배로 증가합니다.")
            .lore(VVIP_PREFIX + " §f칭호를 포함하고 있습니다.")
            .lore("")
            .lore("§f클릭 시 " + VVIP_PREFIX + " §f칭호를 구입합니다.")
            .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());

        PrefixPlayer prefixPlayer = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
        if (prefixPlayer.hasPrefix(VVIP_PREFIX)) {
          Core.wmsg(e.getPlayer(), "이미 구매한 칭호입니다.");
          return;
        } else if (prefixPlayer.hasPrefix(VVIP_PLUS_PREFIX)
            || prefixPlayer.hasPrefix(LEAF_PREFIX)) {
          Core.wmsg(e.getPlayer(), "이미 더 좋은 혜택의 칭호를 가지고 있습니다.");
          return;
        }

        int price = 100000;

        if (prefixPlayer.hasPrefix(VIP_PLUS_PREFIX)) {
          price -= 50000;
        } else if (prefixPlayer.hasPrefix(VIP_PREFIX)) {
          price -= 10000;
        }

        if (!CashAPI.subCash(playerKey, price)) {
          Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
          return;
        }

        CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, price);
        CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.VIP_PLUS_PREFIX, price, LocalDateTime.now());

        PrefixerAPI.addPrefix(playerKey, VVIP_PREFIX);

        Core.msg(e.getPlayer(), VVIP_PREFIX + " §f칭호를 구입했습니다.");
        Core.msg(e.getPlayer(), "'/칭호 목록' 명령어를 사용하고 원하는 칭호를 클릭하여 칭호를 착용하거나 해제할 수 있습니다.");

        update(e.getPlayer());
      }
    };

    FakeIcon vvipPlusIcon = new FakeIcon(new ItemStack(Material.EXP_BOTTLE)) {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        ItemBuilder builder = new ItemBuilder(Material.EMERALD)
            .displayName(VVIP_PLUS_PREFIX + " §f칭호");

        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(up.getPlatformSender());

        PrefixPlayer prefixPlayer = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
        if (prefixPlayer.hasPrefix(VVIP_PREFIX)) {
          builder.lore("§c가격: §f100,000 §e캐시");
        } else if (prefixPlayer.hasPrefix(VIP_PLUS_PREFIX)) {
          builder.lore("§c가격: §f150,000 §e캐시");
        } else if (prefixPlayer.hasPrefix(VIP_PREFIX)) {
          builder.lore("§c가격: §f190,000 §e캐시");
        } else {
          builder.lore("§c가격: §f200,000 §e캐시");
        }

        return builder.lore("")
            .lore("§f게임머니 획득량이 1.4배로 증가합니다.")
            .lore(VVIP_PLUS_PREFIX + " §f칭호를 포함하고 있습니다.")
            .lore("")
            .lore("§f클릭 시 " + VVIP_PLUS_PREFIX + " §f칭호를 구입합니다.")
            .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());

        PrefixPlayer prefixPlayer = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
        if (prefixPlayer.hasPrefix(VVIP_PLUS_PREFIX)) {
          Core.wmsg(e.getPlayer(), "이미 구매한 칭호입니다.");
          return;
        } else if (prefixPlayer.hasPrefix(VVIP_PLUS_PREFIX)
            || prefixPlayer.hasPrefix(LEAF_PREFIX)) {
          Core.wmsg(e.getPlayer(), "이미 더 좋은 혜택의 칭호를 가지고 있습니다.");
          return;
        }

        int price = 200000;

        if (prefixPlayer.hasPrefix(VVIP_PREFIX)) {
          price -= 100000;
        } else if (prefixPlayer.hasPrefix(VIP_PLUS_PREFIX)) {
          price -= 50000;
        } else if (prefixPlayer.hasPrefix(VIP_PREFIX)) {
          price -= 10000;
        }

        if (!CashAPI.subCash(playerKey, price)) {
          Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
          return;
        }

        CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, price);
        CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.VIP_PLUS_PREFIX, price, LocalDateTime.now());

        PrefixerAPI.addPrefix(playerKey, VVIP_PLUS_PREFIX);

        Core.msg(e.getPlayer(), VVIP_PLUS_PREFIX + " §f칭호를 구입했습니다.");
        Core.msg(e.getPlayer(), "'/칭호 목록' 명령어를 사용하고 원하는 칭호를 클릭하여 칭호를 착용하거나 해제할 수 있습니다.");

        update(e.getPlayer());
      }
    };

    FakeIcon leafIcon = new FakeIcon(new ItemStack(Material.EXP_BOTTLE)) {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        ItemBuilder builder = new ItemBuilder(Material.EMERALD)
            .displayName(LEAF_PREFIX + " §f칭호");

        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(up.getPlatformSender());

        PrefixPlayer prefixPlayer = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
        if (prefixPlayer.hasPrefix(VVIP_PLUS_PREFIX)) {
          builder.lore("§c가격: §f100,000 §e캐시");
        } else if (prefixPlayer.hasPrefix(VVIP_PREFIX)) {
          builder.lore("§c가격: §f200,000 §e캐시");
        } else if (prefixPlayer.hasPrefix(VIP_PLUS_PREFIX)) {
          builder.lore("§c가격: §f250,000 §e캐시");
        } else if (prefixPlayer.hasPrefix(VIP_PREFIX)) {
          builder.lore("§c가격: §f290,000 §e캐시");
        } else {
          builder.lore("§c가격: §f300,000 §e캐시");
        }

        return builder.lore("")
            .lore("§f게임머니 획득량이 1.5배로 증가합니다.")
            .lore(LEAF_PREFIX + " §f칭호를 포함하고 있습니다.")
            .lore("")
            .lore("§f클릭 시 " + LEAF_PREFIX + " §f칭호를 구입합니다.")
            .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());

        PrefixPlayer prefixPlayer = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
        if (prefixPlayer.hasPrefix(LEAF_PREFIX)) {
          Core.wmsg(e.getPlayer(), "이미 구매한 칭호입니다.");
          return;
        }

        int price = 300000;

        if (prefixPlayer.hasPrefix(VVIP_PLUS_PREFIX)) {
          price -= 200000;
        } else if (prefixPlayer.hasPrefix(VVIP_PREFIX)) {
          price -= 100000;
        } else if (prefixPlayer.hasPrefix(VIP_PLUS_PREFIX)) {
          price -= 50000;
        } else if (prefixPlayer.hasPrefix(VIP_PREFIX)) {
          price -= 10000;
        }

        if (!CashAPI.subCash(playerKey, price)) {
          Core.wmsg(e.getPlayer(), "캐시 잔액이 부족합니다.");
          return;
        }

        CashAPI.getSQLManager().logCash(playerKey.getId(), -2, Type.SUBTRACT, price);
        CashAPI.getSQLManager().logBenefit(playerKey.getId(), -2, BenefitType.VIP_PLUS_PREFIX, price, LocalDateTime.now());

        PrefixerAPI.addPrefix(playerKey, LEAF_PREFIX);

        Core.msg(e.getPlayer(), LEAF_PREFIX + " §f칭호를 구입했습니다.");
        Core.msg(e.getPlayer(), "'/칭호 목록' 명령어를 사용하고 원하는 칭호를 클릭하여 칭호를 착용하거나 해제할 수 있습니다.");

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

    setIcon(2, 2, vipIcon);
    setIcon(3, 2, vipPlusIcon);
    setIcon(4, 2, vvipIcon);
    setIcon(5, 2, vvipPlusIcon);
    setIcon(6, 2, leafIcon);
    setIcon(7, 2, displayName);
    setIcon(8, 2, colorDisplayName);
//    setIcon(8, 2, donation);

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