package su.plugin.ability.api.category;

import org.bukkit.ChatColor;

public enum AbilityRank {
	F(ChatColor.GRAY + "F"),
	E(ChatColor.GRAY + "E"),
	D(ChatColor.GRAY + "D"),
	C(ChatColor.YELLOW + "C"),
	B(ChatColor.BLUE + "B"),
	A(ChatColor.RED + "A"),
	S(ChatColor.AQUA + "S");
	
	private String text = null;
	
	private AbilityRank(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text + " 등급" + ChatColor.WHITE;
	}
}