package su.plugin.core.common.api.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.config.json.JsonConfig;
import su.plugin.core.common.api.plugin.UPlugin;
import su.plugin.core.common.api.util.DebugUtil;

@Getter
public class SQLManagerBase {
	
	@Setter
	private String SQLAddress, SQLDatabase, SQLUser, SQLPassword, SQLTablePrefix, configName = "mysql-config";
	
	@Setter
	private int SQLPort;
	
	@Setter
	private boolean use, useUseOption;
	
	private JsonConfig jsonConfig;
	
	private SQLConfig SQLConfig;

	private Connection connection;
	
	public void loadJsonConfig(UPlugin plugin) {
		createJsonConfig(plugin);
		
		if(useUseOption) {
			use = jsonConfig.getBoolean("사용");
		}
		SQLAddress = jsonConfig.getString("주소");
		SQLPort = jsonConfig.getInt("포트");
		SQLDatabase = jsonConfig.getString("데이터베이스");
		SQLUser = jsonConfig.getString("유저");
		SQLPassword = jsonConfig.getString("비밀번호");
		SQLTablePrefix = jsonConfig.getString("테이블 접두사");
		loadJsonConfigOthers();
		
		Core.log(getClass().getSimpleName() + ": MySQL 설정을 불러왔습니다.");
	}
	
	protected void createJsonConfig(UPlugin plugin) {
		jsonConfig = new JsonConfig(new File(plugin.getDataFolder(), configName + ".json")).load();
		
		if(useUseOption) {
			jsonConfig.addDefault("사용", false);
		}
		jsonConfig.addDefault("주소", "localhost");
		jsonConfig.addDefault("포트", 3306);
		jsonConfig.addDefault("데이터베이스", "database");
		jsonConfig.addDefault("유저", "root");
		jsonConfig.addDefault("비밀번호", "password");
		jsonConfig.addDefault("테이블 접두사", "u_");
		createJsonConfigOthers();
		
		jsonConfig.save();
	}
	
	public void loadJsonConfigOthers() { }
	public void createJsonConfigOthers() { }
	
	public boolean connect(UPlugin plugin) {
		try {
			loadJsonConfig(plugin);
			if(useUseOption && !use) return false;

			connection = DriverManager.getConnection("jdbc:mysql://" + SQLAddress + ":" + SQLPort + "/" + SQLDatabase + "?autoReconnect=true", SQLUser, SQLPassword);
			SQLConfig = new SQLConfig(this);
			createTable();

			Core.log(getClass().getSimpleName() + ": MySQL에 접속되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
			
			Core.log(getClass().getSimpleName() + ": MySQL에 연결할 수 없습니다.");
			return false;
		}

		onConnected();

		return true;
	}
	
	public void close() {
		try {
			if(connection == null) return;
			
			connection.close();
			
			Core.log(getClass().getSimpleName() + ": MySQL과의 연결을 종료했습니다.");
		} catch(Exception e) {
			e.printStackTrace();
			
			Core.log(getClass().getSimpleName() + ": MySQL과의 연결을 종료하는 중 오류가 발생했습니다.");
		}
	}

	public void onConnected() { }

	public boolean isConnected() {
		return connection != null;
	}
	
	@SneakyThrows(SQLException.class)
	public void update(String sql) {
		sql = sql.replace("\\", "\\\\");

		DebugUtil.log("SQL Update: " + sql);

		@Cleanup PreparedStatement state = connection.prepareStatement(sql);
		state.executeUpdate();
	}
	
	public void updatef(String sql, Object...args) {
		update(String.format(sql, args));
	}
	
	@SneakyThrows(SQLException.class)
	public PreparedStatement getPreparedStatement(String sql) {
		DebugUtil.log("SQL PreparedStatement: " + sql);
		return connection.prepareStatement(sql);
	}
	
	public PreparedStatement getPreparedStatementf(String sql, Object...args) {
		return getPreparedStatement(String.format(sql, args));
	}
	
	public String getTableName(String tableName) {
		return SQLTablePrefix + tableName;
	}
	
	public void createTable() { }
	
	public void createTable(String name, String calumn) {
		update("create table if not exists " + getTableName(name) + " (" + calumn + ")");
	}
	
	public void deleteTable(String name) {
		update("drop table " + getTableName(name));
	}
	
	public void truncateTable(String name) {
		update("truncate table " + getTableName(name));
	}
	
}