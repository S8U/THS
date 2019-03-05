package su.plugin.core.bukkit.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.plugin.core.bukkit.api.event.UKCancellableEvent;

@RequiredArgsConstructor
public class PlayerMoveLocationEvent extends UKCancellableEvent {
	
	@Getter
	private final Player player;
	
	@Getter
	private final PlayerMoveEvent playerMoveEvent;
	
}