package s8u.plugin.optiongui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.FakeIcon;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

public class OptionGUI extends GUI {

  public OptionGUI() {
    super("U-OptionGUI", "옵션", 2);

    Icon chatIcon = new Icon(new ItemBuilder(Material.SIGN).displayName("§f전체 채팅").build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };
    FakeIcon chatOptionIcon = new FakeIcon() {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        Object option = Core.getOptionManager().getPlayerOption(up.getPlayerKey(), "gessentials_chat_ignore_all");

        return option == null ?
            new ItemBuilder(Material.QUARTZ_BLOCK)
                .displayName("§b전체 채팅: 켜짐")
                .lore("§f클릭 시 전체 채팅을 §c보지 않도록 §f변경합니다.")
                .build() :
            new ItemBuilder(Material.BARRIER)
                .displayName("§c전체 채팅: 꺼짐")
                .lore("§f클릭 시 전체 채팅을 §b보도록 §f변경합니다.")
                .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());
        Object option = Core.getOptionManager().getPlayerOption(playerKey, "gessentials_chat_ignore_all");

        if (option == null) {
          Core.getOptionManager().setPlayerOption(playerKey, "gessentials_chat_ignore_all", true);
          Core.getOptionSQLManager().setPlayerOption(playerKey, "gessentials_chat_ignore_all", true);
        } else {
          Core.getOptionManager().deletePlayerOption(playerKey, "gessentials_chat_ignore_all");
          Core.getOptionSQLManager().deletePlayerOption(playerKey, "gessentials_chat_ignore_all");
        }

        update(e.getPlayer());
      }
    };

    Icon whisperIcon = new Icon(new ItemBuilder(Material.NAME_TAG).displayName("§f귓속말").build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };
    FakeIcon whisperOptionIcon = new FakeIcon() {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        Object option = Core.getOptionManager().getPlayerOption(up.getPlayerKey(), "gessentials_allow_whisper");

        return option == null ?
            new ItemBuilder(Material.QUARTZ_BLOCK)
                .displayName("§b귓속말: 모두 허용")
                .lore("§f클릭 시 귓속말을 §a친구만 허용§f합니다.")
                .build() :
            option.equals("friend") ?
                new ItemBuilder(Material.STAINED_CLAY).durability((short) 5)
                    .displayName("§a귓속말: 친구만 허용")
                    .lore("§f클릭 시 귓속말을 §c모두 차단§f합니다.")
                    .build() :
                new ItemBuilder(Material.BARRIER)
                    .displayName("§c귓속말: 모두 차단")
                    .lore("§f클릭 시 귓속말을 §b모두 허용§f합니다.")
                    .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());
        Object option = Core.getOptionManager().getPlayerOption(playerKey, "gessentials_allow_whisper");

        if (option == null) {
          Core.getOptionManager().setPlayerOption(playerKey, "gessentials_allow_whisper", "friend");
          Core.getOptionSQLManager().setPlayerOption(playerKey, "gessentials_allow_whisper", "friend");
        } else if (option.equals("friend")) {
          Core.getOptionManager().setPlayerOption(playerKey, "gessentials_allow_whisper", "block");
          Core.getOptionSQLManager().setPlayerOption(playerKey, "gessentials_allow_whisper", "block");
        } else {
          Core.getOptionManager().deletePlayerOption(playerKey, "gessentials_allow_whisper");
          Core.getOptionSQLManager().deletePlayerOption(playerKey, "gessentials_allow_whisper");
        }

        update(e.getPlayer());
      }
    };

    Icon friendRequestIcon = new Icon(new ItemBuilder(Material.SKULL_ITEM).durability((short) 3).displayName("§f친구 요청").build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };
    FakeIcon friendRequestOptionIcon = new FakeIcon() {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        Object option = Core.getOptionManager().getPlayerOption(up.getPlayerKey(), "gfriend_allow_request");

        return option == null ?
            new ItemBuilder(Material.QUARTZ_BLOCK)
                .displayName("§b친구 요청: 허용")
                .lore("§f클릭 시 친구 요청을 §c받지 않도록 §f변경합니다.")
                .build() :
            new ItemBuilder(Material.BARRIER)
                .displayName("§c친구 요청: 차단")
                .lore("§f클릭 시 친구 요청을 §b받도록 §f변경합니다.")
                .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());
        Object option = Core.getOptionManager().getPlayerOption(playerKey, "gfriend_allow_request");

        if (option == null) {
          Core.getOptionManager().setPlayerOption(playerKey, "gfriend_allow_request", "block");
          Core.getOptionSQLManager().setPlayerOption(playerKey, "gfriend_allow_request", "block");
        } else {
          Core.getOptionManager().deletePlayerOption(playerKey, "gfriend_allow_request");
          Core.getOptionSQLManager().deletePlayerOption(playerKey, "gfriend_allow_request");
        }

        update(e.getPlayer());
      }
    };

    Icon partyIcon = new Icon(new ItemBuilder(Material.FIREWORK).displayName("§f파티 초대").build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };
    FakeIcon partyOptionIcon = new FakeIcon() {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        Object option = Core.getOptionManager().getPlayerOption(up.getPlayerKey(), "gparty_allow_invite");

        return option == null ?
            new ItemBuilder(Material.QUARTZ_BLOCK)
                .displayName("§b파티 초대: 모두 허용")
                .lore("§f클릭 시 파티 초대를 §a친구만 허용§f합니다.")
                .build() :
            option.equals("friend") ?
                new ItemBuilder(Material.STAINED_CLAY).durability((short) 5)
                    .displayName("§a파티 초대: 친구만 허용")
                    .lore("§f클릭 시 파티 초대를 §c모두 차단§f합니다.")
                    .build() :
                new ItemBuilder(Material.BARRIER)
                    .displayName("§c파티 초대: 모두 차단")
                    .lore("§f클릭 시 파티 초대를 §b모두 허용§f합니다.")
                    .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());
        Object option = Core.getOptionManager().getPlayerOption(playerKey, "gparty_allow_invite");

        if (option == null) {
          Core.getOptionManager().setPlayerOption(playerKey, "gparty_allow_invite", "friend");
          Core.getOptionSQLManager().setPlayerOption(playerKey, "gparty_allow_invite", "friend");
        } else if (option.equals("friend")) {
          Core.getOptionManager().setPlayerOption(playerKey, "gparty_allow_invite", "block");
          Core.getOptionSQLManager().setPlayerOption(playerKey, "gparty_allow_invite", "block");
        } else {
          Core.getOptionManager().deletePlayerOption(playerKey, "gparty_allow_invite");
          Core.getOptionSQLManager().deletePlayerOption(playerKey, "gparty_allow_invite");
        }

        update(e.getPlayer());
      }
    };

    Icon broadcastIcon = new Icon(new ItemBuilder(Material.BOOK).displayName("§f전체 공지").build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };
    FakeIcon broadcastOptionIcon = new FakeIcon() {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        Object option = Core.getOptionManager().getPlayerOption(up.getPlayerKey(), "gbroadcaster_hide");

        return option == null ?
            new ItemBuilder(Material.QUARTZ_BLOCK)
                .displayName("§b전체 공지: 보기")
                .lore("§f클릭 시 전체 공지를 §c보지 않도록 §f변경합니다.")
                .build() :
            new ItemBuilder(Material.BARRIER)
                .displayName("§c전체 공지: 가리기")
                .lore("§f클릭 시 전체 공지를 §b보도록 §f변경합니다.")
                .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer());
        Object option = Core.getOptionManager().getPlayerOption(playerKey, "gbroadcaster_hide");

        if (option == null) {
          Core.getOptionManager().setPlayerOption(playerKey, "gbroadcaster_hide", true);
          Core.getOptionSQLManager().setPlayerOption(playerKey, "gbroadcaster_hide", true);
        } else {
          Core.getOptionManager().deletePlayerOption(playerKey, "gbroadcaster_hide");
          Core.getOptionSQLManager().deletePlayerOption(playerKey, "gbroadcaster_hide");
        }

        update(e.getPlayer());
      }
    };

    setIcon(3, 1, chatIcon);
    setIcon(3, 2, chatOptionIcon);

    setIcon(4, 1, whisperIcon);
    setIcon(4, 2, whisperOptionIcon);

    setIcon(5, 1, friendRequestIcon);
    setIcon(5, 2, friendRequestOptionIcon);

    setIcon(6, 1, partyIcon);
    setIcon(6, 2, partyOptionIcon);

    setIcon(7, 1, broadcastIcon);
    setIcon(7, 2, broadcastOptionIcon);

    updateAsynchronously();
  }

}