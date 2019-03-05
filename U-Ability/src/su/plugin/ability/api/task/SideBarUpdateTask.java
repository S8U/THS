package su.plugin.ability.api.task;

import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;

public class SideBarUpdateTask extends UKRunnable {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	public SideBarUpdateTask() {
		super(AbilityPlugin.getInstance());
	}
	
	public void run() {
		api.getBarManager().updateSideBarAllPlayer();
	}
	
}