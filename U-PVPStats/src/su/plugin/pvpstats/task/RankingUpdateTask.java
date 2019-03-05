package su.plugin.pvpstats.task;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.Core;
import su.plugin.pvpstats.PVPStatsPlugin;
import su.plugin.pvpstats.api.PVPStatsAPI;

public class RankingUpdateTask extends UKRunnable {

	@Setter
	@Getter
	private int count;

	public RankingUpdateTask() {
		super(PVPStatsPlugin.getInstance());

		this.count = count;
	}

	public void run() {
		count--;

		if(count < 1) {
			PVPStatsAPI.getRankingManager().updateRanking(Core.getUConsoleCommandSender());

			PVPStatsAPI.getRankingManager().updateRankingHologram(true);

			Bukkit.getScheduler().runTaskLaterAsynchronously(PVPStatsPlugin.getInstance(), () -> PVPStatsAPI.getRankingManager().runRankingUpdateTask(), 20);
		} else {
			PVPStatsAPI.getRankingManager().updateRankingHologram(false);
		}
	}
	
}