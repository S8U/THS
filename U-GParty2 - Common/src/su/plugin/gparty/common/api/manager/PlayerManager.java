package su.plugin.gparty.common.api.manager;

import java.util.HashMap;
import lombok.Getter;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.gparty.common.api.object.PartyPlayer;

public class PlayerManager {

  @Getter
  private HashMap<PlayerKey, PartyPlayer> partyPlayers = new HashMap<>();

}