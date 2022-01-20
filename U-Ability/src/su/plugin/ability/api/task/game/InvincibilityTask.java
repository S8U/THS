package su.plugin.ability.api.task.game;

import lombok.Getter;
import org.bukkit.Sound;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;

public class InvincibilityTask extends UKRunnable {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Getter
	private int count, invincibilityCount;
	
	public InvincibilityTask() {
		super(AbilityPlugin.getInstance());
	}
	
	public void init(int invincibilityCount) {
		count = -1;
		this.invincibilityCount = invincibilityCount;
	}

	@Override
	public void run() {
		count++;
		
		api.getBarManager().updateSideBarAllPlayer();
		
		if(count == 0) {
			if(api.isInvincibilityTime()) {
				api.getTaskManager().stopInvincbilityTask();
				return;
			}
			api.setInvincibilityTime(true);
			Core.nbc(" ");
			Core.cbc(ChatColor.DARK_GREEN, "§e무적 시간이 시작되었습니다.");
			Core.cbc(ChatColor.DARK_GREEN, StringUtil.buildTimeString(getRemainingCount()  * 1000) +" §e동안 무적 효과가 지속됩니다.");
		} else {
			if(!api.isInvincibilityTime()) {
				api.getTaskManager().stopInvincbilityTask();
				return;
			}
			if(getRemainingCount() == 600 || getRemainingCount() == 300 || getRemainingCount() == 60) {
				Core.cbc(ChatColor.DARK_GREEN, "§e무적 해제까지 §f" + StringUtil.buildTimeString(getRemainingCount()  * 1000) + " §e남았습니다.");
			} else if(getRemainingCount() < 1) {
				api.setInvincibilityTime(false);
				api.playSoundToAll(Sound.EXPLODE, 1, 1);
				Core.cbc(ChatColor.DARK_GREEN, "§a무적이 해제되었습니다.");
				api.getBarManager().getBossBar().setText("무적이 해제되었습니다.");
				api.getBarManager().getBossBar().setProgress(100);
				api.getBarManager().getBossBar().startTimer(5);
				
				if(api.getGameManager().isAutoMode() && api.isUseAutoTeleport()) {
					api.getTaskManager().runTeleportAllTask(20 * 3, api.getAutoTeleportCount());
				}
				
				cancel();
				return;
			} else if(getRemainingCount() < 11) {
				api.playSoundToAll(Sound.ORB_PICKUP, 1, 1);
				Core.cbc(ChatColor.DARK_GREEN, getRemainingCount() + "§e초 후 무적이 해제됩니다.");
			}
		}
		
		api.getBarManager().getBossBar().setText("무적 해제까지 " + StringUtil.buildTimeString(getRemainingCount()  * 1000) + " 남았습니다.");
		api.getBarManager().getBossBar().setProgress(((float) getRemainingCount() / (float) invincibilityCount) * 100);
	}
	
	public int getRemainingCount() {
		return invincibilityCount - count;
	}

}