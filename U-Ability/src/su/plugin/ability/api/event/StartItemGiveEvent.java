package su.plugin.ability.api.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.api.event.UKCancellableEvent;

public class StartItemGiveEvent extends UKCancellableEvent {
	
	@Getter
	private final Player player;
	
	public StartItemGiveEvent(Player player) {
		this.player = player;
	}
	
}