package su.plugin.gessentials.bungee.api.manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.category.Allow;
import su.plugin.gessentials.bungee.api.object.EMute;
import su.plugin.gessentials.bungee.api.object.EPlayer;
import su.plugin.gessentials.bungee.api.object.ban.EIpBan;
import su.plugin.gessentials.bungee.api.object.ban.EPlayerKeyBan;

public class SQLManager extends SQLManagerBase {
	
	@Getter
	private SQLTable playerTable, playerWarningTable, banTable, ipBanTable, muteTable,
	kickLogTable, banLogTable, ipBanLogTable, unBanLogTable, unIPBanLogTable, muteLogTable, unMuteLogTable;
	
	@Override
	public void createTable() {
		playerTable = new SQLTable(this, "Player",
				"player_id int primary key, ip varchar(15), last_login bigint, last_logout bigint").createTable();
		
		playerWarningTable = new SQLTable(this,"Player_Warning",
				"player_id int primary key, count int").createTable();
		
		//
		
		banTable = new SQLTable(this, "Ban",
				"player_id int primary key, admin_id int, time bigint, duration bigint, reason varchar(255)").createTable();
		
		ipBanTable = new SQLTable(this, "Ban_IP",
				"ip varchar(15) primary key, admin_id int, time bigint, duration bigint, reason varchar(255)").createTable();

		muteTable = new SQLTable(this, "Mute",
				"player_id int primary key, admin_id int, time bigint, duration bigint, reason varchar(255)").createTable();
		
		//
		
		kickLogTable = new SQLTable(this, "Log_Kick",
				"id int not null auto_increment primary key, player_id int, admin_id int, time bigint, reason varchar(255)").createTable();
		
		banLogTable = new SQLTable(this, "Log_Ban",
				"id int not null auto_increment primary key, player_id int, admin_id int, time bigint, duration bigint, reason varchar(255)").createTable();
		
		ipBanLogTable = new SQLTable(this, "Log_Ban_IP",
				"id int not null auto_increment primary key, ip varchar(15), admin_id int, time bigint, duration bigint, reason varchar(255)").createTable();

		unBanLogTable = new SQLTable(this, "Log_UnBan",
				"id int not null auto_increment primary key, player_id int, admin_id int, time bigint").createTable();
		
		unIPBanLogTable = new SQLTable(this, "Log_UnBan_IP",
				"id int not null auto_increment primary key, ip varchar(15), admin_id int, time bigint").createTable();

		muteLogTable = new SQLTable(this, "Log_Mute",
				"id int not null auto_increment primary key, player_id int, admin_id int, time bigint, duration bigint, reason varchar(255)").createTable();

		unMuteLogTable = new SQLTable(this, "Log_UnMute",
				"id int not null auto_increment primary key, player_id int, admin_id int, time bigint").createTable();
		
		getSQLConfig().createTable();
	}
	
	// PLAYER SAVE
	
	public void savePlayer(EPlayer ep) {
		playerTable.insertDuplicate(ep.getPlayerKey(), ep.getIp(), ep.getLastLogin(), ep.getLastLogout());
	}
	
	public void saveWarning(PlayerKey playerKey, int count) {
		if(count < 1) return;
		playerWarningTable.insertDuplicate(playerKey, count);
	}
	
	public void initWarning() {
		playerWarningTable.truncateTable();
	}
	
	// PLAYER LOAD
	
