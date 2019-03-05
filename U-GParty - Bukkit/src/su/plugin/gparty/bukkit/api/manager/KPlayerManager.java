package su.plugin.gparty.bukkit.api.manager;

import java.util.HashMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import su.plugin.gparty.bukkit.api.object.KPartyPlayer;
import su.plugin.core.common.api.player.PlayerKey;

public class KPlayerManager {
	
	@Getter
	private HashMap<PlayerKey, KPartyPlayer> players = new HashMap<>();
	
	public void setPartyPlayer(Player p, KPartyPlayer pp) {
		setPartyPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p), pp);
	}
	
	public void setPartyPlayer(PlayerKey playerKey, KPartyPlayer pp) {
		players.put(playerKey, pp);
	}
	
	public void removePartyPlayer(Player p) {
		removePartyPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public void removePartyPlayer(PlayerKey playerKey) {
		players.remove(playerKey);
	}
	
	public boolean existsPartyPlayer(Player p) {
		return existsPartyPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public boolean existsPartyPlayer(PlayerKey playerKey) {
		return players.containsKey(playerKey);
	}
	
	public KPartyPlayer getPartyPlayer(Player p) {
		return getPartyPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public KPartyPlayer getPartyPlayer(PlayerKey playerKey) {
		if(!existsPartyPlayer(playerKey)) return null;
		return players.get(playerKey);
	}
	
	public boolean existPlayer(Player p) {
		return existPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public boolean existPlayer(PlayerKey playerKey) {
		return players.containsKey(playerKey);
	}
	
}