package s8u.plugin.minigamecore.api.vote.map;

import java.util.HashMap;
import lombok.Getter;
import s8u.plugin.minigamecore.api.map.GameMap;
import su.plugin.core.common.api.player.PlayerKey;

public class MapVoteManager {

  @Getter
  private MapVoteGUI gui = new MapVoteGUI();

  @Getter
  private HashMap<PlayerKey, GameMap> mapVotes = new HashMap<>();

  public void voteTo(PlayerKey playerKey, GameMap map) {
    mapVotes.put(playerKey,map);
  }

  public void removeVote(PlayerKey playerKey) {
    mapVotes.remove(playerKey);
  }

  public int getMapVoteCount(GameMap map) {
    return (int) mapVotes.values().stream().filter(m -> m.equals(map)).count();
  }

}
