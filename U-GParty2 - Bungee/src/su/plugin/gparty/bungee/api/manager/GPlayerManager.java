package su.plugin.gparty.bungee.api.manager;

import java.util.ArrayList;
import java.util.List;
import su.plugin.gparty.bungee.api.object.GPartyPlayer;
import su.plugin.gparty.common.api.manager.PlayerManager;
import su.plugin.gparty.common.api.object.PartyPlayer;

public class GPlayerManager extends PlayerManager {

  public List<GPartyPlayer> getChatSpys() {
    List<GPartyPlayer> list = new ArrayList<>();

    for (PartyPlayer pp : getPartyPlayers().values()) {
      if (!((GPartyPlayer) pp).isChatSpy()) continue;

      list.add((GPartyPlayer) pp);
    }

    return list;
  }

}
