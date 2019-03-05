package su.plugin.gparty.bukkit.api.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import su.plugin.core.common.api.player.PlayerKey;

@ToString
@RequiredArgsConstructor
public class KPartyPlayer {
	
	@Getter
	private final PlayerKey playerKey;

	@Setter
	@Getter
	private KParty party;

	public Player getPlayer() {
		return (Player) playerKey.getPlatformPlayer();
	}
	
	public boolean hasParty() {
		return party != null;
	}
	
}