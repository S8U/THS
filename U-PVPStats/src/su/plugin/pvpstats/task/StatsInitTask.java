package su.plugin.pvpstats.task;

import java.util.TimerTask;
import su.plugin.pvpstats.api.PVPStatsAPI;

public class StatsInitTask extends TimerTask {

	public void run() {
		PVPStatsAPI.initAnotherPeriodStats(true);

		PVPStatsAPI.runStatsInitTimer();
	}
	
}