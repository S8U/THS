package s8u.plugin.minigamecore.api.vote.start;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import s8u.plugin.minigamecore.api.MiniGameCore;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;

public class StartVoteGUI extends GUI {

  public StartVoteGUI() {
    super("U-MiniGameCore/Vote/Start", "게임 시작 투표", 1);

    // 투표 정보 아이콘
    Icon infoIcon = new Icon() {
      @Override
      protected ItemStack updateItem() {
        return new ItemBuilder(Material.PAPER)
            .amount(MiniGameCore.getStartVoteManager().getVoteTimeout() - MiniGameCore.getStartVoteManager().getStartVoteTask().getCount())
            .displayName("§l§e투표 정보")
            .lore("§b남은 시간: "
                + StringUtil.buildTimeString(
                (MiniGameCore.getStartVoteManager().getVoteTimeout() - MiniGameCore.getStartVoteManager().getStartVoteTask().getCount()) * 1000, ChatColor.AQUA))
            .lore("")
            .lore("§a투표 찬성: " + MiniGameCore.getStartVoteManager().getAgreePlayers().size() + "§a명")
            .lore("§c투표 반대: " + MiniGameCore.getStartVoteManager().getDisagreePlayers().size() + "§c명")
            .build();
      }
    };

    // 찬성 아이콘
    Icon agreeIcon = new Icon() {
      @Override
      protected ItemStack updateItem() {
        return new ItemBuilder("351:10")
            .amount(MiniGameCore.getStartVoteManager().getAgreePlayers().size())
            .displayName("§l§a찬성")
            .lore("§f클릭 시 투표에 §a찬성§f합니다.")
            .lore("")
            .lore("§b인원: §f" + MiniGameCore.getStartVoteManager().getAgreePlayers().size() + " §b명")
            .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        MiniGameCore.getStartVoteManager().joinVote(Core.getUPlayerByPlatformPlayer(e.getPlayer()),true);

        e.getPlayer().closeInventory();
      }
    };

    // 반대 아이콘
    Icon disagreeIcon = new Icon() {
      @Override
      protected ItemStack updateItem() {
        return new ItemBuilder("351:13")
            .amount(MiniGameCore.getStartVoteManager().getDisagreePlayers().size())
            .displayName("§l§c반대")
            .lore("§f클릭 시 투표에 §c반대§f합니다.")
            .lore("")
            .lore("§b인원: " + MiniGameCore.getStartVoteManager().getDisagreePlayers().size() + " §b명")
            .build();
      }

      @Override
      public void onIconClick(IconClickEvent e) {
        MiniGameCore.getStartVoteManager().joinVote(Core.getUPlayerByPlatformPlayer(e.getPlayer()),false);

        e.getPlayer().closeInventory();
      }
    };

    setIcon(2, 1, infoIcon);
    setIcon(6, 1, agreeIcon);
    setIcon(8, 1, disagreeIcon);
  }

}