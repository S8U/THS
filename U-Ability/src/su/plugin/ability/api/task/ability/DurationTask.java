package su.plugin.ability.api.task.ability;

import org.bukkit.Sound;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.Ability;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;

public class DurationTask extends UKRunnable {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	private Ability ability = null;
	
	public DurationTask(Ability ability) {
		super(AbilityPlugin.getInstance());
		this.ability = ability;
	}
	
	public void run() {
		ability.setRemainingDurationTime(ability.getRemainingDurationTime() - 1);

		int duration = ability.getRemainingDurationTime();

		api.getBarManager().updateSideBar(ability.getGamePlayer());

		if(ability.getPlayer() != null && duration < 4) {
			if(duration < 1) {
				api.playSound(ability.getPlayer(), Sound.ANVIL_BREAK, 1, 1);
				Core.cmsg(ability.getPlayer(), ChatColor.RED, (ability.getGamePlayer().getAbilities().size() < 2 ? "" : ability.getName() + " ") + "§c능력 사용이 종료되었습니다.");

				ability.stopDurationTask();
				return;
			}
			
			api.playSound(ability.getPlayer(), Sound.ORB_PICKUP, 1, 1);
			Core.cmsg(ability.getPlayer(), ChatColor.GOLD, (ability.getGamePlayer().getAbilities().size() < 2 ? "" : ability.getName() + " ") + "§c능력 지속 시간이 §f" + StringUtil.buildTimeString(duration * 1000) + " §c남았습니다.");
		}
	}
	
}
