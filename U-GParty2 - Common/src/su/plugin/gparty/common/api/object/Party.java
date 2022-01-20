package su.plugin.gparty.common.api.object;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NotDuplicatedArrayList;

@Setter
@Getter
public class Party {

  private PlayerKey leader;

  private List<PartyPlayer> players = new NotDuplicatedArrayList<>();

  public void bc(String msg) {
    players.forEach(pp -> pp.getPlayerKey().getUPlayer().msg(msg));
  }

  public void msg(PartyPlayer partyPlayer, String msg) {
    players.forEach(pp -> pp.getPlayerKey().getUPlayer().nmsg("§a[파티 채팅] " + partyPlayer.getPlayerKey().getDisplayName() + "§a: " + msg));
  }

}
