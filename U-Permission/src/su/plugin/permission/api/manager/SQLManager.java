package su.plugin.permission.api.manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.object.PermissionGroup;
import su.plugin.permission.api.object.PermissionPlayer;

public class SQLManager extends SQLManagerBase {
	
	@Getter
	private SQLTable userTable, userNodeTable, groupTable, groupParentTable, groupNodeTable;
	
	@Override
	public void createTable() {
		userTable = new SQLTable(this, "User",
				"player_id int primary key, prefix varchar(255), suffix varchar(255), group_name varchar(255)").createTable();
		
		userNodeTable = new SQLTable(this, "User_Node",
				"player_id int, node varchar(255), primary key(player_id, node)").createTable();
		
		
		groupTable = new SQLTable(this, "Group",
				"name varchar(255) primary key, prefix varchar(255), suffix varchar(255)").createTable();
		
		groupParentTable = new SQLTable(this, "Group_Parent",
				"name varchar(255), parent_name varchar(255), primary key(name, parent_name)").createTable();
		
		groupNodeTable = new SQLTable(this, "Group_Node",
				"name varchar(255), node varchar(255), primary key(name, node)").createTable();
		
		getSQLConfig().createTable();
	}
	
	public void loadConfig() {
		PermissionAPI.getGroupManager().setDefaultGroupName(getDefaultGroup());
	}
	
	//
	
	public void setDefaultGroup(String groupName) {
		getSQLConfig().set("default_group", groupName);
	}
	
	public String getDefaultGroup() {
		return getSQLConfig().getString("default_group");
	}
	
	//
	
	public void savePlayer(PermissionPlayer pp) {
		userTable.insertDuplicate(pp.getPlayerKey(), pp.hasPrefix() ? pp.getPrefix() : null, pp.hasSuffix() ? pp.getSuffix() : null, pp.getGroupName());
	}
	
	public void setPlayerPrefix(PlayerKey playerKey, String prefix) {
		userTable.insertDuplicate(playerKey, prefix, "$$suffix", "$$group_name");
	}
	
	public void setPlayerSuffix(PlayerKey playerKey, String suffix) {
		userTable.insertDuplicate(playerKey, "$$prefix", suffix, "$$group_name");
	}
	
	public void setPlayerGroup(PlayerKey playerKey, String group) {
		userTable.insertDuplicate(playerKey, "$$prefix", "$$suffix", group);
	}
	
	public void addPlayerNode(PlayerKey playerKey, String node) {
		userNodeTable.insert(playerKey, node);
	}
	
	public void removePlayerNode(PlayerKey playerKey, String node) {
		userNodeTable.delete("where player_id = " + playerKey +" and node='" + node + "'");
	}
	
	public void deletePlayer(PlayerKey playerKey) {
		userTable.delete("where player_id = " + playerKey);
		userNodeTable.delete("where player_id = " + playerKey);
	}
	