	@SneakyThrows(SQLException.class)
	public boolean existsEPlayer(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = playerTable.select("player_id", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public EPlayer getEPlayer(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = playerTable.select("*", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return null;
		
		EPlayer ep = new EPlayer(playerKey);
		ep.setName(playerKey.getName());
		ep.setIp(result.getString("ip"));
		ep.setLastLogin(result.getLong("last_login"));
		ep.setLastLogout(result.getLong("last_logout"));

		ep.setMute(getEMute(playerKey));

		/*Object chatSpy = Core.getOptionSQLManager().getPlayerOption(playerKey, "gessentials_chat_spy");
		if(chatSpy != null) {
			ep.setChatSpy((boolean) chatSpy);
		}

		Object moveSpy = Core.getOptionSQLManager().getPlayerOption(playerKey, "gessentials_move_spy");
		if(moveSpy != null) {
			ep.setMoveSpy((boolean) moveSpy);
		}*/

		ep.setWarning(getWarning(playerKey));
		
		Object chatIgnore = Core.getOptionSQLManager().getPlayerOption(playerKey, "gessentials_chat_ignore");
		if(chatIgnore != null) {
			((List<Double>) chatIgnore).forEach(id -> ep.getChatIgnoreList().add(PlayerKey.getPlayerKey(id.intValue())));
		}

		Object whisperIgnore = Core.getOptionSQLManager().getPlayerOption(playerKey, "gessentials_whisper_ignore");
		if(whisperIgnore != null) {
			((List<Double>) whisperIgnore).forEach(id -> ep.getWhisperIgnoreList().add(PlayerKey.getPlayerKey(id.intValue())));
		}

		Object ignoreAllChat = Core.getOptionSQLManager().getPlayerOption(playerKey, "gessentials_chat_ignore_all");
		if(ignoreAllChat != null) {
			ep.setIgnoreAllChat(true);
		}

		Object ignoreAllWhisper = Core.getOptionSQLManager().getPlayerOption(playerKey, "gessentials_allow_whisper");
		if(ignoreAllWhisper != null) {
			ep.setWhisperAllow(ignoreAllWhisper.equals("friend") ? Allow.FRIEND : Allow.BLOCK);
		}

		return ep;
	}
	
	public EPlayer getEPlayerByName(String name) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(name);
		if(playerKey == null) return null;
		
		return getEPlayer(playerKey);
	}
	
	@SneakyThrows(SQLException.class)
	public int getWarning(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = playerWarningTable.select("count", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next() ? result.getInt("count") : 0;
	}
	
	public String getEPlayerIp(String name) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(name);
		if(playerKey == null) return null;
		
		return getEPlayerIp(playerKey);
	}
	
	@SneakyThrows(SQLException.class)
	public String getEPlayerIp(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = playerTable.select("ip", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next() ? result.getString("ip") : null;
	}

	@SneakyThrows(SQLException.class)
	public EMute getEMute(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = muteTable.select("*", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();

		return result.next() ? new EMute(playerKey, result.getInt("admin_id"), result.getLong("time"), result.getLong("duration"), result.getString("reason")) : null;
	}
	
	@SneakyThrows(SQLException.class)
	public void loadAllEPlayer() {
		@Cleanup PreparedStatement state = playerTable.select("*");
		@Cleanup ResultSet result = state.executeQuery();
		
		while(result.next()) {
			PlayerKey playerKey = PlayerKey.getPlayerKey(result.getInt("player_id"));
			
			EPlayer ep = new EPlayer(playerKey);
			ep.setName(playerKey.getName());
			ep.setIp(result.getString("ip"));
			ep.setLastLogin(result.getLong("last_login"));
			ep.setLastLogout(result.getLong("last_logout"));

			ep.setMute(getEMute(playerKey));

		/*	Object chatSpy = Core.getOptionSQLManager().getPlayerOption(playerKey, "gessentials_chat_spy");
			if(chatSpy != null) {
				ep.setChatSpy((boolean) chatSpy);
			}
			
			Object moveSpy = Core.getOptionSQLManager().getPlayerOption(playerKey, "gessentials_move_spy");
			if(moveSpy != null) {
				ep.setMoveSpy((boolean) moveSpy);
			}*/
			
			ep.setWarning(getWarning(playerKey));
			
			Object chatIgnore = Core.getOptionSQLManager().getPlayerOption(playerKey, "gessentials_chat_ignore");
			if(chatIgnore != null) {
				((List<Integer>) chatIgnore).forEach(id -> ep.getChatIgnoreList().add(PlayerKey.getPlayerKey(id)));
			}

			Object ignoreAllChat = Core.getOptionSQLManager().getPlayerOption(playerKey, "gessentials_chat_ignore_all");
			if(ignoreAllChat != null) {
				ep.setIgnoreAllChat(true);
			}

			Object whisperIgnore = Core.getOptionSQLManager().getPlayerOption(playerKey, "gessentials_whisper_ignore");
			if(whisperIgnore != null) {
				((List<Integer>) whisperIgnore).forEach(id -> ep.getWhisperIgnoreList().add(PlayerKey.getPlayerKey(id)));
			}

			Object ignoreAllWhisper = Core.getOptionSQLManager().getPlayerOption(playerKey, "gessentials_allow_whisper");
			if(ignoreAllWhisper != null) {
				ep.setWhisperAllow(ignoreAllWhisper.equals("friend") ? Allow.FRIEND : Allow.BLOCK);
			}

			GGEssentialsAPI.getPlayerManager().setEPlayer(ep);
		}
	}
	
	// BAN SAVE
	
	public void savePlayerKeyBanData(EPlayerKeyBan ban) {
		banTable.insert(ban.getPlayerKey(), ban.getAdminId(), ban.getTime(), ban.getDuration(), ban.getReason());
	}
	
	public void saveIpBanData(EIpBan ban) {
		ipBanTable.insert(ban.getIp(), ban.getAdminId(), ban.getTime(), ban.getDuration(), ban.getReason());
	}
	
	public void deletePlayerKeyBanData(PlayerKey playerKey) {
		banTable.delete("where player_id = " + playerKey);
	}

	public void deleteIpBanData(String ip) {
		ipBanTable.delete("where ip = '" + ip + "'");
	}

	public void saveEMute(PlayerKey playerKey, EMute mute) {
		muteTable.insertDuplicate(playerKey, mute.getAdminId(), mute.getTime(), mute.getDuration(), mute.getReason());
	}

	public void deleteEMute(PlayerKey playerKey) {
		muteTable.delete("where player_id = " + playerKey);
	}
	
	// BAN LOAD
	
	@SneakyThrows(SQLException.class)
	public boolean hasPlayerKeyBanData(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = banTable.select("player_id", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public EPlayerKeyBan getPlayerKeyBanData(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = banTable.select("*", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return null;
		
		return new EPlayerKeyBan(playerKey, result.getInt("admin_id"), result.getLong("time"), result.getLong("duration"), result.getString("reason"));
	}
	
	@SneakyThrows(SQLException.class)
	public boolean hasIpBanData(String ip) {
		@Cleanup PreparedStatement state = ipBanTable.select("ip", "where ip='" + ip + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public EIpBan getIpBanData(String ip) {
		@Cleanup PreparedStatement state = ipBanTable.select("*", "where ip='" + ip + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return null;
		
		return new EIpBan(ip, result.getInt("admin_id"), result.getLong("time"), result.getLong("duration"), result.getString("reason"));
	}
	
	@SneakyThrows(SQLException.class)
	public void loadAllPlayerKeyBanData() {
		@Cleanup PreparedStatement state = banTable.select("*");
		@Cleanup ResultSet result = state.executeQuery();
		
		while(result.next()) {
			PlayerKey playerKey = PlayerKey.getPlayerKey(result.getInt("player_id"));
			GGEssentialsAPI
					.getBanManager().setBanData(playerKey.getId() + "", new EPlayerKeyBan(playerKey, result.getInt("admin_id"), result.getLong("time"), result.getLong("duration"), result.getString("reason")));
		}
	}
	
	@SneakyThrows(SQLException.class)
	public void loadAllIpBanData() {
		@Cleanup PreparedStatement state = ipBanTable.select("*");
		@Cleanup ResultSet result = state.executeQuery();
		
		while(result.next()) {
			String ip = result.getString("ip");
			GGEssentialsAPI
					.getBanManager().setBanData(ip, new EIpBan(ip, result.getInt("admin_id"), result.getLong("time"), result.getLong("duration"), result.getString("reason")));
		}
	}
	
	// WRITE LOG
	
	public void writeKickLog(PlayerKey playerKey, int adminId, String reason) {
		kickLogTable.insertDuplicate(null, playerKey, adminId, System.currentTimeMillis(), reason);
	}
	
	public void writeBanLog(EPlayerKeyBan ban) {
		banLogTable.insertDuplicate(null, ban.getPlayerKey(), ban.getAdminId(), ban.getTime(), ban.getDuration(), ban.getReason());
	}
	
	public void writeIpBanLog(EIpBan ban) {
		ipBanLogTable.insertDuplicate(null, ban.getIp(), ban.getAdminId(), ban.getTime(), ban.getDuration(), ban.getReason());
	}
	
	public void writeUnBanLog(PlayerKey playerKey, int adminId) {
		unBanLogTable.insertDuplicate(null, playerKey, adminId, System.currentTimeMillis());
	}
	
	public void writeUnIpBanLog(String ip, int adminId) {
		unIPBanLogTable.insertDuplicate(null, ip, adminId, System.currentTimeMillis());
	}

	public void writeMuteLog(PlayerKey playerKey, EMute mute) {
		muteLogTable.insertDuplicate(null, playerKey, mute.getAdminId(), mute.getTime(), mute.getDuration(), mute.getReason());
	}

	public void writeUnMuteLog(PlayerKey playerKey, int adminId) {
		unMuteLogTable.insertDuplicate(null, playerKey, adminId, System.currentTimeMillis());
	}
	
}