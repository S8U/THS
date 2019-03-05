package su.plugin.permission.api.event;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.event.UKEvent;
import su.plugin.core.common.api.player.PlayerKey;

@ToString
@RequiredArgsConstructor
public class OnlinePlayerSuffixChangedEvent extends UKEvent {
	
	@Getter
	private final PlayerKey playerKey;
	
	@Getter
	private final String suffix;
	
	public Player getPlayer() {
		return KCore.getPlayer(playerKey);
	}
	
}