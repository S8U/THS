package s8u.plugin.minigamecore.api.wait;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import s8u.plugin.minigamecore.api.MiniGameCore;
import s8u.plugin.minigamecore.api.config.AutoGameConfig;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.enumeration.ClickAction;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.bukkit.api.gui.QuickBar;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class WaitQuickBar extends QuickBar {

  public WaitQuickBar() {
    // 시작 투표
    if(AutoGameConfig.isUseStartVote()) {
      Icon startVoteIcon = new Icon() {
        @Override
        protected ItemStack updateItem() {
          return new ItemBuilder(Material.PAPER)
              .amount(MiniGameCore.getStartVoteManager().getNumberOfVotes())
              .displayName("§e§l시작 투표")
              .lore(MiniGameCore.getStartVoteManager().isVoting() ? "§f우클릭 시 시작 투표 GUI가 열립니다." : "§f우클릭 시 시작 투표를 시작합니다.")
              .build();
        }

        @Override
        public void onIconClick(IconClickEvent e) {
          if (e.getQuickBarClickEvent().getClickAction() == ClickAction.LEFT_CLICK) return;

          MiniGameCore.getStartVoteManager().startVote(Core.getUPlayerByPlatformPlayer(e.getPlayer()));
        }
      };

      setIcon(1, startVoteIcon);
    }

    // 맵 투표
    if(AutoGameConfig.isUseMapVote()) {
      Icon mapVoteIcon = new Icon() {
        @Override
        protected ItemStack updateItem() {
          return new ItemBuilder(Material.MAP)
              .amount(MiniGameCore.getStartVoteManager().getNumberOfVotes())
              .displayName("§b§l맵 투표")
              .lore(MiniGameCore.getStartVoteManager().isVoting() ? "§f우클릭 시 맵 투표 GUI가 열립니다." : "§f우클릭 시 맵 투표를 시작합니다.")
              .build();
        }

        @Override
        public void onIconClick(IconClickEvent e) {
          if(e.getQuickBarClickEvent().getClickAction() == ClickAction.LEFT_CLICK) return;

          else if(MiniGameCore.getMapManager().getMaps().size() < 1) {
            Core.wmsg(e.getPlayer(), "아직 맵이 생성되지 않았습니다.");
            return;
          }

          MiniGameCore.getMapVoteManager().getGui().open(e.getPlayer(), 1);
        }
      };

      setIcon(AutoGameConfig.isUseStartVote() ? 2 : 1, mapVoteIcon);
    }

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