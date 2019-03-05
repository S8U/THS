package su.plugin.core.common.api.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.platform.PlatformType;
import su.plugin.core.common.api.player.PlayerKey;

public class SQLManager extends SQLManagerBase {
	
	@Setter
	@Getter
	private boolean usePlayerKeyUpload, useConsoleLog, stableNewPlayerBukkitHandling;
	
	@Getter
	private SQLTable playerKeyTable, displayNameTable, NewPlayerEventHandleTable;
	
	@Override
	public void createTable() {
		playerKeyTable = new SQLTable(this, "PlayerKey", "id int not null auto_increment primary key, name varchar(16), uuid varchar(36), online_mode tinyint(1)").createTable();
		displayNameTable = new SQLTable(this, "DisplayName", "player_id int primary key, display_name varchar(255)").createTable();

		NewPlayerEventHandleTable = new SQLTable(this, "NewPlayerEventHandle", "player_id int primary key").createTable();
	}
	
	@Override
	public void createJsonConfigOthers() {
		getJsonConfig().addDefault("PlayerKey 업로드", false);
		getJsonConfig().addDefault("콘솔 로그 사용", true);

		if(Core.getPlatformType() == PlatformType.BUNGEECORD) {
			getJsonConfig().addDefault("새로운 플레이어 버킷에서 처리 안정화", true);
		}
	}
	
	@Override
	public void loadJsonConfigOthers() {
		usePlayerKeyUpload = getJsonConfig().getBoolean("PlayerKey 업로드");
		useConsoleLog = getJsonConfig().getBoolean("콘솔 로그 사용");

		if(Core.getPlatformType() == PlatformType.BUNGEECORD) {
			stableNewPlayerBukkitHandling = getJsonConfig().getBoolean("새로운 플레이어 버킷에서 처리 안정화");
		}
	}
	
	//
	
	public void savePlayerKey(int playerId, String name, UUID uuid, boolean onlineMode) {
		playerKeyTable.insertDuplicate(playerId, name, uuid == null ? null : uuid.toString(), onlineMode);
	}
	
	public void savePlayerKey(PlayerKey playerKey) {
		savePlayerKey(playerKey.getId(), playerKey.getName(), playerKey.getUuid(), playerKey.isOnlineMode());
	}
	
	public PlayerKey createPlayerKey(String name, UUID uuid, boolean onlineMode) {
		playerKeyTable.insertDuplicate(null, name, uuid.toString(), onlineMode);

		return getPlayerKey(name);
	}
	
	//
	
	@SneakyThrows(SQLException.class)
	public PlayerKey getPlayerKey(int id) {
		@Cleanup PreparedStatement state = playerKeyTable.select("*", "where id = " + id);
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return null;
		
		String uuid = result.getString("uuid");
		
		return new PlayerKey(result.getInt("id"), result.getString("name"), uuid == null ? null : UUID.fromString(uuid), result.getInt("online_mode") == 1);
	}
	
	@SneakyThrows(SQLException.class)
	public PlayerKey getPlayerKey(String name) {
		@Cleanup PreparedStatement state = playerKeyTable.select("*", "where name = '" + name + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return null;
		
		String uuid = result.getString("uuid");
		
		return new PlayerKey(result.getInt("id"), result.getString("name"), uuid == null ? null : UUID.fromString(uuid), result.getInt("online_mode") == 1);
	}
	
	@SneakyThrows(SQLException.class)
	public PlayerKey getPlayerKeyByDisplayName(String displayName) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(displayName);
		if(playerKey != null) return playerKey;
		
		@Cleanup PreparedStatement state = displayNameTable.select("player_id", "where replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(display_name, '§0', ''), '§1', ''), '§2', ''), '§3', ''), '§4', ''), '§5', ''), '§6', ''), '§7', ''), '§8', ''), '§9', ''), '§a', ''), '§b', ''), '§c', ''), '§d', ''), '§e', ''), '§f', ''), '§k', ''), '§l', ''), '§m', ''), '§n', ''), '§o', ''), '§r', '') = '" + displayName + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next() ? getPlayerKey(result.getInt("player_id")) : null;
	}
	
	@SneakyThrows(SQLException.class)
	public PlayerKey getPlayerKey(UUID uuid) {
		@Cleanup PreparedStatement state = playerKeyTable.select("*", "where uuid = '" + uuid.toString() + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return null;
		
		String uuidString = result.getString("uuid");
		
		return new PlayerKey(result.getInt("id"), result.getString("name"), uuidString == null ? null : UUID.fromString(uuidString), result.getInt("online_mode") == 1);
	}

	@SneakyThrows(SQLException.class)
	public int getPlayerKeyCount() {
		@Cleanup PreparedStatement state = playerKeyTable.select("count(id)");
		@Cleanup ResultSet result = state.executeQuery();

		return result.next() ? result.getInt("count(id)") : 0;
	}
	
	//
	
	public void setDisplayName(PlayerKey playerKey, String displayName) {
		displayNameTable.insertDuplicate(playerKey, displayName);
	}
	
	public void deleteDisplayName(PlayerKey playerKey) {
		displayNameTable.delete("where player_id = " + playerKey);
	}
	
	@SneakyThrows(SQLException.class)
	public boolean hasDisplayName(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = displayNameTable.select("player_id", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public String getDisplayName(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = displayNameTable.select("*", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next() ? result.getString("display_name") : null;
	}

	//

	public void addNewPlayerHandle(PlayerKey playerKey) {
		NewPlayerEventHandleTable.insert(playerKey.getId());
	}

	public void deleteNewPlayerHandle(PlayerKey playerKey) {
		NewPlayerEventHandleTable.delete("where player_id = " + playerKey);
	}

	@SneakyThrows(SQLException.class)
	public boolean existsNewPlayerHandle(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = NewPlayerEventHandleTable.select("*", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();

		return result.next();
	}
	
}