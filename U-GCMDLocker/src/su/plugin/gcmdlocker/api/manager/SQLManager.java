package su.plugin.gcmdlocker.api.manager;

import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;

public class SQLManager extends SQLManagerBase {
	
	private SQLTable loginLogTable;

	public SQLManager() {
		setUseUseOption(true);
	}

	public void createTable() {
		loginLogTable = new SQLTable(this, "Log_Login", "player_id int, ip varchar(15), type varchar(6), time bigint").createTable();
	}
	
	public void writeLog(PlayerKey playerKey, String ip, String type) {
		if(!isUse()) return;

		loginLogTable.insert(playerKey, ip, type, System.currentTimeMillis());
	}
	
}