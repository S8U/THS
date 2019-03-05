package su.plugin.permission.api.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.category.ChangeAction;
import su.plugin.permission.api.event.GroupNodeChangedEvent;
import su.plugin.permission.api.event.GroupParentChangedEvent;
import su.plugin.permission.api.event.GroupPrefixChangedEvent;
import su.plugin.permission.api.event.GroupSuffixChangedEvent;

@RequiredArgsConstructor
@AllArgsConstructor
public class PermissionGroup {
	
	@Getter
	private final String name;
	
	@Getter
	private String prefix, suffix;
	
	@Setter
	@Getter
	private List<String> parents = new ArrayList<>();
	
	@Setter
	@Getter
	private List<String> nodes = new ArrayList<>();
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
		
		Bukkit.getPluginManager().callEvent(new GroupPrefixChangedEvent(name, prefix));
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
		
		Bukkit.getPluginManager().callEvent(new GroupSuffixChangedEvent(name, suffix));
	}
	
	public boolean hasPrefix() {
		return prefix != null;
	}
	
	public boolean hasSuffix() {
		return suffix != null;
	}
	
	public boolean addNode(String node) {
		if(hasNode(node)) return false;
		
		nodes.add(node);
		
		Bukkit.getPluginManager().callEvent(new GroupNodeChangedEvent(name, node, ChangeAction.ADD));
		return true;
	}
	
	public boolean removeNode(String node) {
		if(!hasNode(node)) return false;
		
		nodes.remove(getNode(node));
		
		Bukkit.getPluginManager().callEvent(new GroupNodeChangedEvent(name, node, ChangeAction.REMOVE));
		return true;
	}
	
	public boolean hasNode(String node) {
		for(String n : nodes) {
			if(node.equalsIgnoreCase(n)) return true;
		}
		
		return false;
	}
	
	public boolean hasNodeIncludeParent(String node) {
		for(String n : getAllNodes()) {
			if(node.equalsIgnoreCase(n)) return true;
		}
		
		return false;
	}
	
	public String getNode(String node) {
		for(String n : nodes) {
			if(node.equalsIgnoreCase(n)) return n;
		}
		
		return null;
	}
	
	public boolean addParent(String parent) {
		if(hasParent(parent)) return false;
		parents.add(parent.toLowerCase());
		
		Bukkit.getPluginManager().callEvent(new GroupParentChangedEvent(name, parent, ChangeAction.ADD));
		return true;
	}
	
	public void removeParent(String parent) {
		parents.remove(parent);
		
		Bukkit.getPluginManager().callEvent(new GroupParentChangedEvent(name, parent, ChangeAction.REMOVE));
	}
	
	public boolean hasParent(String parent) {
		return parents.contains(parent.toLowerCase());
	}
	
	public boolean hasParents() {
		return parents.size() > 0;
	}
	
	public boolean setDefaultGroup() {
		if(isDefaultGroup()) return false;
		
		PermissionAPI.getGroupManager().setDefaultGroupName(name);
		return true;
	}
	
	public boolean isDefaultGroup() {
		return PermissionAPI.getGroupManager().getDefaultGroupName() != null && PermissionAPI.getGroupManager().getDefaultGroupName().equalsIgnoreCase(name);
	}
	
	public List<PermissionGroup> getChildGroups() {
		List<PermissionGroup> l = new ArrayList<>();
		
		for(PermissionGroup pg : PermissionAPI.getGroupManager().getPermissionGroups().values()) {
			if(!pg.getParentGroups().contains(this)) continue;
			l.add(pg);
		}
		
		return l;
	}
	
	public List<PermissionGroup> getParentGroups() {
		List<PermissionGroup> l = new ArrayList<>();
		
		for(String p : getParents()) {
			PermissionGroup pg = PermissionAPI.getGroupManager().getGroup(p);
			if(pg == null) continue;
			
			l.add(pg);

			if(pg.hasParents()) {
				l.addAll(pg.getParentGroups());
			}
		}
		
		return l;
	}
	
	public List<String> getParentNodes() {
		List<String> l = new ArrayList<>();
		
		for(PermissionGroup pg : getParentGroups()) {
			l.addAll(pg.getNodes());
		}
		
		return l;
	}
	
	public List<String> getAllNodes() {
		List<String> l = new ArrayList<>();
		
		l.addAll(nodes);
		l.addAll(getParentNodes());
		
		return l;
	}
	
	
	public List<PermissionPlayer> getOnlinePlayers() {
		List<PermissionPlayer> l = new ArrayList<>();
		
		for(PermissionPlayer pp : PermissionAPI.getPlayerManager().getPermissionPlayers().values()) {
			if(!pp.isOnline()) continue;
			
			l.add(pp);
		}
		
		return l;
	}
	
	public void updatePlayerPermissionAttachments() {
		for(PermissionPlayer pp : getOnlinePlayers()) {
			pp.updatePermissionAttachment();
		}
		
		for(PermissionGroup pg : getChildGroups()) {
			for(PermissionPlayer pp : pg.getOnlinePlayers()) {
				pp.updatePermissionAttachment();
			}
		}
	}
	
}