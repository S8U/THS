package su.plugin.permission.api.object;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.permission.PermissionPlugin;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.category.ChangeAction;
import su.plugin.permission.api.event.OnlinePlayerGroupChangedEvent;
import su.plugin.permission.api.event.OnlinePlayerNodeChangedEvent;
import su.plugin.permission.api.event.OnlinePlayerPrefixChangedEvent;
import su.plugin.permission.api.event.OnlinePlayerSuffixChangedEvent;

@AllArgsConstructor
public class PermissionPlayer {
	
	@Getter
	private final PlayerKey playerKey;
	
	@Setter
	@Getter
	private String name;
	
	@Getter
	private String groupName;
	
	private String prefix, suffix;
	
	@Setter
	@Getter
	private List<String> nodes = new ArrayList<>();
	
	public Player getBukkitPlayer() {
		return Bukkit.getPlayer(name);
	}
	
	public boolean isOnline() {
		return getBukkitPlayer() != null;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
		
		Bukkit.getPluginManager().callEvent(new OnlinePlayerPrefixChangedEvent(playerKey, prefix));
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
		
		Bukkit.getPluginManager().callEvent(new OnlinePlayerSuffixChangedEvent(playerKey, suffix));
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
		
		Bukkit.getPluginManager().callEvent(new OnlinePlayerGroupChangedEvent(playerKey, groupName));
	}
	
	public boolean hasPrefix() {
		return prefix != null;
	}
	
	public boolean hasSuffix() {
		return suffix != null;
	}
	
	public String getPrefix() {
		return hasPrefix() ? prefix : (hasGroup() ? getGroup().getPrefix() : null);
	}
	
	public String getSuffix() {
		return hasSuffix() ? suffix : (hasGroup() ? getGroup().getSuffix() : null);
	}
	
	public PermissionAttachment updatePermissionAttachment() {
		if(!isOnline()) return null;
		else if(getPermissionAttachment() != null) {
			getBukkitPlayer().removeAttachment(getPermissionAttachment());
		}
		
		PermissionAttachment attachment = getBukkitPlayer().addAttachment(PermissionPlugin.getInstance());
		
		PermissionAPI.getAttachmentManager().setAttachment(playerKey, attachment);
		
		for(String node : nodes) {
			addPermission(node);
		}
		
		if(hasGroup()) {
			for(String node : getGroup().getAllNodes()) {
				addPermission(node);
			}
		}
		
		return attachment;
	}
	
	public PermissionAttachment getPermissionAttachment() {
		return PermissionAPI.getAttachmentManager().getAttachment(playerKey);
	}
	
	public void setPermission(String node, boolean toggle) {
		if(node.equals("*")) {
			for(Permission perm : Bukkit.getPluginManager().getDefaultPermissions(true)) {
				addPermission(perm.getName());
			}

			for(Permission perm : Bukkit.getPluginManager().getPermissions()) {
				addPermission(perm.getName());
			}
			return;
		}

		getPermissionAttachment().setPermission(node, toggle);
	}
	
	public boolean addPermission(String node) {
		if(hasPermission(node)) return false;
		
		setPermission(node, true);
		return true;
	}
	
	public boolean removePermission(String node) {
		if(!hasPermission(node)) return false;
		
		getPermissionAttachment().unsetPermission(node);
		return true;
	}
	
	public boolean hasPermission(String node) {
		return isOnline() ? getBukkitPlayer().hasPermission(node) : false;
	}
	
	public boolean addNode(String node) {
		if(hasNode(node)) return false;
		
		nodes.add(node);
		
		Bukkit.getPluginManager().callEvent(new OnlinePlayerNodeChangedEvent(playerKey, node, ChangeAction.ADD));
		return true;
	}
	
	public boolean removeNode(String node) {
		if(!hasNode(node)) return false;
		
		nodes.remove(getNode(node));
		
		Bukkit.getPluginManager().callEvent(new OnlinePlayerNodeChangedEvent(playerKey, node, ChangeAction.REMOVE));
		return true;
	}
	
	public boolean hasNode(String node) {
		for(String n : nodes) {
			if(node.equalsIgnoreCase(n)) return true;
		}
		
		return false;
	}
	
	public boolean hasNodeIncludeGroup(String node) {
		return hasNode(node) || (hasGroup() && getGroup().hasNodeIncludeParent(node));
	}
	
	public String getNode(String node) {
		for(String n : nodes) {
			if(node.equalsIgnoreCase(n)) return n;
		}
		
		return null;
	}
	
	public boolean hasGroup() {
		return groupName != null;
	}
	
	public PermissionGroup getGroup() {
		return PermissionAPI.getGroupManager().getGroup(groupName);
	}
	
}