package su.plugin.effect;

import lombok.Getter;
import su.plugin.core.bukkit.plugin.UBukkitPlugin;
import su.plugin.core.common.ChatColor;
import su.plugin.effect.api.EffectAPI;

public class EffectPlugin extends UBukkitPlugin {
	
	@Getter
	private static EffectPlugin instance;
	@Getter
	private static EffectAPI api;
	
	@Override
	public void onUEnable() {
		instance = this;
		api = new EffectAPI();
		
		setPrefix("§a[ U-Effect ]");
		setColor(ChatColor.GREEN);
	}
	
	@Override
	public void onUDisable() {
		
	}
	
}