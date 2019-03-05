package su.plugin.pvpstats.api;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import lombok.Getter;
import lombok.Setter;
import su.plugin.core.bukkit.api.util.KStringUtil;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.plugin.UPlugin;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.pvpstats.api.manager.PlayerManager;
import su.plugin.pvpstats.api.manager.RankingManager;
import su.plugin.pvpstats.api.manager.RankingPrefixManager;
import su.plugin.pvpstats.api.manager.SQLManager;
import su.plugin.pvpstats.api.object.PSPlayer;
import su.plugin.pvpstats.task.StatsInitTask;

public class PVPStatsAPI {

	@Setter
	@Getter
	private static boolean usePrefixer, useHolographicDisplays, useAbility,
			includeNatureDeath, writePVPStats, giveDailyRankingPrefix, giveWeeklyRankingPrefix, giveMonthlyRankingPrefix, giveAllRankingPrefix;

	@Setter
	@Getter
	private static int assistEffectiveTime;

	@Setter
	@Getter
	private static String rankingFormula;

	@Setter
	@Getter
	private static Timer statsInitTimer;

	@Getter
	private static PlayerManager playerManager;
	@Getter
	private static RankingManager rankingManager;
	@Getter
	private static RankingPrefixManager rankingPrefixManager;
	@Getter
	private static SQLManager SQLManager;

	public void init() {
		playerManager = new PlayerManager();
		rankingManager = new RankingManager();
		rankingPrefixManager = new RankingPrefixManager();
		SQLManager = new SQLManager();
	}

	public void registerPlugins() {
		if(usePrefixer = PluginUtil.existsPlugin("U-Prefixer")) {
			Core.log("U-Prefixer 플러그인과 연동되었습니다.");
		}
		if(useAbility = PluginUtil.existsPlugin("U-Ability")) {
			Core.log("U-Ability 플러그인과 연동되었습니다.");
		}
		if(useHolographicDisplays = PluginUtil.existsPlugin("HolographicDisplays")) {
			Core.log("HolographicDisplays 플러그인과 연동되었습니다.");
		}
	}

	public void loadConfig(UPlugin plugin) {
		plugin.getJsonConfig().addDefault("전적 기록", true);
		plugin.getJsonConfig().addDefault("자연사 데스 추가", true);
		plugin.getJsonConfig().addDefault("어시스트 시간(s)", 10);

		plugin.getJsonConfig().addDefault("랭킹.계산식", "kill_count + assist_count / 2 - death_count");

		plugin.getJsonConfig().addDefault("랭킹.업데이트 시간", Arrays.asList("0 0 0"));

		writePVPStats = plugin.getJsonConfig().getBoolean("전적 기록", true);
		includeNatureDeath = plugin.getJsonConfig().getBoolean("자연사 데스 추가");
		assistEffectiveTime = plugin.getJsonConfig().getInt("어시스트 시간(s)");

		rankingFormula = plugin.getJsonConfig().getString("랭킹.계산식");

		rankingManager.getRankingUpdateTimes().clear();
		for(String time : plugin.getJsonConfig().getStringList("랭킹.업데이트 시간")) {
			String[] timeSplit = time.split(" ");
			rankingManager.getRankingUpdateTimes().add(new byte[]{Byte.parseByte(timeSplit[0]), Byte.parseByte(timeSplit[1]), Byte.parseByte(timeSplit[2])});
		}

		if(useHolographicDisplays) {
			String loc = plugin.getJsonConfig().getString("랭킹.홀로그램 위치");
			if(loc != null) {
				rankingManager.setHologramLocation(KStringUtil.stringToLocation(loc));
			}
		}

		//

		if(usePrefixer) {
			plugin.getJsonConfig().addDefault("일간 랭킹 칭호 지급", false);
			plugin.getJsonConfig().addDefault("주간 랭킹 칭호 지급", false);
			plugin.getJsonConfig().addDefault("월간 랭킹 칭호 지급", false);
			plugin.getJsonConfig().addDefault("전체 랭킹 칭호 지급", false);

			plugin.getJsonConfig().addDefault("일간 랭킹 칭호", Arrays.asList("1~10 [{ranking}위]"));
			plugin.getJsonConfig().addDefault("주간 랭킹 칭호", Arrays.asList("1~10 [{ranking}위]"));
			plugin.getJsonConfig().addDefault("월간 랭킹 칭호", Arrays.asList("1~10 [{ranking}위]"));
			plugin.getJsonConfig().addDefault("전체 랭킹 칭호", Arrays.asList("1~10 [{ranking}위]"));

			giveDailyRankingPrefix = plugin.getJsonConfig().getBoolean("일간 랭킹 칭호 지급");
			giveDailyRankingPrefix = plugin.getJsonConfig().getBoolean("일간 랭킹 칭호 지급");
			giveDailyRankingPrefix = plugin.getJsonConfig().getBoolean("일간 랭킹 칭호 지급");
			giveDailyRankingPrefix = plugin.getJsonConfig().getBoolean("일간 랭킹 칭호 지급");

			rankingPrefixManager.getDailyRankingPrefixes().clear();
			rankingPrefixManager.getWeeklyRankingPrefixes().clear();
			rankingPrefixManager.getMonthlyRankingPrefixes().clear();
			rankingPrefixManager.getAllRankingPrefixes().clear();
			plugin.getJsonConfig().getStringList("일간 랭킹 칭호").forEach(line -> rankingPrefixManager.getDailyRankingPrefixes().put(line.substring(0, line.indexOf(" ")), line.substring(line.indexOf(" "))));
			plugin.getJsonConfig().getStringList("주간 랭킹 칭호").forEach(line -> rankingPrefixManager.getWeeklyRankingPrefixes().put(line.substring(0, line.indexOf(" ")), line.substring(line.indexOf(" "))));
			plugin.getJsonConfig().getStringList("월간 랭킹 칭호").forEach(line -> rankingPrefixManager.getMonthlyRankingPrefixes().put(line.substring(0, line.indexOf(" ")), line.substring(line.indexOf(" "))));
			plugin.getJsonConfig().getStringList("전체 랭킹 칭호").forEach(line -> rankingPrefixManager.getAllRankingPrefixes().put(line.substring(0, line.indexOf(" ")), line.substring(line.indexOf(" "))));
		}

		plugin.getJsonConfig().saveDefaults();
	}

