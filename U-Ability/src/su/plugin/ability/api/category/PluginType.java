package su.plugin.ability.api.category;

import org.bukkit.ChatColor;

public enum PluginType {
	
	DEFAULT(ChatColor.DARK_GREEN + "U-Ability"),
	BITABILITY(ChatColor.BLUE + "BitAbility"),
	PHYSICALFIGHTERS(ChatColor.RED + "PhysicalFighters"),
	ABILITY_WAR(ChatColor.GOLD + "AbilityWar");
	
	private String text = null;
	
	private PluginType(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
}