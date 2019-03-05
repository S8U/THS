package su.plugin.pvpstats.api.manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;
import su.plugin.pvpstats.api.PVPStatsAPI;
import su.plugin.pvpstats.api.object.PSPlayer;
import su.plugin.pvpstats.api.object.Stats;

public class SQLManager extends SQLManagerBase {
	
	@Getter
	private SQLTable dailyStatsTable, weeklyStatsTable, monthlyStatsTable, allStatsTable, PVPLogTable, dailyRankingPrefixTable, weeklyRankingPrefixTable, monthlyRankingPrefixTable, allRankingPrefixTable;

	@Getter
	private boolean initTable;

	@Setter
	@Getter
	private Date lastInit;

	@Override
	public void createTable() {
		String statsColumn = "player_id int primary key, "
				+ "kill_count int not null default 0, "
				+ "death_count int not null default 0, "
				+ "assist_count int not null default 0, "
				+ "win_count int not null default 0, "
				+ "quit_count int not null default 0, "
				+ "kill_streak int not null default 0, "
				+ "death_streak int not null default 0, "
				+ "win_streak int not null default 0, "
				+ "max_kill_streak int not null default 0, "
				+ "max_death_streak int not null default 0, "
				+ "max_win_streak int not null default 0";

		dailyStatsTable = new SQLTable(this, "Stats_Day", statsColumn).createTable();
		weeklyStatsTable = new SQLTable(this, "Stats_Week", statsColumn).createTable();
		monthlyStatsTable = new SQLTable(this, "Stats_Month", statsColumn).createTable();
		allStatsTable = new SQLTable(this, "Stats_All", statsColumn).createTable();

		PVPLogTable = new SQLTable(this, "PVP_Log", "killer_id int, dead_id int, time bigint").createTable();

		if(PVPStatsAPI.isGiveDailyRankingPrefix()) {
			dailyRankingPrefixTable = new SQLTable(this, "Ranking_Prefix_Day", "player_id int, prefix varchar(255)").createTable();
		}
		if(PVPStatsAPI.isGiveWeeklyRankingPrefix()) {
			weeklyRankingPrefixTable = new SQLTable(this, "Ranking_Prefix_Week", "player_id int, prefix varchar(255)").createTable();
		}
		if(PVPStatsAPI.isGiveMonthlyRankingPrefix()) {
			monthlyRankingPrefixTable = new SQLTable(this, "Ranking_Prefix_Month", "player_id int, prefix varchar(255)").createTable();
		}
		if(PVPStatsAPI.isGiveAllRankingPrefix()) {
			allRankingPrefixTable = new SQLTable(this, "Ranking_Prefix_All", "player_id int, prefix varchar(255)").createTable();
		}

		getSQLConfig().createTable();
	}

	@Override
	public void createJsonConfigOthers() {
		getJsonConfig().addDefault("테이블 초기화", false);
	}

	@Override
	public void loadJsonConfigOthers() {
		initTable = getJsonConfig().getBoolean("테이블 초기화");
	}

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void onConnected() {
		loadSQLConfig();
	}

	public void saveSQLConfig() {
		getSQLConfig().set("last_init", format.format(lastInit));
	}

	@SneakyThrows(ParseException.class)
	public void loadSQLConfig() {
		String loadStr = getSQLConfig().getString("last_init");
		if(loadStr == null) return;

		lastInit = format.parse(loadStr);
	}

	public void updateLastInit() {
		if(lastInit == null) {
			lastInit = new Date();
			return;
		}

		lastInit.setTime(System.currentTimeMillis());

		if(!initTable) return;

		saveSQLConfig();
	}

	//
	
	public void savePlayer(PSPlayer pp) {
		pp.getDailyStats().save(dailyStatsTable);
		pp.getWeeklyStats().save(weeklyStatsTable);
		pp.getMonthlyStats().save(monthlyStatsTable);
		pp.getAllStats().save(allStatsTable);
	}
	
	//
	
	public PSPlayer loadPlayer(PlayerKey playerKey) {
		PSPlayer pp = PVPStatsAPI.getPlayerManager().getPSPlayer(playerKey);
		if(pp == null) {
			pp = new PSPlayer(playerKey);
			PVPStatsAPI.getPlayerManager().setPSPlayer(playerKey, pp);
		}

		pp.getDailyStats().load(dailyStatsTable);
		pp.getWeeklyStats().load(weeklyStatsTable);
		pp.getMonthlyStats().load(monthlyStatsTable);
		pp.getAllStats().load(allStatsTable);

		return pp;
	}
	
	//

	@SneakyThrows(SQLException.class)
	private List<Stats> getRanking(SQLTable table) {
		List<Stats> list = new ArrayList<>();

		@Cleanup PreparedStatement state = table.select("*", "order by (" + PVPStatsAPI.getRankingFormula() + ") desc");
		@Cleanup ResultSet result = state.executeQuery();

		while(result.next()) {
			Stats s = new Stats(PlayerKey.getDummy(result.getInt("player_id")));
			s.load(result);

			list.add(s);
		}

		return list;
	}

	public List<Stats> getDailyRanking() {
		return getRanking(dailyStatsTable);
	}

	public List<Stats> getWeeklyRanking() {
		return getRanking(weeklyStatsTable);
	}

	public List<Stats> getMonthlyRanking() {
		return getRanking(monthlyStatsTable);
	}

	public List<Stats> getAllRanking() {
		return getRanking(allStatsTable);
	}

	@SneakyThrows(SQLException.class)
	private HashMap<PlayerKey, String> getRankingPrefix(SQLTable table) {
		HashMap<PlayerKey, String> map = new HashMap<>();

		@Cleanup PreparedStatement state = table.select("*");
		@Cleanup ResultSet result = state.executeQuery();
		while(result.next()) {
			map.put(PlayerKey.getDummy(result.getInt("player_id")), result.getString("prefix"));
		}

		return map;
	}

	public HashMap<PlayerKey, String> getDailyRankingPrefix() {
		return getRankingPrefix(dailyStatsTable);
	}

	public HashMap<PlayerKey, String> getWeeklyRankingPrefix() {
		return getRankingPrefix(weeklyStatsTable);
	}

	public HashMap<PlayerKey, String> getMonthlyRankingPrefix() {
		return getRankingPrefix(monthlyStatsTable);
	}

	public HashMap<PlayerKey, String> getAllRankingPrefix() {
		return getRankingPrefix(allStatsTable);
	}

	//
	
	public void writePVPLog(PlayerKey killerPlayerKey, PlayerKey deadPlayerKey) {
		PVPLogTable.insert(killerPlayerKey == null ? -1 : killerPlayerKey, deadPlayerKey, System.currentTimeMillis());
	}
	
}