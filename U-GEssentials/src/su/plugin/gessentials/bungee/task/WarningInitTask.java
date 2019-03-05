package su.plugin.gessentials.bungee.task;

import su.plugin.gessentials.bungee.GGEssentialsPlugin;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.core.bungee.api.scheduler.UGRunnable;
import su.plugin.core.common.api.Core;

public class WarningInitTask extends UGRunnable {

	public WarningInitTask() {
		super(GGEssentialsPlugin.getInstance());
	}
	
	@Override
	public void run() {
		GGEssentialsAPI.getWarningManager().getWarnings().clear();
		GGEssentialsAPI.getSQLManager().initWarning();
		
		Core.log("경고가 초기화되었습니다.");
	}
	
}