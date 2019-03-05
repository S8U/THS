package su.plugin.effect.api.manager;

import java.util.HashMap;

import lombok.Getter;
import su.plugin.core.common.player.PlayerKey;
import su.plugin.effect.api.object.EffectPlayer;

public class PlayerManager {
	
	@Getter
	private HashMap<PlayerKey, EffectPlayer> effectPlayers = new HashMap<>();
	
	public void setEffectPlayer(PlayerKey playerKey, EffectPlayer effectPlayer) {
		effectPlayers.put(playerKey, effectPlayer);
	}
	
	public EffectPlayer getEffectPlayer(PlayerKey playerKey) {
		return effectPlayers.get(playerKey);
	}
	
	public boolean existsEffectPlayer(PlayerKey playerKey) {
		return effectPlayers.containsKey(playerKey);
	}
	
	public void removeEffectPlayer(EffectPlayer effectPlayer) {
		effectPlayers.remove(effectPlayer);
	}
	
}