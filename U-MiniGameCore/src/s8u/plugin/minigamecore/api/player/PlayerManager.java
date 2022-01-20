package s8u.plugin.minigamecore.api.player;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import s8u.plugin.minigamecore.PluginChecker;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NotDuplicatedArrayList;

public class PlayerManager {

  private HashMap<PlayerKey, GamePlayer> players = new HashMap<>();

  public GamePlayer registerGamePlayer(PlayerKey playerKey) {
    GamePlayer gp = new GamePlayer(playerKey);

    players.put(playerKey,gp);

    return gp;
  }

  public GamePlayer getGamePlayer(PlayerKey playerKey) {
    return players.get(playerKey);
  }

  public boolean existsGamePlayer(PlayerKey playerKey) {
    return players.containsKey(playerKey);
  }

  public void removeGamePlayer(PlayerKey playerKey) {
    players.remove(playerKey);
  }

  //

  // 게임에 참여한 온라인 플레이어
  public List<GamePlayer> getOnlineJoinPlayers() {
    return players.values().stream().filter(gp -> gp.isOnline() && !gp.isSpectator()).collect(Collectors.toList());
  }

  // 게임에 참여하고 탈락하지 않은 온라인 플레이어
  public List<GamePlayer> getOnlinePlayingPlayers() {
    return players.values().stream().filter(GamePlayer::isPlaying).collect(Collectors.toList());
  }

  // 관전 중인 온라인 플레이어
  public List<GamePlayer> getOnlineSpectators() {
    return players.values().stream().filter(gp -> gp.isOnline() && gp.isSpectator()).collect(Collectors.toList());
  }

  // 남아있는 팀 수
  public int getNumberOfTeams() {
    if (PluginChecker.isUseGParty()) { // 파티 플러그인을 사용 할 경우
      int solo = 0;
      List<su.plugin.gparty.common.api.object.Party> parties = new NotDuplicatedArrayList<>();

      for (GamePlayer gp : getOnlinePlayingPlayers()) {
        su.plugin.gparty.common.api.object.Party party = su.plugin.gparty.bukkit.api.KGPartyAPI.getPlayerManager().getPartyPlayers().get(gp.getPlayerKey()).getParty();
        if (party == null) {
          solo++;
        } else {
          parties.add(party);
        }
      }

      return parties.size() + solo;
    } else {
      return getOnlinePlayingPlayers().size();
    }
  }

}