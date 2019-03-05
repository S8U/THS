package su.plugin.core.bukkit.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.plugin.core.bukkit.api.event.UKEvent;

@RequiredArgsConstructor
@Getter
public class FirstPlayerJoinEvent extends UKEvent {
	
	private final Player player;
	
	private final PlayerJoinEvent playerJoinEvent;
	
}