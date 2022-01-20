package su.plugin.permission.api.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.bukkit.api.util.ChannelMessageUtil;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.object.PermissionGroup;

@Setter
@Getter
public class GroupManager {
	
	private String defaultGroupName;
	
	private HashMap<String, PermissionGroup> permissionGroups = new HashMap<>();
	
	public void setGroup(String name, PermissionGroup group) {
		permissionGroups.put(name.toLowerCase(), group);
	}
	
	public void removeGroup(String name) {
		permissionGroups.remove(name.toLowerCase());
	}
	
	public boolean existsGroup(String name) {
		return permissionGroups.containsKey(name.toLowerCase());
	}
	
	public PermissionGroup getGroup(String name) {
		return permissionGroups.get(name.toLowerCase());
	}
	
	public PermissionGroup getDefaultGroup() {
		return defaultGroupName == null ? null : getGroup(defaultGroupName);
	}
	
	public List<PermissionGroup> getPermissionGroupList() {
		List<PermissionGroup> l = new ArrayList<>();
		
		l.addAll(permissionGroups.values());
		
		return l;
	}

	public List<PermissionGroup> getChildGroups(PermissionGroup group) {
		List<PermissionGroup> list = new ArrayList<>();

		for (PermissionGroup pg : permissionGroups.values()) {
			for (String parentName : pg.getParents()) {
				if (group.getName().equals(parentName)) {
					list.add(pg);
					list.addAll(getChildGroups(pg));
					break;
				}
			}
		}

		return list;
	}

	public List<PermissionGroup> getParentGroups(PermissionGroup group) {
		List<PermissionGroup> list = new ArrayList<>();

		for (String parentName : group.getParents()) {
			PermissionGroup pg = getGroup(parentName);
			if (pg == null) continue;

			list.add(pg);
			list.addAll(getParentGroups(pg));
		}

		return list;
	}
	
	public void sendGroupUpdateToAllChannel(String group) {
		if(!PermissionAPI.isUseBungeecord()) return;
		
		ChannelMessageUtil.sendToAllChannelExistsPlayers("U-Permission", "GroupUpdate", group);
	}
	
	public void sendGroupDeleteToAllChannel(String group) {
		if(!PermissionAPI.isUseBungeecord()) return;
		
		ChannelMessageUtil.sendToAllChannelExistsPlayers("U-Permission", "GroupDelete", group);
	}
	
	public void sendDefaultGroupChangeToAllChannel() {
		if(!PermissionAPI.isUseBungeecord()) return;
		
		ChannelMessageUtil.sendToAllChannelExistsPlayers("U-Permission", "DefaultGroupChange");
	}
	
}