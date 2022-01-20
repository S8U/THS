package su.plugin.gparty.common.api.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import su.plugin.core.common.api.player.PlayerKey;

@Setter
@Getter
@RequiredArgsConstructor
public class PartyPlayer {

  private final PlayerKey playerKey;

  private Party party;

  public boolean hasParty() {
    return party != null;
  }

}