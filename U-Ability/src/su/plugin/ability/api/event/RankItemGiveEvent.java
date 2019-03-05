package su.plugin.ability.api.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.api.event.UKCancellableEvent;

public class RankItemGiveEvent extends UKCancellableEvent {
	
	@Getter
	private final Player player;
	
	public RankItemGiveEvent(Player player) {
		this.player = player;
	}
	
}