package su.plugin.lobbysystem.listener;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.Icon;

public class NPCListener implements Listener {

  private GUI rankShopGUI;

  public NPCListener() {
    rankShopGUI = new GUI("U-LobbySystem/RankShop", "등급 상점", 1);

    Icon ironIcon = new Icon(new ItemBuilder(Material.LEATHER_CHESTPLATE)
        .displayName("§f아이언")
        .lore("§e 보호 IV 인챈트북 x4")
        .lore("")
        .lore("§c 가격 : 10,000원")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent event) {
        event.getPlayer().performCommand("등급구입 구매 iron");
      }
    };

    Icon bronzeIcon = new Icon(new ItemBuilder(Material.GOLD_CHESTPLATE)
        .displayName("§f브론즈")
        .lore("§e 보호 IV 인챈트북 x4")
        .lore("§e 날카로움 V 인챈트북 x1")
        .lore("")
        .lore("§c 가격 : 20,000원")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent event) {
        event.getPlayer().performCommand("등급구입 구매 bronze");
      }
    };

    Icon silverIcon = new Icon(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE)
        .displayName("§f실버")
        .lore("§e 보호 IV 인챈트북 x4")
        .lore("§e 날카로움 V 인챈트북 x1")
        .lore("§e 발화 인챈트북 x1")
        .lore("")
        .lore("§c 가격 : 30,000원")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent event) {
        event.getPlayer().performCommand("등급구입 구매 silver");
      }
    };

    Icon goldIcon = new Icon(new ItemBuilder(Material.IRON_CHESTPLATE)
        .displayName("§f골드")
        .lore("§e 보호 IV 인챈트북 x4")
        .lore("§e 날카로움 V 인챈트북 x1")
        .lore("§e 발화 인챈트북 x1")
        .lore("")
        .lore("§f 눈덩이 x16")
        .lore("§f 낚싯대 x1")
        .lore("")
        .lore("§c 가격 : 50,000원")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent event) {
        event.getPlayer().performCommand("등급구입 구매 gold");
      }
    };

    Icon platinumIcon = new Icon(new ItemBuilder(Material.DIAMOND_CHESTPLATE)
        .displayName("§f플래티넘")
        .lore("§e 보호 IV 인챈트북 x4")
        .lore("§e 날카로움 V 인챈트북 x1")
        .lore("§e 발화 인챈트북 x1")
        .lore("")
        .lore("§f 눈덩이 x32")
        .lore("§f 낚싯대 x1")
        .lore("§f 엔더진주 x1")
        .lore("")
        .lore("§c 가격 : 150,000원")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent event) {
        event.getPlayer().performCommand("등급구입 구매 platinum");
      }
    };

    Icon diamondIcon = new Icon(new ItemBuilder(Material.IRON_SWORD)
        .displayName("§f다이아몬드")
        .lore("§e 보호 IV 인챈트북 x4")
        .lore("§e 날카로움 V 인챈트북 x1")
        .lore("§e 발화 인챈트북 x1")
        .lore("")
        .lore("§f 눈덩이 x64")
        .lore("§f 낚싯대 x1")
        .lore("§f 엔더진주 x1")
        .lore("")
        .lore("§d 능력 재추첨권 x1 → 능력 재추첨권 x2")
        .lore("")
        .lore("§c 가격 : 350,000원")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent event) {
        event.getPlayer().performCommand("등급구입 구매 diamond");
      }
    };

    Icon masterIcon = new Icon(new ItemBuilder(Material.DIAMOND_SWORD)
        .displayName("§f마스터")
        .lore("§e 보호 IV 인챈트북 x4")
        .lore("§e 날카로움 V 인챈트북 x1")
        .lore("§e 발화 인챈트북 x1")
        .lore("")
        .lore("§f 눈덩이 x64")
        .lore("§f 낚싯대 x1")
        .lore("§f 엔더진주 x2")
        .lore("")
        .lore("§d 능력 재추첨권 x1 → 능력 재추첨권 x2")
        .lore("")
        .lore("§c 가격 : 500,000원")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent event) {
        event.getPlayer().performCommand("등급구입 구매 master");
      }
    };

    Icon grandMasterIcon = new Icon(new ItemBuilder(Material.BOW)
        .displayName("§f그랜드마스터")
        .lore("§e 보호 IV 인챈트북 x4")
        .lore("§e 날카로움 V 인챈트북 x1")
        .lore("§e 발화 인챈트북 x1")
        .lore("")
        .lore("§f 눈덩이 x64")
        .lore("§f 낚싯대 x1")
        .lore("§f 엔더진주 x2")
        .lore("§b 신속 II 10초 x1")
        .lore("")
        .lore("§d 능력 재추첨권 x1 → 능력 재추첨권 x2")
        .lore("")
        .lore("§c 가격 : 800,000원")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent event) {
        event.getPlayer().performCommand("등급구입 구매 grandmaster");
      }
    };

    Icon challengerIcon = new Icon(new ItemBuilder(Material.MAGMA_CREAM)
        .displayName("§f챌린저")
        .lore("§e 보호 IV 인챈트북 x4")
        .lore("§e 날카로움 V 인챈트북 x1")
        .lore("§e 발화 인챈트북 x1")
        .lore("")
        .lore("§f 눈덩이 x64")
        .lore("§f 낚싯대 x1")
        .lore("§f 엔더진주 x2")
        .lore("§b 신속 II 20초 x1")
        .lore("")
        .lore("§d 능력 재추첨권 x1 → 능력 재추첨권 x2")
        .lore("")
        .lore("§c 가격 : 1000,000원")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent event) {
        event.getPlayer().performCommand("등급구입 구매 challenger");
      }
    };

    rankShopGUI.setIcon(1, 1, ironIcon);
    rankShopGUI.setIcon(2, 1, bronzeIcon);
    rankShopGUI.setIcon(3, 1, silverIcon);
    rankShopGUI.setIcon(4, 1, goldIcon);
    rankShopGUI.setIcon(5, 1, platinumIcon);
    rankShopGUI.setIcon(6, 1, diamondIcon);
    rankShopGUI.setIcon(7, 1, masterIcon);
    rankShopGUI.setIcon(8, 1, grandMasterIcon);
    rankShopGUI.setIcon(9, 1, challengerIcon);

    rankShopGUI.updateAsynchronously();
  }

  @EventHandler
  public void onNPCClick(NPCRightClickEvent e) {
    if (!e.getNPC().getName().equals("등급상점")) return;

    rankShopGUI.open(e.getClicker());
  }

}