package su.plugin.gcculogger.api.manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.Cleanup;
import lombok.SneakyThrows;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;
import su.plugin.core.common.api.util.StringUtil;

public class SQLManager extends SQLManagerBase {
	
	private SQLTable CCULogTable, maxCCULogTable, newPlayerCountTable;
	
	@Override
	public void createTable() {
		CCULogTable = new SQLTable(this, "Log_CCU", "time varchar(19), CCU int").createTable();
		maxCCULogTable = new SQLTable(this, "Log_MaxCCU", "date varchar(19) primary key, time varchar(19), CCU int").createTable();

		newPlayerCountTable = new SQLTable(this, "Log_NewPlayerCount", "time varchar(19) primary key, count int").createTable();
	}
	
	public void writeCCULog(int CCU) {
		CCULogTable.insert(StringUtil.buildDateString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"), CCU);
	}
	
	public void writeMaxCCULog(int CCU) {
		maxCCULogTable.insertDuplicate(StringUtil.buildDateString(System.currentTimeMillis(), "yyyy-MM-dd"), StringUtil.buildDateString(System.currentTimeMillis(), "HH:mm:ss"), CCU);
	}
	
	@SneakyThrows(SQLException.class)
	public int getMaxCCU() {
		@Cleanup PreparedStatement state = maxCCULogTable.select("*", "where time like '" + StringUtil.buildDateString(System.currentTimeMillis(), "yyyy-MM-dd") + "%'");
		@Cleanup ResultSet result = state.executeQuery();
		
		return result.next() ? result.getInt("CCU") : 0;
	}

	public void addNewPlayerCount() {
		newPlayerCountTable.insertDuplicate(StringUtil.buildDateString(System.currentTimeMillis(), "yyyy-MM-dd"), getNewPlayerCount() + 1);
	}

	@SneakyThrows(SQLException.class)
	public int getNewPlayerCount() {
		@Cleanup PreparedStatement state = newPlayerCountTable.select("*", "where time like '" + StringUtil.buildDateString(System.currentTimeMillis(), "yyyy-MM-dd") + "'");
		@Cleanup ResultSet result = state.executeQuery();

		return result.next() ? result.getInt("count") : 0;
	}
	
}