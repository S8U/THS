package s8u.plugin.minigamecore.api.vote.start;

import java.util.List;
import s8u.plugin.minigamecore.api.MiniGameCore;
import s8u.plugin.minigamecore.api.config.AutoGameConfig;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

public class DefaultStartVoteHandler implements StartVoteHandler {

  @Override
  public void onVote(UPlayer player, boolean agree, List<PlayerKey> agreePlayers, List<PlayerKey> disagreePlayers) {
    int playerCount = MiniGameCore.getPlayerManager().getOnlineJoinPlayers().size();

    if ((playerCount == 2 && agreePlayers.size() >= playerCount) || agreePlayers.size() >= playerCount / 2) {
      MiniGameCore.getStartVoteManager().stopVote(null);
      MiniGameCore.getGameManager().gameStart(AutoGameConfig.isUseAutoMode());
    } else if(playerCount % 2 == 0 && disagreePlayers.size() >= playerCount / 2) {
      MiniGameCore.getStartVoteManager().stopVote(null);
      Core.cbc(ChatColor.RED, "§c게임 시작 투표가 부결되었습니다.");
    }
  }
}
