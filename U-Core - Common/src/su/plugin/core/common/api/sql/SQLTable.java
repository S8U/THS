package su.plugin.core.common.api.sql;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import su.plugin.core.common.api.util.StringUtil;

@Getter
public class SQLTable {
	
	@NonNull
	private final SQLManagerBase SQLManager;
	
	@NonNull
	private final String name, columnString;
	
	private final List<SQLColumn> columns;
	
	public SQLTable(SQLManagerBase SQLManager, String name, String column) {
		this.SQLManager = SQLManager;
		
		this.name = name;
		this.columnString = column;
		columns = new ArrayList<>();
		
		String[] cs = column.split(",");
		for(int i = 0; i < cs.length; i++) {
			String c = cs[i].trim();
			
			if(c.toLowerCase().startsWith("primary key")) {
				String keyStr = c.substring("primary key(".length(), c.length() - 1);
				
				for(String key : keyStr.split(",")) {
					SQLColumn cm = getColumn(key.trim());
					if(cm == null) continue;
					
					cm.setPrimaryKey(true);
				}
				
				break;
			}
			
			String[] ccs = c.split(" ");
			
			String cName = ccs[0];
			String cType = ccs[1];
			boolean notNull = c.toLowerCase().contains("not null");
			boolean autoIncrement = c.toLowerCase().contains("auto_increment");
			boolean primaryKey = c.toLowerCase().endsWith("primary key");
			
			SQLColumn sc = new SQLColumn(cName, cType, notNull, autoIncrement, primaryKey);
			columns.add(sc);
		}
	}
	
	public SQLColumn getColumn(String name) {
		for(SQLColumn ac : columns) {
			if(ac.getName().equalsIgnoreCase(name)) return ac;
		}
		
		return null;
	}
	
	public String getTableName() {
		return SQLManager.getSQLTablePrefix() + name;
	}
	
	public SQLTable createTable() {
		SQLManager.createTable(name, columnString);
		return this;
	}
	
	public void deleteTable() {
		SQLManager.deleteTable(name);
	}
	
	public void truncateTable() {
		SQLManager.truncateTable(name);
	}
	
	public void insert(Object...values) {
		for (int i = 0; i < values.length; i++) {
			if(values[i] == null) {
				values[i] = "null";
			} else if(values[i].toString().startsWith("$$")) {
				values[i] = values[i].toString().substring(2);
			} else if(!(values[i] instanceof String)) continue;
			values[i] = "'" + values[i] + "'";
		}
		
		SQLManager.update("insert into " + getTableName() + " values (" + StringUtil.connectString(values, ",") + ")");
	}
	
	public void insertIgnore(Object...values) {
		for (int i = 0; i < values.length; i++) {
			if(values[i] == null) {
				values[i] = "null";
			} else if(values[i].toString().startsWith("$$")) {
				values[i] = values[i].toString().substring(2);
			} else if(!(values[i] instanceof String)) continue;
			values[i] = "'" + values[i] + "'";
		}
		
		SQLManager.update("insert ignore into " + getTableName() + " values (" + StringUtil.connectString(values, ",") + ")");
	}
	
	public void insertDuplicate(Object...values) {
		for (int i = 0; i < values.length; i++) {
			if(values[i] == null) {
				values[i] = "null";
			} else if(values[i] instanceof String && !values[i].toString().startsWith("$$")) {
				values[i] = "'" + values[i] + "'";
			} else if(values[i] instanceof Boolean) {
				values[i] = (boolean) values[i] ? 1 : 0;
			}
		}
		
		List<String> ds = new ArrayList<>();
		for (int i = 0; i < columns.size(); i++) {
			SQLColumn c = columns.get(i);
			if(c.isPrimaryKey() || c.isAutoIncrement()) continue;
			
			String v = "=" + (values[i].toString().startsWith("$$") ? c.getName() : values[i].toString());
			
			ds.add(c.getName() + v);
		}
		
		for (int i = 0; i < values.length; i++) {
			if(!values[i].toString().startsWith("$$")) continue;
			values[i] = values[i].toString().substring(2);
		}
		
		SQLManager.update("insert into " + getTableName() + " values (" + StringUtil.connectString(values, ",") + ")"
				+ " on duplicate key update " + StringUtil.connectString(ds, ","));
	}
	
	public void update(String set) {
		SQLManager.update("update " + getTableName() + " set " + set);
	}
	
	public void update(String set, String sql) {
		SQLManager.update("update " + getTableName() + " set " + set + " " + sql);
	}
	
	public void delete() {
		SQLManager.update("delete from " + getTableName());
	}
	
	public void delete(String sql) {
		SQLManager.update("delete from " + getTableName() + " " + sql);
	}
	
	public PreparedStatement select(String selected) {
		return SQLManager.getPreparedStatement("select " + selected + " from " + getTableName());
	}
	
	public PreparedStatement select(String selected, String sql) {
		return SQLManager.getPreparedStatement("select " + selected + " from " + getTableName() + " " + sql);
	}
	
}