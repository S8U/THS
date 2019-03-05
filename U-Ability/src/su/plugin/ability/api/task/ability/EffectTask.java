package su.plugin.ability.api.task.ability;

import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.object.Ability;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;

public class EffectTask extends UKRunnable {
	
	private Ability ability = null;
	
	public EffectTask(Ability ability) {
		super(AbilityPlugin.getInstance());
		this.ability = ability;
	}
	
	public void run() {
		if(ability.getPlayer() == null) return;
		ability.onEffect();
	}
	
}
