package su.plugin.prefixer.api.manager;

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
import su.plugin.prefixer.api.object.PrefixPlayer;

public class SQLManager extends SQLManagerBase {
	
	@Getter
	private SQLTable prefixTable, prefixMainTable, prefixLogTable, prefixMainLogTable;
	
	@Override
	public void createTable() {
		prefixTable = new SQLTable(this, "Prefix",
				"player_id int, prefix varchar(255), primary key(player_id, prefix)").createTable();
		
		prefixMainTable = new SQLTable(this, "Prefix_Main",
				"player_id int, priority int, prefix varchar(255), primary key(player_id, priority)").createTable();
		
		
		prefixLogTable = new SQLTable(this, "Prefix_Log",
				"id int unsigned not null auto_increment primary key, player_id int, task varchar(16), prefix varchar(255), time bigint").createTable();
		
		prefixMainLogTable = new SQLTable(this, "Prefix_Main_Log",
				"id int unsigned not null auto_increment primary key, player_id int, task varchar(16), prefix varchar(255), time bigint").createTable();
	}
	
	public void addPrefix(PlayerKey playerKey, String prefix) {
		prefixTable.insertIgnore(playerKey, prefix);
	}
	
	public void deletePrefix(PlayerKey playerKey, String prefix) {
		prefixTable.delete("where player_id = " + playerKey + " and prefix = '" + prefix + "'");
	}
	
	@SneakyThrows(SQLException.class)
	public boolean hasPrefix(PlayerKey playerKey, String prefix) {
		@Cleanup PreparedStatement state = prefixTable.select("prefix", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public List<String> getPrefixes(PlayerKey playerKey) {
		List<String> l = new ArrayList<>();
		
		@Cleanup PreparedStatement state = prefixTable.select("prefix", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		while(result.next()) {
			String prefix = result.getString("prefix");
			if(prefix == null) continue;
			
			l.add(prefix);
		}
		
		return l;
	}
	
	//
	
	public void setMainPrefix(PlayerKey playerKey, int priority, String prefix) {
		removeMainPrefix(playerKey, prefix);
		
		prefixMainTable.insertDuplicate(playerKey, priority, prefix);
	}
	
	public void removeMainPrefix(PlayerKey playerKey, String prefix) {
		prefixMainTable.delete("where player_id = " + playerKey + " and prefix = '" + prefix + "'");
	}
	
	@SneakyThrows(SQLException.class)
	public boolean isMainPrefixes(PlayerKey playerKey, String prefix) {
		@Cleanup PreparedStatement state = prefixMainTable.select("priority", "where player_id = " + playerKey + " and prefix = '" + prefix + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public HashMap<Integer,String> getMainPrefixes(PlayerKey playerKey) {
		HashMap<Integer, String> prefixes = new HashMap<>();
		
		@Cleanup PreparedStatement state = prefixMainTable.select("*", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		while(result.next()) {
			prefixes.put(result.getInt("priority"), result.getString("prefix"));
		}
		
		return prefixes;
	}
	
	public PrefixPlayer getPrefixPlayer(PlayerKey playerKey) {
		List<String> prefixes = getPrefixes(playerKey);
		if(prefixes.size() < 1) return null;
		
		return new PrefixPlayer(playerKey, prefixes, getMainPrefixes(playerKey));
	}
	
	public void writePrefixLog(PlayerKey playerKey, String task, String prefix) {
		prefixLogTable.insertDuplicate(null, playerKey, task, prefix, System.currentTimeMillis());
	}
	
	public void writeMainPrefixLog(PlayerKey playerKey, String task, String prefix) {
		prefixMainLogTable.insertDuplicate(null, playerKey, task, prefix, System.currentTimeMillis());
	}
	
}