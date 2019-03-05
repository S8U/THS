package su.plugin.ability.api.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import su.plugin.ability.api.object.GameMap;
import su.plugin.core.bukkit.api.event.UKCancellableEvent;

public class RangeOutEvent extends UKCancellableEvent {
	
	@Getter
	private final Player player;
	
	@Getter
	private final GameMap map;
	
	public RangeOutEvent(Player player, GameMap map) {
		this.player = player;
		this.map = map;
	}
	
}