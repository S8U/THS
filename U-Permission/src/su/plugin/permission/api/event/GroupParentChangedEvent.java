package su.plugin.permission.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import su.plugin.core.bukkit.api.event.UKEvent;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.category.ChangeAction;
import su.plugin.permission.api.object.PermissionGroup;

@ToString
@RequiredArgsConstructor
public class GroupParentChangedEvent extends UKEvent {
	
	@Getter
	private final String groupName, parent;
	
	@Getter
	private final ChangeAction action;
	
	public PermissionGroup getGroup() {
		return PermissionAPI.getGroupManager().getGroup(groupName);
	}
	
}