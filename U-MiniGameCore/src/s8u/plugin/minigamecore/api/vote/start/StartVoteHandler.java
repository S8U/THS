package s8u.plugin.minigamecore.api.vote.start;

import java.util.List;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

public interface StartVoteHandler {

  void onVote(UPlayer player, boolean agree, List<PlayerKey> agreePlayers, List<PlayerKey> disagreePlayers);

}