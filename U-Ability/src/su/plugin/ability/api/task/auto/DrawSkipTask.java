package su.plugin.ability.api.task.auto;

import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;

public class DrawSkipTask extends UKRunnable {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	private int count, skipCount = api.getDrawSkipCount() + 1;
	
	private String alertMessage = " 후 능력이 강제로 확정됩니다.";
	private String skipMessage = "능력이 강제로 확정되었습니다.";
	
	public DrawSkipTask() {
		super(AbilityPlugin.getInstance());
	}
	
	@Override
	public void run() {
		count++;
		
		if(count >= skipCount) {
			for(GamePlayer gp : api.getPlayerManager().getOnlineJoinedPlayers()) {
				gp.setRedrawCount(0);
			}
			
			api.getBarManager().updateSideBarAllPlayer();
			
			Core.cbc(ChatColor.DARK_GREEN, "§c" + skipMessage);
			api.getBarManager().getBossBar().setText(skipMessage);
			api.getBarManager().getBossBar().setProgress(0);
			
			api.getTaskManager().runGameStartCountTask(20);
			
			cancel();
			return;
		}
		
		String msg = StringUtil.buildTimeString(getRemainingCount()  * 1000) + alertMessage;
		api.getBarManager().getBossBar().setText(msg);
		api.getBarManager().getBossBar().setProgress((float) 100 - (float) (count - 1) / (float) (skipCount - 1) * 100);
		if(count != 1) return;
		Core.cbc(ChatColor.DARK_GREEN, msg);
	}
	
	public int getRemainingCount() {
		return skipCount - count;
	}
	
}