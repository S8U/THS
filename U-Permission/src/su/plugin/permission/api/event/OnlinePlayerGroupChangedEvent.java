package su.plugin.permission.api.event;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.event.UKEvent;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.object.PermissionGroup;

@ToString
@RequiredArgsConstructor
public class OnlinePlayerGroupChangedEvent extends UKEvent {
	
	@Getter
	private final PlayerKey playerKey;
	
	@Getter
	private final String groupName;
	
	public Player getPlayer() {
		return KCore.getPlayer(playerKey);
	}
	
	public PermissionGroup getGroup() {
		return PermissionAPI.getGroupManager().getGroup(groupName);
	}
	
}