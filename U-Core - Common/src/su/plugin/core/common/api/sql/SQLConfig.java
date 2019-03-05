package su.plugin.core.common.api.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class SQLConfig {
	
	@Getter
	private final SQLManagerBase SQLManager;
	
	@Getter
	private SQLTable configTable;
	
	public void createTable() {
		configTable = new SQLTable(SQLManager, "Config", "name varchar(255) primary key, value text").createTable();
	}
	
	public void set(String name, Object value) {
		if(value == null) {
			delete(name);
			return;
		}
		
		configTable.insertDuplicate(name, value);
	}
	
	public void delete(String name) {
		configTable.delete("where name='" + name + "'");
	}
	
	public void deleteAll() {
		configTable.delete();
	}
	
	@SneakyThrows(SQLException.class)
	public Object get(String name) {
		@Cleanup PreparedStatement state = configTable.select("value", "where name='" + name + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return null;
		
		Object obj = result.getObject("value");
		
		return obj;
	}
	
	public String getString(String name) {
		Object d = get(name);
		
		if(d == null) return null;
		
		return String.valueOf(d);
	}
	
	public boolean getBoolean(String name) {
		String d = getString(name);
		
		if(d == null) return false;
		
		return Boolean.valueOf(d);
	}
	
	public int getInt(String name) {
		String d = getString(name);
		
		if(d == null) return 0;
		
		return Integer.valueOf(d);
	}
	
	public float getFloat(String name) {
		String d = getString(name);
		
		if(d == null) return 0;
		
		return Float.valueOf(d);
	}
	
	public double getDouble(String name) {
		String d = getString(name);
		
		if(d == null) return 0;
		
		return Double.valueOf(d);
	}
	
}
