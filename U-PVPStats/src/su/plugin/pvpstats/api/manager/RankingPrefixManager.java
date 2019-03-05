package su.plugin.pvpstats.api.manager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import lombok.Getter;
import lombok.Setter;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.prefixer.api.PrefixerAPI;
import su.plugin.prefixer.api.object.PrefixPlayer;
import su.plugin.pvpstats.api.PVPStatsAPI;

public class RankingPrefixManager {

	@Setter
	@Getter
	private LinkedHashMap<String, String> dailyRankingPrefixes = new LinkedHashMap<>(),
			weeklyRankingPrefixes = new LinkedHashMap<>(),
			monthlyRankingPrefixes = new LinkedHashMap<>(),
			allRankingPrefixes = new LinkedHashMap<>();

	public void updateDailyRankingPrefix() {
		if(PVPStatsAPI.getRankingManager().getDailyRankings().size() < 1) return;

		HashMap<PlayerKey, String> beforeData = PVPStatsAPI.getSQLManager().getDailyRankingPrefix();
		for(PlayerKey playerKey : beforeData.keySet()) {
			String beforePrefix = beforeData.get(playerKey);

			PrefixPlayer pfp = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
			if(pfp == null) {
				pfp.deletePrefix(beforePrefix);
			} else {
				PrefixerAPI.getSQLManager().deletePrefix(playerKey, beforePrefix);
			}
		}

		PVPStatsAPI.getSQLManager().getDailyRankingPrefixTable().delete();

		dailyRankingPrefixes.forEach((numberStr, prefix) -> {
			int min, max;
			if(numberStr.contains("~")) {
				String[] numberSplit = numberStr.split("~");
				min = Integer.parseInt(numberSplit[0]);
				max = Integer.parseInt(numberSplit[1]);
			} else {
				min = Integer.parseInt(numberStr);
				max = min;
			}

			for(; min <= max; min++) {
				if(PVPStatsAPI.getRankingManager().getDailyRankings().size() < min) break;
				PlayerKey playerKey = PVPStatsAPI.getRankingManager().getDailyRankingPlayer(min);
				String replacedPrefix = prefix.replace("{ranking}", min + "");

				PVPStatsAPI.getSQLManager().getDailyRankingPrefixTable().insert(playerKey, replacedPrefix);

				PrefixPlayer pfp = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
				if(pfp == null) {
					PrefixerAPI.getSQLManager().addPrefix(playerKey, replacedPrefix);
				} else {
					pfp.addPrefix(replacedPrefix);
				}
			}
		});
	}

	public void updateWeeklyRankingPrefix() {
		if(PVPStatsAPI.getRankingManager().getWeeklyRankings().size() < 1) return;

		HashMap<PlayerKey, String> beforeData = PVPStatsAPI.getSQLManager().getWeeklyRankingPrefix();
		for(PlayerKey playerKey : beforeData.keySet()) {
			String beforePrefix = beforeData.get(playerKey);

			PrefixPlayer pfp = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
			if(pfp == null) {
				pfp.deletePrefix(beforePrefix);
			} else {
				PrefixerAPI.getSQLManager().deletePrefix(playerKey, beforePrefix);
			}
		}

		PVPStatsAPI.getSQLManager().getWeeklyRankingPrefixTable().delete();

		weeklyRankingPrefixes.forEach((numberStr, prefix) -> {
			int min, max;
			if(numberStr.contains("~")) {
				String[] numberSplit = numberStr.split("~");
				min = Integer.parseInt(numberSplit[0]);
				max = Integer.parseInt(numberSplit[1]);
			} else {
				min = Integer.parseInt(numberStr);
				max = min;
			}

			for(; min <= max; min++) {
				if(PVPStatsAPI.getRankingManager().getWeeklyRankings().size() < min) break;
				PlayerKey playerKey = PVPStatsAPI.getRankingManager().getWeeklyRankingPlayer(min);
				String replacedPrefix = prefix.replace("{ranking}", min + "");

				PVPStatsAPI.getSQLManager().getWeeklyRankingPrefixTable().insert(playerKey, replacedPrefix);

				PrefixPlayer pfp = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
				if(pfp == null) {
					PrefixerAPI.getSQLManager().addPrefix(playerKey, replacedPrefix);
				} else {
					pfp.addPrefix(replacedPrefix);
				}
			}
		});
	}

