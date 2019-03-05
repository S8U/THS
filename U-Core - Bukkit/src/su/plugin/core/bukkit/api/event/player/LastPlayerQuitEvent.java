package su.plugin.core.bukkit.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.plugin.core.bukkit.api.event.UKEvent;

@RequiredArgsConstructor
@Getter
public class LastPlayerQuitEvent extends UKEvent {
	
	private final Player player;
	
	private final PlayerQuitEvent playerQuitEvent;
	
}