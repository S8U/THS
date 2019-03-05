package su.plugin.ability.api.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.event.UKEvent;

public class JoinEvent extends UKEvent {
	
	@Getter
	private final Player player;
	
	@Getter
	private final GamePlayer gamePlayer;
	
	@Getter
	private final boolean reconnect;
	
	@Getter
	private PlayerJoinEvent playerJoinEvent;
	
	public JoinEvent(GamePlayer gamePlayer, boolean reconnect, PlayerJoinEvent playerJoinEvent) {
		player = playerJoinEvent.getPlayer();
		this.gamePlayer = gamePlayer;
		this.reconnect = reconnect;
		this.playerJoinEvent = playerJoinEvent;
	}
	
}