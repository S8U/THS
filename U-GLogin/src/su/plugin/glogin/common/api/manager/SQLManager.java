package su.plugin.glogin.common.api.manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import su.plugin.glogin.common.api.GLoginAPI;
import su.plugin.glogin.common.api.category.Type;
import su.plugin.glogin.common.api.object.Account;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.platform.PlatformType;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;

public class SQLManager extends SQLManagerBase {

	@Getter
	private SQLTable accountTable, loginLogTable, manageLogTable;
	
	@Setter
	@Getter
	private boolean useLog;
	
	@Override
	public void createTable() {
		accountTable = new SQLTable(this ,"Account",
				"player_id int primary key, ip varchar(15), password varchar(255), register_time bigint, last_login_time bigint, last_logout_time bigint, login tinyint(1)").createTable();
		
		loginLogTable = new SQLTable(this ,"Log_Login",
				"id int not null auto_increment primary key, player_id int, ip varchar(15), type varchar(20), time bigint").createTable();
		
		manageLogTable = new SQLTable(this ,"Log_Manage",
				"id int not null auto_increment primary key, target_id int, admin_id int, admin_ip varchar(15), type varchar(20), time bigint").createTable();
	}
	
	@Override
	public void createJsonConfigOthers() {
		if(Core.getPlatformType() == PlatformType.BUKKIT) return;
		
		getJsonConfig().set("로그 사용", true);
	}
	
	@Override
	public void loadJsonConfigOthers() {
		useLog = getJsonConfig().getBoolean("로그 사용");
	}
	
	@SneakyThrows(SQLException.class)
	public boolean hasAccount(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = accountTable.select("player_id", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	public boolean hasAccount(String name) {
		return hasAccount(PlayerKey.getPlayerKey(name));
	}
	
	@SneakyThrows(SQLException.class)
	public boolean hasAccountIp(String ip) {
		@Cleanup PreparedStatement state = accountTable.select("player_id", "where ip = '" + ip + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next();
	}
	
	@SneakyThrows(SQLException.class)
	public Account getAccount(PlayerKey playerKey) {
		@Cleanup PreparedStatement state = accountTable.select("*", "where player_id = " + playerKey);
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return null;
		
		Account account = GLoginAPI.getAccountManager().getAccount(playerKey);
		if(account == null) {
			account = new Account(playerKey);
		}
		
		account.setName(playerKey.getName());
		account.setIp(result.getString("ip"));
		account.setPassword(result.getString("password"));
		account.setRegisterTime(result.getLong("register_time"));
		account.setLastLogin(result.getLong("last_login_time"));
		account.setLastLogout(result.getLong("last_logout_time"));
		account.setLogin(result.getInt("login") == 1);
		
		return account;
	}
	
	public Account getAccount(String name) {
		return getAccount(PlayerKey.getPlayerKey(name));
	}
	
	@SneakyThrows(SQLException.class)
	public List<Account> getAccounts(String ip) {
		List<Account> accounts = new ArrayList<>();
		
		@Cleanup PreparedStatement state = accountTable.select("*", "where ip = '" + ip + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		while(result.next()) {
			PlayerKey playerKey = PlayerKey.getPlayerKey(result.getInt("player_id"));
			
			Account account = GLoginAPI.getAccountManager().getAccount(playerKey);
			if(account == null) {
				account = new Account(playerKey);
			}
			
			account.setName(playerKey.getName());
			account.setIp(result.getString("ip"));
			account.setPassword(result.getString("password"));
			account.setRegisterTime(result.getLong("register_time"));
			account.setLastLogin(result.getLong("last_login_time"));
			account.setLastLogout(result.getLong("last_logout_time"));
			account.setLogin(result.getInt("login") == 1);
			
			accounts.add(account);
		}
		
		return accounts;
	}
	
	public void clearLogin() {
		accountTable.update("login = 0", "where login = 1");
	}
	
	public void saveAccount(Account account) {
		accountTable.insertDuplicate(account.getPlayerKey(), account.getIp(), account.getPassword(), account.getRegisterTime(), account.getLastLogin(), account.getLastLogout(), account.isLogin());
	}
	
	public void deleteAccount(PlayerKey playerKey) {
		accountTable.delete("where player_id = " + playerKey);
	}
	
	public void deleteAccount(Account account) {
		deleteAccount(account.getPlayerKey());
	}
	
	public void writeLoginLog(PlayerKey playerKey, String ip, Type type) {
		if(!useLog) return;
		loginLogTable.insert(null, playerKey.getId(), ip, type.getText(), System.currentTimeMillis());
	}
	
	public void writeManageLog(PlayerKey targetPlayerKey, int adminId, String adminIp, Type type) {
		if(!useLog) return;
		manageLogTable.insert(null, targetPlayerKey.getId(), adminId, adminIp, type.getText(), System.currentTimeMillis());
	}
	
}