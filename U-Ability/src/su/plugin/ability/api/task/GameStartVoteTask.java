package su.plugin.ability.api.task;

import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class GameStartVoteTask extends UKRunnable {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	public GameStartVoteTask() {
		super(AbilityPlugin.getInstance());
	}
	
	public void run() {
		if(api.getGameManager().isGameStarted()) return;
		
		Core.cbc(ChatColor.RED, "§c투표 시간이 초과하여 투표가 부결되었습니다.");

		api.getVoteManager().stopVote();
	}

}