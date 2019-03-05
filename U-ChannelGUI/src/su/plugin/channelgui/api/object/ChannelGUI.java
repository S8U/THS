package su.plugin.channelgui.api.object;

import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import su.plugin.channelgui.api.category.CType;
import su.plugin.core.bukkit.api.gui.GUI;

@ToString
@Setter
@Getter
public class ChannelGUI extends GUI  {
	
	private String name, permission;
	
	private boolean playerAmountMenu;
	
	private int row = 1;

	private CType defaultType;

	private HashMap<String, Long> cooldown = new HashMap<>();

	//

	public ChannelGUI(String key, String title, int row) {
		super(key, title, row);
	}

	//

	public void addCooldown(String name, int cooldown) {
		this.cooldown.put(name.toLowerCase(), System.currentTimeMillis() + cooldown);
	}
	
	public void removeCooldown(String name) {
		cooldown.remove(name.toLowerCase());
	}
	
	public boolean hasCooldown(String name) {
		if(!cooldown.containsKey(name.toLowerCase())) return false;
		return cooldown.get(name.toLowerCase()) > System.currentTimeMillis();
	}


}