	public static void initAnotherPeriodStats(boolean midNight) {
		if(SQLManager.getLastInit() == null) {
			if(midNight) {
				Calendar today = Calendar.getInstance();

				for(PSPlayer psp : playerManager.getPSPlayers().values()) {
					psp.getDailyStats().init();
				}

				if(SQLManager.isInitTable()) {
					SQLManager.getDailyStatsTable().delete();
				}

				Core.log("일간 전적을 초기화했습니다.");

				if(today.get(Calendar.DAY_OF_WEEK) == 2) {
					for(PSPlayer psp : playerManager.getPSPlayers().values()) {
						psp.getWeeklyStats().init();
					}

					if(SQLManager.isInitTable()) {
						SQLManager.getWeeklyStatsTable().delete();
					}

					Core.log("주간 전적을 초기화했습니다.");
				}

				if(today.get(Calendar.DAY_OF_MONTH) == 1) {
					for(PSPlayer psp : playerManager.getPSPlayers().values()) {
						psp.getMonthlyStats().init();
					}

					if(SQLManager.isInitTable()) {
						SQLManager.getMonthlyStatsTable().delete();
					}

					Core.log("월간 전적을 초기화했습니다.");
				}
			}

			SQLManager.updateLastInit();
			return;
		}

		Calendar today = Calendar.getInstance();
		Calendar last = Calendar.getInstance();
		last.setTime(SQLManager.getLastInit());

		int todayDate = today.get(Calendar.DATE);
		int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
		dayOfWeek = dayOfWeek == 1 ? 7 : dayOfWeek - 1;
		int firstDateOfWeek = todayDate - dayOfWeek + 1;
		int lastDateOfWeek = todayDate + (7 - dayOfWeek);

		Calendar fd = Calendar.getInstance(), ld = Calendar.getInstance();

		fd.set(Calendar.DATE, firstDateOfWeek);
		fd.set(Calendar.HOUR_OF_DAY, 0);
		fd.set(Calendar.MINUTE, 0);
		fd.set(Calendar.SECOND, 0);
		fd.set(Calendar.MILLISECOND, 0);

		ld.set(Calendar.DATE, lastDateOfWeek);
		ld.set(Calendar.HOUR_OF_DAY, 23);
		ld.set(Calendar.MINUTE, 59);
		ld.set(Calendar.SECOND, 59);
		ld.set(Calendar.MILLISECOND, 0);

		if(todayDate != last.get(Calendar.DATE)) {
			for(PSPlayer psp : playerManager.getPSPlayers().values()) {
				psp.getDailyStats().init();
			}

			if(SQLManager.isInitTable()) {
				SQLManager.getDailyStatsTable().delete();
			}

			Core.log("일간 전적을 초기화했습니다.");
		}

		if(last.before(fd) || last.after(ld)) {
			for(PSPlayer psp : playerManager.getPSPlayers().values()) {
				psp.getWeeklyStats().init();
			}

			if(SQLManager.isInitTable()) {
				SQLManager.getWeeklyStatsTable().delete();
			}

			Core.log("주간 전적을 초기화했습니다.");
		}

		if(today.get(Calendar.MONTH) != last.get(Calendar.MONTH)) {
			for(PSPlayer psp : playerManager.getPSPlayers().values()) {
				psp.getMonthlyStats().init();
			}

			if(SQLManager.isInitTable()) {
				SQLManager.getMonthlyStatsTable().delete();
			}

			Core.log("월간 전적을 초기화했습니다.");
		}

		SQLManager.updateLastInit();
	}

	public static void runStatsInitTimer() {
		if(statsInitTimer != null) {
			statsInitTimer.cancel();
		}

		statsInitTimer = new Timer();

		Calendar midNight = Calendar.getInstance();
		midNight.add(Calendar.DATE, 1);
		midNight.set(Calendar.HOUR_OF_DAY, 0);
		midNight.set(Calendar.MINUTE, 0);
		midNight.set(Calendar.SECOND, 0);
		midNight.set(Calendar.MILLISECOND, 0);

		long remainingTime = midNight.getTimeInMillis() - System.currentTimeMillis();
		statsInitTimer.schedule(new StatsInitTask(), remainingTime);

		Core.log(StringUtil.buildTimeString(remainingTime) + " 후 일부 기간 전적이 초기화됩니다.");
	}

	public void stopRankingUpdateTimer() {
		if(statsInitTimer == null) return;

		statsInitTimer.cancel();
	}

}