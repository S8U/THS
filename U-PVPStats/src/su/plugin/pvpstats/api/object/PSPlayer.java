package su.plugin.pvpstats.api.object;

import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.pvpstats.PVPStatsPlugin;
import su.plugin.pvpstats.api.PVPStatsAPI;

@Getter
public class PSPlayer {
	
	private final PlayerKey playerKey;
	
	@Setter
	private Stats dailyStats, weeklyStats, monthlyStats, allStats;

	@Setter
	private HashMap<PlayerKey, Long> lastHitTimes = new HashMap<>();

	public PSPlayer(PlayerKey playerKey) {
		this.playerKey = playerKey;
		dailyStats = new Stats(playerKey);
		weeklyStats = new Stats(playerKey);
		monthlyStats = new Stats(playerKey);
		allStats = new Stats(playerKey);
	}

	public Player getBukkitPlayer() {
		return (Player) playerKey.getPlatformPlayer();
	}
	
	public boolean isOnline() {
		return getBukkitPlayer() != null;
	}
	
	public void addKillCount() {
		dailyStats.addKillCount();
		weeklyStats.addKillCount();
		monthlyStats.addKillCount();
		allStats.addKillCount();
	}
	
	public void addDeathCount() {
		dailyStats.addDeathCount();
		weeklyStats.addDeathCount();
		monthlyStats.addDeathCount();
		allStats.addDeathCount();
	}
	
	public void addAssistCount() {
		dailyStats.addAssistCount();
		weeklyStats.addAssistCount();
		monthlyStats.addAssistCount();
		allStats.addAssistCount();
	}

	public void addWinCount() {
		dailyStats.addWinCount();
		weeklyStats.addWinCount();
		monthlyStats.addWinCount();
		allStats.addWinCount();
	}

	public void addQuitCount() {
		dailyStats.addQuitCount();
		weeklyStats.addQuitCount();
		monthlyStats.addQuitCount();
		allStats.addQuitCount();
	}
	
	public void addKillStreak() {
		dailyStats.addKillStreak();
		weeklyStats.addKillStreak();
		monthlyStats.addKillStreak();
		allStats.addKillStreak();
	}

	public void addDeathStreak() {
		dailyStats.addDeathStreak();
		weeklyStats.addDeathStreak();
		monthlyStats.addDeathStreak();
		allStats.addDeathStreak();
	}

	public void addWinStreak() {
		dailyStats.addWinStreak();
		weeklyStats.addWinStreak();
		monthlyStats.addWinStreak();
		allStats.addWinStreak();
	}

	public void setKillStreak(int count) {
		dailyStats.setKillStreak(count);
		weeklyStats.setKillStreak(count);
		monthlyStats.setKillStreak(count);
		allStats.setKillStreak(count);
	}

	public void setDeathStreak(int count) {
		dailyStats.setDeathStreak(count);
		weeklyStats.setDeathStreak(count);
		monthlyStats.setDeathStreak(count);
		allStats.setDeathStreak(count);
	}

	public void setWinStreak(int count) {
		dailyStats.setWinStreak(count);
		weeklyStats.setWinStreak(count);
		monthlyStats.setWinStreak(count);
		allStats.setWinStreak(count);
	}

	public void setLastHitTime(PlayerKey playerKey, long hitTime) {
		lastHitTimes.put(playerKey, hitTime);
	}
	
	public Long getLastHitTime(PlayerKey playerKey) {
		return lastHitTimes.get(playerKey);
	}

	public void savePlayer() {
		PVPStatsAPI.getSQLManager().savePlayer(this);
	}

	public void savePlayerAsynchronously() {
		Bukkit.getScheduler().runTaskAsynchronously(PVPStatsPlugin.getInstance(), () -> PVPStatsAPI.getSQLManager().savePlayer(this));
	}
	
}