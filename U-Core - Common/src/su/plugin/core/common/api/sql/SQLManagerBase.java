package su.plugin.core.common.api.sql;

import java.io.File;
import java.io.IOException;
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

	//SQLite
	@Setter
	private String SQLiteFileName = "storage.db";

	// MySQL
	@Setter
	private String SQLAddress, SQLDatabase, SQLUser, SQLPassword, SQLTablePrefix, configName = "sql-config";
	
	@Setter
	private int SQLPort;


	@Setter
	private boolean use, useUseOption;
	
	private JsonConfig jsonConfig;

	private SQLConfig SQLConfig;

	@Setter
	private SQLType SQLType = su.plugin.core.common.api.sql.SQLType.SQLite;

	private Connection connection;

	@SneakyThrows(IOException.class)
	public void loadJsonConfig(UPlugin plugin) {
		createJsonConfig(plugin);

		if(useUseOption) {
			use = jsonConfig.getBoolean("사용");
		}

		SQLType = su.plugin.core.common.api.sql.SQLType.valueOf(jsonConfig.getString("타입"));

		// SQLite
		SQLiteFileName = jsonConfig.getString("SQLite.파일 이름");
		new File(plugin.getDataFolder().getPath() + "\\" + SQLiteFileName).createNewFile();

		if (SQLType == su.plugin.core.common.api.sql.SQLType.MySQL) {
			// MySQL
			SQLAddress = jsonConfig.getString("MySQL.주소");
			SQLPort = jsonConfig.getInt("MySQL.포트");
			SQLDatabase = jsonConfig.getString("MySQL.데이터베이스");
			SQLUser = jsonConfig.getString("MySQL.유저");
			SQLPassword = jsonConfig.getString("MySQL.비밀번호");
			SQLTablePrefix = jsonConfig.getString("MySQL.테이블 접두사");
		} else if (SQLType == su.plugin.core.common.api.sql.SQLType.MariaDB) {
			// MariaDB
			SQLAddress = jsonConfig.getString("MariaDB.주소");
			SQLPort = jsonConfig.getInt("MariaDB.포트");
			SQLDatabase = jsonConfig.getString("MariaDB.데이터베이스");
			SQLUser = jsonConfig.getString("MariaDB.유저");
			SQLPassword = jsonConfig.getString("MariaDB.비밀번호");
			SQLTablePrefix = jsonConfig.getString("MariaDB.테이블 접두사");
		}

		loadJsonConfigOthers();
		
		Core.log(getClass().getSimpleName() + ": SQL 설정을 불러왔습니다.");
	}

	protected void createJsonConfig(UPlugin plugin) {
		jsonConfig = new JsonConfig(new File(plugin.getDataFolder(), configName + ".json")).load();

		transformOldConfig(plugin);

		if(useUseOption) {
			jsonConfig.addDefault("사용", false);
		}

		jsonConfig.addDefault("타입", SQLType.name());

		// SQLite
		jsonConfig.addDefault("SQLite.파일 이름", SQLiteFileName);

		// MySQL
		jsonConfig.addDefault("MySQL.주소", "localhost");
		jsonConfig.addDefault("MySQL.포트", 3306);
		jsonConfig.addDefault("MySQL.데이터베이스", "database");
		jsonConfig.addDefault("MySQL.유저", "root");
		jsonConfig.addDefault("MySQL.비밀번호", "password");
		jsonConfig.addDefault("MySQL.테이블 접두사", "u_");

		// MariaDB
		jsonConfig.addDefault("MariaDB.주소", "localhost");
		jsonConfig.addDefault("MariaDB.포트", 3307);
		jsonConfig.addDefault("MariaDB.데이터베이스", "database");
		jsonConfig.addDefault("MariaDB.유저", "root");
		jsonConfig.addDefault("MariaDB.비밀번호", "password");
		jsonConfig.addDefault("MariaDB.테이블 접두사", "u_");

		createJsonConfigOthers();
		
		jsonConfig.save();
	}

	private void transformOldConfig(UPlugin plugin) {
		File oldFile = new File(plugin.getDataFolder(), configName.replace("sql", "mysql") + ".json");
		if (!oldFile.exists()) return;

		JsonConfig oldConfig = new JsonConfig(oldFile);
		oldConfig.load();

		String address = oldConfig.getString("주소");
		if (address == null) return;

		int port = oldConfig.getInt("포트");
		String database = oldConfig.getString("데이터베이스");
		String user = oldConfig.getString("유저");
		String password = oldConfig.getString("비밀번호");
		String tablePrefix = oldConfig.getString("테이블 접두사");

		oldConfig.getDefaults().forEach((k, v) -> {
			if (k.equals("주소") ||
					k.equals("포트") ||
					k.equals("데이터베이스") ||
					k.equals("유저") ||
					k.equals("비밀번호") ||
					k.equals("테이블 접두사")
			) return;

			jsonConfig.addDefault(k, v);
		});

		oldConfig.getValues().forEach((k, v) -> {
			if (k.equals("주소") ||
					k.equals("포트") ||
					k.equals("데이터베이스") ||
					k.equals("유저") ||
					k.equals("비밀번호") ||
					k.equals("테이블 접두사")
			) return;

			jsonConfig.set(k, v);
		});

		jsonConfig.set("타입","MySQL");
		jsonConfig.set("MySQL.주소", address);
		jsonConfig.set("MySQL.포트", port);
		jsonConfig.set("MySQL.데이터베이스", database);
		jsonConfig.set("MySQL.유저", user);
		jsonConfig.set("MySQL.비밀번호", password);
		jsonConfig.set("MySQL.테이블 접두사", tablePrefix);

		transformOldConfigOthers();

		jsonConfig.save();

		oldFile.delete();

		Core.log("오래된 SQL 설정을 변환했습니다.");
	}

	public void loadJsonConfigOthers() { }
	public void createJsonConfigOthers() { }
	protected void transformOldConfigOthers() { }
	
	public boolean connect(UPlugin plugin) {
		try {
			loadJsonConfig(plugin);
			if(useUseOption && !use) return false;

			switch (SQLType) {
				case SQLite:
					connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getPath() + "\\" + SQLiteFileName);
					break;
				case MySQL:
					connection = DriverManager.getConnection("jdbc:mysql://" + SQLAddress + ":" + SQLPort + "/" + SQLDatabase + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true", SQLUser, SQLPassword);
					break;
				case MariaDB:
					connection = DriverManager.getConnection("jdbc:mariadb://" + SQLAddress + ":" + SQLPort + "/" + SQLDatabase + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true", SQLUser, SQLPassword);
					break;
			}

			SQLConfig = new SQLConfig(this);
			createTable();

			Core.log(getClass().getSimpleName() + ": " + SQLType.name() + "에 연결되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
			
			Core.log(getClass().getSimpleName() + ": " + SQLType.name() + "에 연결할 수 없습니다.");
			return false;
		}

		onConnected();

		return true;
	}
	
	public void close() {
		try {
			if(connection == null) return;
			
			connection.close();
			
			Core.log(getClass().getSimpleName() + ": " + SQLType.name() + "과의 연결을 종료했습니다.");
		} catch(Exception e) {
			e.printStackTrace();
			
			Core.log(getClass().getSimpleName() + ": " + SQLType.name() + "과의 연결을 종료하는 중 오류가 발생했습니다.");
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