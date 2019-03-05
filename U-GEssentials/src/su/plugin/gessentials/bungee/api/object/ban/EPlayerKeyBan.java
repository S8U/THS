package su.plugin.gessentials.bungee.api.object.ban;

import lombok.Getter;
import su.plugin.core.common.api.player.PlayerKey;

@Getter
public class EPlayerKeyBan extends EBan {
	
	private final PlayerKey playerKey;
	
	public EPlayerKeyBan(PlayerKey playerKey, int adminId, long time, long duration, String reason) {
		super(adminId, time, duration, reason);
		
		this.playerKey = playerKey;
	}
	
}