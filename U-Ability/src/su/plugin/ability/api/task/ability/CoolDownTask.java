package su.plugin.ability.api.task.ability;

import org.bukkit.Sound;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.Ability;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;

public class CoolDownTask extends UKRunnable {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	private Ability ability = null;
	
	public CoolDownTask(Ability ability) {
		super(AbilityPlugin.getInstance());
		this.ability = ability;
	}
	
	public void run() {
		int cooltime = ability.getRemainingCoolTime();
		
		api.getBarManager().updateSideBar(ability.getGamePlayer());
		
		if(cooltime < 4) {
			if(cooltime < 1) {
				ability.onCoolDownEnd();
				api.playSound(ability.getPlayer(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
				Core.cmsg(ability.getPlayer(), ChatColor.GOLD, (ability.getGamePlayer().getAbilities().size() < 2 ? "" : ability.getName() + " ") + "§a능력을 다시 사용할 수 있습니다.");
				ability.stopCoolDownTask();
				return;
			}

			api.playSound(ability.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			Core.cmsg(ability.getPlayer(), ChatColor.GOLD, (ability.getGamePlayer().getAbilities().size() < 2 ? "" : ability.getName() + " ") + "§e능력 사용 가능 시간까지 §f" + StringUtil.buildTimeString(cooltime * 1000) + " §e남았습니다.");
		}
		
		ability.setRemainingCoolTime(cooltime - 1);
	}
	
}