	@SneakyThrows(SQLException.class)
	public boolean hasPlayerNode(PlayerKey playerKey, String node) {
		@Cleanup PreparedStatement state = userNodeTable.select("player_id", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public boolean existsPlayer(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = userTable.select("player_id", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public boolean loadPermissionPlayer(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = userTable.select("*", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return false;
		
		String prefix = result.getString("prefix");
		String suffix = result.getString("suffix");
		String groupName = result.getString("group_name");
		
		PermissionPlayer pp = new PermissionPlayer(playerKey, playerKey.getName(), groupName, prefix, suffix, getPlayerNodes(playerKey));
		
		PermissionAPI.getPlayerManager().setPermissionPlayer(playerKey, pp);
		
		pp.updatePermissionAttachment();
		
		return true;
	}
	
	@SneakyThrows(SQLException.class)
	public String getPlayerPrefix(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = userTable.select("prefix", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next() ? result.getString("prefix") : null;
	}
	
	@SneakyThrows(SQLException.class)
	public String getPlayerSuffix(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = userTable.select("suffix", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next() ? result.getString("suffix") : null;
	}
	
	@SneakyThrows(SQLException.class)
	public String getPlayerGroup(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = userTable.select("group", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next() ? result.getString("group") : null;
	}
	
	@SneakyThrows(SQLException.class)
	public List<String> getPlayerNodes(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = userNodeTable.select("node", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		List<String> l = new ArrayList<>();
		
		while(result.next()) {
			l.add(result.getString("node"));
		}
		
		return l;
	}
	
	@SneakyThrows(SQLException.class)
	public int getPlayerCount() {
		@Cleanup PreparedStatement state = userTable.select("count(player_id)");
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next() ? result.getInt("count(player_id)") : -1;
	}
	
	//
	
	public void saveGroup(PermissionGroup group) {
		groupTable.insertDuplicate(group.getName(), group.getPrefix(), group.getSuffix());
	}
	
	public void setGroupPrefix(String name, String prefix) {
		groupTable.insertDuplicate(name, prefix, "$$suffix");
	}
	
	public void setGroupSuffix(String name, String suffix) {
		groupTable.insertDuplicate(name, "$$prefix", suffix);
	}
	
	public void addGroupParent(String name, String parentName) {
		groupParentTable.insert(name, parentName);
	}
	
	public void removeGroupParent(String name, String parentName) {
		groupParentTable.delete("where name='" + name +"' and parent_name='" + parentName + "'");
	}
	
	public void deleteGroup(String name) {
		groupTable.delete("where name='" + name +"'");
		groupParentTable.delete("where name='" + name +"'");
		groupNodeTable.delete("where name='" + name +"'");
		
		userTable.update("group_name=null", "where group_name='" + name +"'");
	}
	
	public void addGroupNode(String name, String node) {
		groupNodeTable.insert(name, node);
	}
	
	public void removeGroupNode(String name, String node) {
		groupNodeTable.delete("where name='" + name +"' and node='" + node + "'");
	}
	
	@SneakyThrows(SQLException.class)
	public boolean existsGroup(String name) {
		@Cleanup PreparedStatement state = groupTable.select("name", "where name='" + name + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public PermissionGroup getGroup(String name) {
		PermissionGroup group = null;
		
		@Cleanup PreparedStatement state = groupTable.select("*", "where name='" + name + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		if(result.next()) {
			name = result.getString("name");
			String prefix = result.getString("prefix");
			String suffix = result.getString("suffix");
			
			group = new PermissionGroup(name, prefix, suffix, getGroupParents(name), getGroupNodes(name));
		}
		
		return group;
	}
	
	@SneakyThrows(SQLException.class)
	public HashMap<String, PermissionGroup> getAllGroups() {
		HashMap<String, PermissionGroup> groups = new HashMap<>();
		
		HashMap<String, List<String>> parents = getAllGroupParents();
		HashMap<String, List<String>> nodes = getAllGroupNodes();
		
		@Cleanup PreparedStatement state = groupTable.select("*");
		@Cleanup ResultSet result = state.executeQuery();
		
		while(result.next()) {
			String name = result.getString("name");
			String prefix = result.getString("prefix");
			String suffix = result.getString("suffix");
			
			List<String> pList = parents.containsKey(name.toLowerCase()) ? parents.get(name.toLowerCase()) : new ArrayList<>();
			List<String> nList = nodes.containsKey(name.toLowerCase()) ? nodes.get(name.toLowerCase()) : new ArrayList<>();
			
			groups.put(name.toLowerCase(), new PermissionGroup(name, prefix, suffix, pList, nList));
		}
		
		return groups;
	}
	
	public void loadGroup(String groupName) {
		PermissionGroup group = getGroup(groupName);
		if(group == null) return;
		
		PermissionAPI.getGroupManager().setGroup(groupName, group);
	}
	
	public void loadAllGroup() {
		PermissionAPI.getGroupManager().setPermissionGroups(getAllGroups());
	}
	
	@SneakyThrows(SQLException.class)
	public boolean hasGroupParent(String name) {
		@Cleanup PreparedStatement state = groupParentTable.select("name", "where name='" + name + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public List<String> getGroupParents(String name) {
		List<String> list = new ArrayList<>();
		
		@Cleanup PreparedStatement state = groupParentTable.select("parent_name", "where name='" + name + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		while(result.next()) {
			list.add(result.getString("parent_name"));
		}
		
		return list;
	}
	
	@SneakyThrows(SQLException.class)
	public HashMap<String, List<String>> getAllGroupParents() {
		HashMap<String, List<String>> h = new HashMap<>();
		
		@Cleanup PreparedStatement state = groupParentTable.select("*");
		@Cleanup ResultSet result = state.executeQuery();
		
		while(result.next()) {
			String name = result.getString("name");
			
			List<String> list = h.containsKey(name.toLowerCase()) ? h.get(name.toLowerCase()) : new ArrayList<>();
			list.add(result.getString("parent_name"));
			
			h.put(name.toLowerCase(), list);
		}
		
		return h;
	}
	
	@SneakyThrows(SQLException.class)
	public boolean hasGroupNode(String name) {
		@Cleanup PreparedStatement state = groupNodeTable.select("name", "where name='" + name + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public List<String> getGroupNodes(String name) {
		List<String> list = new ArrayList<>();
		
		@Cleanup PreparedStatement state = groupNodeTable.select("node", "where name='" + name + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		while(result.next()) {
			list.add(result.getString("node"));
		}
		
		return list;
	}
	
	@SneakyThrows(SQLException.class)
	public HashMap<String, List<String>> getAllGroupNodes() {
		HashMap<String, List<String>> h = new HashMap<>();
		
		@Cleanup PreparedStatement state = groupNodeTable.select("*");
		@Cleanup ResultSet result = state.executeQuery();
		
		while(result.next()) {
			String name = result.getString("name");
			
			List<String> list = h.containsKey(name.toLowerCase()) ? h.get(name.toLowerCase()) : new ArrayList<>();
			list.add(result.getString("node"));
			
			h.put(name.toLowerCase(), list);
		}
		
		return h;
	}

	@SneakyThrows(SQLException.class)
	public int getGroupPlayerCount(String groupName) {
		@Cleanup PreparedStatement state = userTable.select("count(player_id)", "where group_name = '" + groupName + "'");
		@Cleanup ResultSet result = state.executeQuery();

		return result.next() ? result.getInt("count(player_id)") : 0;
	}
	
}