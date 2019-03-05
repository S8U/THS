package su.plugin.pvpstats.api.manager;

import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.pvpstats.api.PVPStatsAPI;
import su.plugin.pvpstats.api.object.PSPlayer;

public class PlayerManager {
	
	@Setter
	@Getter
	private HashMap<PlayerKey, PSPlayer> PSPlayers = new HashMap<>();
	
	public void setPSPlayer(PlayerKey playerKey, PSPlayer pp) {
		PSPlayers.put(playerKey, pp);
	}
	
	public void removePSPlayer(PlayerKey playerKey) {
		PSPlayers.remove(playerKey);
	}
	
	public boolean existsPSPlayer(PlayerKey playerKey) {
		return PSPlayers.containsKey(playerKey);
	}
	
	public PSPlayer getPSPlayer(PlayerKey playerKey) {
		return PSPlayers.get(playerKey);
	}
	
	public PSPlayer getPSPlayer(PlayerKey playerKey, boolean sql) {
		return existsPSPlayer(playerKey) ? PSPlayers.get(playerKey) : PVPStatsAPI.getSQLManager().loadPlayer(playerKey);
	}
	
	public void registerPlayer(Player p) {
		PVPStatsAPI.getSQLManager().loadPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}

	public void registerAllPlayers() {
		KCore.getOnlinePlayers().forEach(ap -> registerPlayer(ap));
	}
	
}