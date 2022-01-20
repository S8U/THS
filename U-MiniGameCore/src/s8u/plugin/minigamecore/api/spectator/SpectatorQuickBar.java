package s8u.plugin.minigamecore.api.spectator;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import s8u.plugin.minigamecore.api.MiniGameCore;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.enumeration.ClickAction;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.bukkit.api.gui.QuickBar;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class SpectatorQuickBar extends QuickBar {

  public SpectatorQuickBar() {
    // 텔레포터 아이콘
    Icon teleporterIcon = new Icon() {
      @Override
      protected ItemStack updateItem() {
        return new ItemBuilder(Material.COMPASS)
            .amount(MiniGameCore.getPlayerManager().getOnlinePlayingPlayers().size())
            .displayName("§e§l순간이동기 §f§l(우클릭)")
            .lore("§f우클릭 시 순간이동할 플레이어를 선택합니다.")
            .build();
      }
    };
    setIcon(1, teleporterIcon);

    // 속도 조절 아이콘

    // 로비 아이콘
    if (MiniGameCore.getBungeeManager().isUseBungeeCord() && MiniGameCore.getBungeeManager().isUseLobby()) {
      Icon lobbyIcon = new Icon() {
        @Override
        protected ItemStack updateItem() {
          return new ItemBuilder(Material.BED)
              .amount(1)
              .displayName("§e§l로비로 이동 §f§l(우클릭)")
              .lore("§f우클릭 시 로비로 이동합니다.")
              .build();
        }

        @Override
        public void onIconClick(IconClickEvent e) {
          if(e.getQuickBarClickEvent().getClickAction() == ClickAction.LEFT_CLICK) return;

          MiniGameCore.getBungeeManager().sendToLobby(e.getPlayer());

          Core.cmsg(e.getPlayer(), ChatColor.DARK_GREEN, "§e로비로 이동됩니다.");
        }
      };
      setIcon(9, lobbyIcon);
    }
  }

}