package s8u.plugin.minigamecore.api.vote.map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import s8u.plugin.minigamecore.api.MiniGameCore;
import s8u.plugin.minigamecore.api.map.GameMap;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.bukkit.api.gui.PageableGUI;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class MapVoteGUI extends PageableGUI {

  public MapVoteGUI() {
    super("U-MiniGameCore/Vote/Map", "게임 맵 투표", 6, 1, 9, 1, 4);

    // 4줄까지 맵
    // 5줄은 장식
    // 6줄은 페이지

    Icon deco = new Icon(new ItemStack(Material.STAINED_GLASS_PANE)) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };

    for (int i = 1; i <= 9; i++) {
      setCommonIcon(i,5,deco);
    }

    Icon previousIcon = new Icon(new ItemBuilder(Material.PAPER).displayName("§b§l이전 페이지").lore("§f클릭 시 이전 페이지로 이동합니다.").build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };

    Icon nextIcon = new Icon(new ItemBuilder(Material.PAPER).displayName("§b§l다음 페이지").lore("§f클릭 시 다음 페이지로 이동합니다.").build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };

    setCommonIcon(1, 6,previousIcon);
    setCommonIcon(9, 6, nextIcon);

    setPreviousIcon(1,6);
    setNextIcon(9,6);
  }

  @Override
  protected void onUpdate() {
    clearIcons();

    Icon randomIcon = new Icon() {
      @Override
      protected ItemStack updateItem() {
        int votedCount = MiniGameCore.getPlayerManager().getOnlineJoinPlayers().size() - MiniGameCore.getMapVoteManager().getMapVotes().size();

        return new ItemBuilder(Material.EMPTY_MAP)
            .amount(votedCount)
            .displayName("§f§l랜덤")
            .lore("§e클릭 시 §f랜덤§e에 투표합니다.", "", "§e인원: §f" + votedCount + " §e명")
            .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        MiniGameCore.getMapVoteManager().removeVote(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()));

        e.getPlayer().closeInventory();

        /*if(api.isUseWaitingQuickBar()) {
          api.getBarManager().getWaitingQuickBar().update();
        }*/

        MiniGameCore.getMapVoteManager().getGui().updateAsynchronously();

        Core.cmsg(e.getPlayer(), ChatColor.DARK_AQUA, "랜덤§e에 투표했습니다.");
      }
    };
    addIcon(randomIcon);

    for (GameMap map : MiniGameCore.getMapManager().getMaps().values()) {
      Icon mapIcon = new Icon() {
        GameMap gameMap = map;

        @Override
        protected ItemStack updateItem() {
          int votedCount = MiniGameCore.getMapVoteManager().getMapVoteCount(gameMap);
          return new ItemBuilder(Material.MAP)
              .amount(votedCount)
              .displayName("§f§l" + gameMap.getName())
              .lore("§e클릭 시 §f" + gameMap.getName() + " §e맵에 투표합니다.", "", "§e인원: §f" + votedCount + " §e명")
              .build();
        }

        @Override
        public void onIconClick(IconClickEvent e) {
          MiniGameCore.getMapVoteManager().voteTo(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()), gameMap);

          e.getPlayer().closeInventory();

          /*if(api.isUseWaitingQuickBar()) {
            api.getBarManager().getWaitingQuickBar().update();
          }*/

          MiniGameCore.getMapVoteManager().getGui().updateAsynchronously();

          Core.cmsg(e.getPlayer(), ChatColor.DARK_AQUA, gameMap.getName() + " §e맵에 투표했습니다.");
        }
      };
      addIcon(mapIcon);
    }
  }

}