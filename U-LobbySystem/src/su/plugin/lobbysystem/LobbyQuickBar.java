package su.plugin.lobbysystem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.enumeration.ClickAction;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.bukkit.api.gui.QuickBar;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class LobbyQuickBar extends QuickBar {

  private Player player;

  public LobbyQuickBar(Player player) {
    this.player = player;

    Icon channelSelectIcon = new Icon(new ItemBuilder(Material.COMPASS)
        .displayName("§e§l채널 선택")
        .lore("§b우클릭 시 채널을 선택합니다.")
        .build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }

      @Override
      public void onIconClick(IconClickEvent event) {
        if (event.getQuickBarClickEvent().getClickAction() != ClickAction.RIGHT_CLICK) return;

        event.getPlayer().performCommand("cg open all_channel");
      }
    };

    Icon toggleHideIcon = new Icon() {
      @Override
      protected ItemStack updateItem() {
        return Core.getOptionManager().existsPlayerOption(PlayerKey.getPlayerKeyByPlatformPlayer(player), "lobby_hide_player") ?
            new ItemBuilder("351:10").displayName("§f현재 상태: §a보기").lore("우클릭 시 플레이어를 가립니다.").build()
            : new ItemBuilder("351:8").displayName("§f현재 상태: §7가리기").lore("우클릭 시 플레이어를 보도록 설정합니다.").build();
      }

      @Override
      public void onIconClick(IconClickEvent event) {
        if (Core.getOptionManager().existsPlayerOption(PlayerKey.getPlayerKeyByPlatformPlayer(player), "lobby_hide_player")) {
          Core.getOptionManager().deletePlayerOption(PlayerKey.getPlayerKeyByPlatformPlayer(player), "lobby_hide_player");
          Core.getOptionSQLManager().deletePlayerOption(PlayerKey.getPlayerKeyByPlatformPlayer(player), "lobby_hide_player");

          KCore.getOnlinePlayers().forEach(ap -> event.getPlayer().showPlayer(ap));

          Core.msg(event.getPlayer(), "플레이어를 보도록 설정했습니다.");
        } else {
          Core.getOptionManager().setPlayerOption(PlayerKey.getPlayerKeyByPlatformPlayer(player), "lobby_hide_player", 1);
          Core.getOptionSQLManager().setPlayerOption(PlayerKey.getPlayerKeyByPlatformPlayer(player), "lobby_hide_player", 1);

          KCore.getOnlinePlayers().forEach(ap -> event.getPlayer().hidePlayer(ap));

          Core.msg(event.getPlayer(), "플레이어를 가렸습니다.");
        }
      }
    };

    setIcon(1, channelSelectIcon);
    setIcon(9, toggleHideIcon);

    update();
  }

}
