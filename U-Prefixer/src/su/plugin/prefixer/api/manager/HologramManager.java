package su.plugin.prefixer.api.manager;

import java.util.HashMap;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.prefixer.task.HologramShowTask;

public class HologramManager {
	
	@Setter
	@Getter
	private HashMap<PlayerKey, Hologram> holograms = new HashMap<>();
	
	@Getter
	private HashMap<PlayerKey, Long> moveTimes = new HashMap<>();
	
	@Getter
	private HologramShowTask hologramShowTask = new HologramShowTask();
	
	public void setHologram(PlayerKey playerKey, Hologram hologram) {
		holograms.put(playerKey, hologram);
	}
	
	public void removeHologram(PlayerKey playerKey) {
		holograms.remove(playerKey);
	}
	
	public boolean existsHologram(PlayerKey playerKey) {
		return holograms.containsKey(playerKey);
	}
	
	public Hologram getHologram(PlayerKey playerKey) {
		return holograms.get(playerKey);
	}
	
	public void updateMoveTime(PlayerKey playerKey) {
		moveTimes.put(playerKey, System.currentTimeMillis());
	}
	
	public void removeMoveTime(PlayerKey playerKey) {
		moveTimes.remove(playerKey);
	}
	
	public boolean hasMoveTime(PlayerKey playerKey) {
		return moveTimes.containsKey(playerKey);
	}
	
	public long getMoveTime(PlayerKey playerKey) {
		return moveTimes.get(playerKey);
	}
	
}