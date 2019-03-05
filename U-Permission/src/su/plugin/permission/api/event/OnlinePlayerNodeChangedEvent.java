package su.plugin.permission.api.event;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.event.UKEvent;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.permission.api.category.ChangeAction;

@ToString
@RequiredArgsConstructor
public class OnlinePlayerNodeChangedEvent extends UKEvent {
	
	@Getter
	private final PlayerKey playerKey;
	
	@Getter
	private final String node;
	
	@Getter
	private final ChangeAction action;
	
	public Player getPlayer() {
		return KCore.getPlayer(playerKey);
	}
	
}