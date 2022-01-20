package su.plugin.gparty.bungee.api.object;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.gparty.common.api.object.Party;
import su.plugin.gparty.common.api.object.PartyPlayer;

@Setter
@Getter
public class GPartyPlayer extends PartyPlayer {

  private boolean moving, partyChat, chatSpy;

  private Party invitedParty;

  public GPartyPlayer(PlayerKey playerKey) {
    super(playerKey);
  }

  public boolean hasInvitedParty() {
    return invitedParty != null;
  }

}