	public void updateMonthlyRankingPrefix() {
		if(PVPStatsAPI.getRankingManager().getMonthlyRankings().size() < 1) return;

		HashMap<PlayerKey, String> beforeData = PVPStatsAPI.getSQLManager().getMonthlyRankingPrefix();
		for(PlayerKey playerKey : beforeData.keySet()) {
			String beforePrefix = beforeData.get(playerKey);

			PrefixPlayer pfp = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
			if(pfp == null) {
				pfp.deletePrefix(beforePrefix);
			} else {
				PrefixerAPI.getSQLManager().deletePrefix(playerKey, beforePrefix);
			}
		}

		PVPStatsAPI.getSQLManager().getMonthlyRankingPrefixTable().delete();

		monthlyRankingPrefixes.forEach((numberStr, prefix) -> {
			int min, max;
			if(numberStr.contains("~")) {
				String[] numberSplit = numberStr.split("~");
				min = Integer.parseInt(numberSplit[0]);
				max = Integer.parseInt(numberSplit[1]);
			} else {
				min = Integer.parseInt(numberStr);
				max = min;
			}

			for(; min <= max; min++) {
				if(PVPStatsAPI.getRankingManager().getMonthlyRankings().size() < min) break;
				PlayerKey playerKey = PVPStatsAPI.getRankingManager().getMonthlyRankingPlayer(min);
				String replacedPrefix = prefix.replace("{ranking}", min + "");

				PVPStatsAPI.getSQLManager().getMonthlyRankingPrefixTable().insert(playerKey, replacedPrefix);

				PrefixPlayer pfp = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
				if(pfp == null) {
					PrefixerAPI.getSQLManager().addPrefix(playerKey, replacedPrefix);
				} else {
					pfp.addPrefix(replacedPrefix);
				}
			}
		});
	}

	public void updateAllRankingPrefix() {
		if(PVPStatsAPI.getRankingManager().getAllRankings().size() < 1) return;

		HashMap<PlayerKey, String> beforeData = PVPStatsAPI.getSQLManager().getAllRankingPrefix();
		for(PlayerKey playerKey : beforeData.keySet()) {
			String beforePrefix = beforeData.get(playerKey);

			PrefixPlayer pfp = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
			if(pfp == null) {
				pfp.deletePrefix(beforePrefix);
			} else {
				PrefixerAPI.getSQLManager().deletePrefix(playerKey, beforePrefix);
			}
		}

		PVPStatsAPI.getSQLManager().getAllRankingPrefixTable().delete();

		allRankingPrefixes.forEach((numberStr, prefix) -> {
			int min, max;
			if(numberStr.contains("~")) {
				String[] numberSplit = numberStr.split("~");
				min = Integer.parseInt(numberSplit[0]);
				max = Integer.parseInt(numberSplit[1]);
			} else {
				min = Integer.parseInt(numberStr);
				max = min;
			}

			for(; min <= max; min++) {
				if(PVPStatsAPI.getRankingManager().getAllRankings().size() < min) break;
				PlayerKey playerKey = PVPStatsAPI.getRankingManager().getAllRankingPlayer(min);
				String replacedPrefix = prefix.replace("{ranking}", min + "");

				PVPStatsAPI.getSQLManager().getAllRankingPrefixTable().insert(playerKey, replacedPrefix);

				PrefixPlayer pfp = PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey);
				if(pfp == null) {
					PrefixerAPI.getSQLManager().addPrefix(playerKey, replacedPrefix);
				} else {
					pfp.addPrefix(replacedPrefix);
				}
			}
		});
	}
	
}