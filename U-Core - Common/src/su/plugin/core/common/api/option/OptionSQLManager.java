package su.plugin.core.common.api.option;

import com.google.gson.Gson;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;

@Getter
public class OptionSQLManager extends SQLManagerBase {
	
	private SQLTable playerOptionTable, serverOptionTable;
	
	@Setter
	private boolean useBungeeSync;
	
	public OptionSQLManager() {
		setUseUseOption(true);
		
		setConfigName("option-sql-config");
		setSQLiteFileName("option-storage.db");
	}
	
	@Override
	public void createTable() {
		playerOptionTable = new SQLTable(this, "Option_Player", "player_id int, name varchar(255), value text, primary key(player_id, name)").createTable();
		serverOptionTable = new SQLTable(this, "Option_Server", "name varchar(255) primary key, value text").createTable();
	}
	
	@Override
	public void createJsonConfigOthers() {
		getJsonConfig().addDefault("MySQL.번지코드 채널들과 옵션 연동", false);
	}
	
	@Override
	public void loadJsonConfigOthers() {
		useBungeeSync = getJsonConfig().getBoolean("MySQL.번지코드 채널들과 옵션 연동");
	}
	
	//
	
	public void setPlayerOption(PlayerKey playerKey, String optionName, Object value) {
		if(!isUse()) return;
		
		playerOptionTable.insertDuplicate(playerKey, optionName, new Gson().toJson(value));
	}
	
	public void setPlayerOption(PlayerKey playerKey, HashMap<String, Object> options) {
		if(!isUse()) return;
		
		for(String name : options.keySet()) {
			setPlayerOption(playerKey, name, options.get(name));
		}
	}
	
	public void deletePlayerOption(PlayerKey playerKey, String optionName) {
		playerOptionTable.delete("where player_id = " + playerKey + " and name = '" + optionName +"'");
	}
	
	@SneakyThrows(SQLException.class)
	public boolean existsPlayerOption(PlayerKey playerKey, String optionName) {
		if(!isUse()) return false;
		
		@Cleanup PreparedStatement state = playerOptionTable.select("player_id", "where player_id = " + playerKey + " and name = '" + optionName + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public Object getPlayerOption(PlayerKey playerKey, String optionName) {
		if(!isUse()) return false;
		
		@Cleanup PreparedStatement state = playerOptionTable.select("*", "where player_id = " + playerKey + " and name = '" + optionName + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return null;
		
		return new Gson().fromJson(result.getString("value"), Object.class);
	}
	
	@SneakyThrows(SQLException.class)
	public HashMap<String, Object> getPlayerOptions(PlayerKey playerKey) {
		if(!isUse()) return null;
		
		@Cleanup PreparedStatement state = playerOptionTable.select("*", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		HashMap<String, Object> options = new HashMap<>();
		
		while(result.next()) {
			options.put(result.getString("name"), new Gson().fromJson(result.getString("value"), Object.class));
		}
		
		return options;
	}
	
	@SneakyThrows(SQLException.class)
	public void loadPlayerOption(PlayerKey playerKey, String optionName) {
		if(!isUse()) return;
		
		@Cleanup PreparedStatement state = playerOptionTable.select("*", "where player_id = " + playerKey + " and name = '" + optionName + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return;
		
		Core.getOptionManager().setPlayerOption(playerKey, result.getString("name"), new Gson().fromJson(result.getString("value"), Object.class), false);
	}
	
	public void loadPlayerOptions(PlayerKey playerKey) {
		if(!isUse()) return;
		
		Core.getOptionManager().setPlayerOptions(playerKey, getPlayerOptions(playerKey), false);
	}
	
	//
	
	public void setServerOption(String optionName, Object value) {
		if(!isUse()) return;
		
		serverOptionTable.insertDuplicate(optionName, new Gson().toJson(value));
	}
	
	public void deleteServerOption(String optionName) {
		serverOptionTable.delete("where name = '" + optionName + "'");
	}
	
	@SneakyThrows(SQLException.class)
	public boolean existsServerOption(String optionName) {
		if(!isUse()) return false;
		
		@Cleanup PreparedStatement state = serverOptionTable.select("*", "where name = '" + optionName + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public Object getServerOption(String optionName) {
		if(!isUse()) return null;
		
		@Cleanup PreparedStatement state = serverOptionTable.select("*", "where name = '" + optionName + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return null;
		
		return new Gson().fromJson(result.getString("value"), Object.class);
	}
	
	public void loadServerOption(String optionName) {
		if(!isUse()) return;
		
		Core.getOptionManager().setServerOption(optionName, getServerOption(optionName), false);
	}
	
	@SneakyThrows(SQLException.class)
	public void loadServerOptions() {
		if(!isUse()) return;
		
		@Cleanup PreparedStatement state = serverOptionTable.select("*");
		@Cleanup ResultSet result = state.executeQuery();
		
		HashMap<String, Object> options = new HashMap<>();
		
		while(result.next()) {
			options.put(result.getString("name"), new Gson().fromJson(result.getString("value"), Object.class));
		}
		
		Core.getOptionManager().setServerOptions(options, false);
	}
	
}