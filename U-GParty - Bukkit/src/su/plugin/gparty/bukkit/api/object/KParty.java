package su.plugin.gparty.bukkit.api.object;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.plugin.core.common.api.player.PlayerKey;

@ToString
public class KParty {

	@Setter
	@Getter
	private List<PlayerKey> players = new ArrayList<>();
	
	public boolean addPlayer(Player p) {
		return addPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public boolean addPlayer(PlayerKey playerKey) {
		if(hasPlayer(playerKey)) return false;
		players.add(playerKey); return true;
	}
	
	public boolean removePlayer(Player p) {
		return removePlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public boolean removePlayer(PlayerKey playerKey) {
		return players.remove(playerKey);
	}
	
	public boolean hasPlayer(Player p) {
		return hasPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public boolean hasPlayer(PlayerKey playerKey) {
		return players.contains(playerKey);
	}

	public List<Player> getOnlinePlayers() {
		List<Player> players = new ArrayList<>();
		for(PlayerKey pk : this.players) {
			Player player = (Player) pk.getPlatformPlayer();
			if(player == null) continue;
			
			players.add(player);
		}

		return players;
	}
	
}